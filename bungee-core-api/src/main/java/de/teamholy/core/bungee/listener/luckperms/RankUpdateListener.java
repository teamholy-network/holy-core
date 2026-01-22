package de.teamholy.core.bungee.listener.luckperms;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bungee.BungeeCore;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeMutateEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;

public class RankUpdateListener {

    public RankUpdateListener(LuckPerms luckPerms) {
        luckPerms.getEventBus().subscribe(NodeAddEvent.class, this::onRankAdd);
        luckPerms.getEventBus().subscribe(NodeRemoveEvent.class, this::onRankRemove);
    }

    private void onRankAdd(NodeAddEvent event) {
        handleRankUpdate(event, event.getNode());
    }

    private void onRankRemove(NodeRemoveEvent event) {
        handleRankUpdate(event, event.getNode());
    }

    private void handleRankUpdate(NodeMutateEvent event, Node node) {
        if (!event.isUser()) {
            return;
        }
        if (!(node instanceof InheritanceNode)) {
            return;
        }

        User user = (User) event.getTarget();
        if (user == null) {
            return;
        }
        Optional<Group> group = event.getDataAfter().stream()
                .filter(filterNode -> filterNode instanceof InheritanceNode)
                .map(inheritanceNode1 -> BungeeCore.getAPI().getRankManager().getGroup(((InheritanceNode) inheritanceNode1).getGroupName()))
                .sorted(Comparator.comparingInt(sortGroup -> ((Group) sortGroup).getWeight().orElseGet(() -> 0)).reversed()).findFirst();
        if (group.isEmpty()) {
            BungeeCore.getInstance().getPlayerColorCacheManager().put(user.getUniqueId(), PlayerRank.PLAYER.getColorCode());
            return;
        }
        Optional<PlayerRank> playerRank = Arrays.stream(PlayerRank.values())
                .filter(rank -> rank.getName().equalsIgnoreCase(group.get().getName())).findFirst();
        playerRank.ifPresent(rank -> BungeeCore.getInstance().getPlayerColorCacheManager().put(user.getUniqueId(),
                rank.getColorCode()));
    }

}
