package de.teamholy.core.api.manager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.platform.PlayerAdapter;

public class RankManager {

    private LuckPerms luckPerms;

    public RankManager() {

    }

    public void init() {
        this.luckPerms = null;
    }

    public void init(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    public CompletableFuture<User> getUser(UUID uuid) {
        return getOrLoadUser(uuid);
    }

    public <T> String getRank(PlayerAdapter<T> playerAdapter, T player) {
        provideLuckPermsAPI();
        User user = playerAdapter.getUser(player);
        if (user == null) {
            return null;
        }
        return user.getPrimaryGroup();
    }

    public CompletableFuture<Optional<InheritanceNode>> getPrimaryGroup(UUID uuid) {
        provideLuckPermsAPI();
        return CompletableFuture.supplyAsync(() -> {
            User user = luckPerms.getUserManager().getUser(uuid);
            if (user == null) {
                return luckPerms.getUserManager().loadUser(uuid).thenApply(this::getPrimaryGroup).join();
            }
            return getPrimaryGroup(user);
        });
    }

    public <T> Optional<InheritanceNode> getPrimaryGroup(PlayerAdapter<T> playerAdapter, T player) {
        provideLuckPermsAPI();
        User user = playerAdapter.getUser(player);
        if (user == null) {
            return Optional.empty();
        }
        return getPrimaryGroup(user);
    }

    public Optional<InheritanceNode> getPrimaryGroup(User user) {
        provideLuckPermsAPI();
        return user.getNodes(NodeType.INHERITANCE).stream()
                .filter(node -> node.getGroupName().equals(user.getPrimaryGroup())).findFirst();
    }

    public List<Group> getRanks() {
        provideLuckPermsAPI();
        return luckPerms.getGroupManager().getLoadedGroups().stream()
                .filter(group -> group.getCachedData().getMetaData().getPrefix() != null
                        && !group.getCachedData().getMetaData().getPrefix().isEmpty())
                .collect(Collectors.toList());
    }

    public CompletableFuture<Optional<List<InheritanceNode>>> getRanks(UUID uuid) {
        provideLuckPermsAPI();
        return getOrLoadUser(uuid).thenApply(this::getRanks);
    }

    public Optional<List<InheritanceNode>> getRanks(User user) {
        provideLuckPermsAPI();
        if (user == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(user.getNodes(NodeType.INHERITANCE).stream().filter(node -> {
            return luckPerms.getGroupManager().getGroup(node.getGroupName()).getCachedData().getMetaData()
                    .getPrefix() != null
                    && !luckPerms.getGroupManager().getGroup(node.getGroupName()).getCachedData().getMetaData()
                            .getPrefix().isEmpty();
        }).collect(Collectors.toList()));
    }

    public Group getGroup(String groupName) {
        provideLuckPermsAPI();
        return luckPerms.getGroupManager().getGroup(groupName);
    }

    public Optional<Group> getGroupByDisplayName(String displayName) {
        provideLuckPermsAPI();
        return luckPerms.getGroupManager().getLoadedGroups().stream()
                .filter(group -> displayName.equals(group.getDisplayName()))
                .findFirst()
                .map(Optional::of)
                .orElseGet(() -> Optional.ofNullable(luckPerms.getGroupManager().getGroup(displayName)));
    }

    public PrefixNode getPrefixNode(String groupName) {
        provideLuckPermsAPI();
        return luckPerms.getGroupManager().getGroup(groupName).getNodes(NodeType.PREFIX).stream().findFirst()
                .orElse(null);
    }

    public CompletableFuture<Boolean> setGroup(User user, Group group, long expiryDuration) {
        provideLuckPermsAPI();
        user.data().clear(NodeType.INHERITANCE::matches);
        InheritanceNode.Builder inheritanceNodeBuilder = InheritanceNode.builder(group);
        if (expiryDuration > 0) {
            inheritanceNodeBuilder.expiry(expiryDuration, TimeUnit.DAYS);
        }
        DataMutateResult dataMutateResult = user.data().add(inheritanceNodeBuilder.build());
        if (!dataMutateResult.wasSuccessful()) {
            return CompletableFuture.completedFuture(null);
        }
        return luckPerms.getUserManager().saveUser(user).thenApply(u -> true);
    }

    public CompletableFuture<Boolean> addGroup(User user, Group group, long expiryDuration) {
        provideLuckPermsAPI();
        InheritanceNode.Builder inheritanceNodeBuilder = InheritanceNode.builder(group);
        if (expiryDuration > 0) {
            Optional<InheritanceNode> inheritanceNode = user.getNodes(NodeType.INHERITANCE).stream()
                    .filter(node -> node.getGroupName().equals(group.getName())).findFirst();
            Long lastExpiryDuration = inheritanceNode.map((node) -> node.getExpiryDuration().toMillis())
                    .orElseGet(() -> 0L);
            Long newExpiryDuration = lastExpiryDuration + TimeUnit.DAYS.toMillis(expiryDuration);
            inheritanceNodeBuilder.expiry(newExpiryDuration, TimeUnit.MILLISECONDS);
        }
        DataMutateResult dataMutateResult = user.data().add(inheritanceNodeBuilder.build());
        if (!dataMutateResult.wasSuccessful()) {
            return CompletableFuture.completedFuture(null);
        }
        return luckPerms.getUserManager().saveUser(user).thenApply(u -> true);
    }

    public String getRankDisplay(Group group) {
        String color = group.getCachedData().getMetaData().getMetaValue("color");
        if (color == null) {
            color = "§7";
        }
        color = color.replaceAll("&", "§");
        return color + (group.getDisplayName() != null ? group.getDisplayName() : group.getName());
    }

    private CompletableFuture<User> getOrLoadUser(UUID uuid) {
        provideLuckPermsAPI();
        return CompletableFuture.supplyAsync(() -> {
            User user = luckPerms.getUserManager().getUser(uuid);
            if (user == null) {
                return luckPerms.getUserManager().loadUser(uuid).join();
            }
            return user;
        });
    }

    public LuckPerms getLuckPerms() {
        provideLuckPermsAPI();
        return this.luckPerms;
    }

    private void provideLuckPermsAPI() {
        if (this.luckPerms == null) {
            this.luckPerms = LuckPermsProvider.get();
        }
    }

}
