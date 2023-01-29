package de.teamholy.core.bukkit.perks;

import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/* copyright by Yassino */
public class PerkManager {

    private ItemBuilder getPerk(Player player, PerkType perkType) {
        PerkPlayerProfile perkPlayerProfile = BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().get(player.getUniqueId());
        Perk perk;
        ItemBuilder itemBuilder = null;
        if (perkType == PerkType.STICK) {
            perk = BukkitCore.getInstance().getPerkCache().getPerkHashMap().get(perkPlayerProfile.getStickPerk());
            itemBuilder = new ItemBuilder(perk.getMaterial(),1,perk.getSubId());

            if (perk.isBanner()) {
                itemBuilder.setBannerMeta(perk.getBaseColor(),perk.getPatterns());
            }

        } else if (perkType == PerkType.BLOCK) {
            perk = BukkitCore.getInstance().getPerkCache().getPerkHashMap().get(perkPlayerProfile.getBlockPerk());
            itemBuilder = new ItemBuilder(perk.getMaterial(),1,perk.getSubId());
        }

        return itemBuilder;
    }

    public void openMainPerkInventory(Player player) {
        Inventory inventory = new Inventory("§8» §6Perks Type",9);
        for (int i = 0; i < 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE,1,(byte) 15).setName("§8//").build(),i);
        }

        inventory.setItem(new ItemBuilder(Material.STICK,1).setName("§8» §6Stick").build(),2,(event) ->
                openSecondPerkInventory(player,PerkType.STICK,SortOptionPerk.NORMAL,SortOptionPlayer.ALL));

        inventory.setItem(new ItemBuilder(Material.PAPER,1).setName("§8» §6Chat").build(),4,(event) ->
                openSecondPerkInventory(player,PerkType.CHAT,SortOptionPerk.NORMAL,SortOptionPlayer.ALL));

        inventory.setItem(new ItemBuilder(Material.SANDSTONE,1).setName("§8» §6Block").build(),6,(event) ->
                openSecondPerkInventory(player,PerkType.BLOCK,SortOptionPerk.NORMAL,SortOptionPlayer.ALL));

        player.openInventory(inventory.getInventory());
    }

    public void openSecondPerkInventory(Player player, PerkType perkType, PerkManager.SortOptionPerk sortOptionPerk, PerkManager.SortOptionPlayer sortOptionPlayer) {
        int inventorySize = checkInventorySize((int) BukkitCore.getInstance().getPerkCache().getPerkHashMap().values().stream().filter(perk -> perk.getPerkType() == perkType).count()) + 9;
        Inventory inventory = new Inventory("§8» §6Perks",inventorySize);


        for (int i = 0; i < inventorySize; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        for (int i = inventorySize - 9; i < inventorySize; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 8).setName("§8//").build(), i);
        }


        ItemBuilder sortPerk = new ItemBuilder(Material.HOPPER).setName("§8» §6Sort");
        ItemBuilder sortPlayer = new ItemBuilder(Material.DIAMOND).setName("§8» §6Filter");

        sortPerk.setLore(Arrays.stream(SortOptionPerk.values())
                .map(value -> (sortOptionPerk == value) ? "§a" + value.toString().toLowerCase(Locale.ROOT) : "§7" + value.toString().toLowerCase(Locale.ROOT))
                .collect(Collectors.toList()));

        sortPlayer.setLore(Arrays.stream(SortOptionPlayer.values())
                .map(value -> (sortOptionPlayer == value) ? "§a" + value.toString().toLowerCase(Locale.ROOT) : "§7" + value.toString().toLowerCase(Locale.ROOT))
                .collect(Collectors.toList()));


        // sort perk
        inventory.setItem(sortPerk.build(),(inventorySize - 9) + 2, event -> {

            if (sortOptionPerk == SortOptionPerk.NORMAL) {
                openSecondPerkInventory(player,perkType,SortOptionPerk.COINS,sortOptionPlayer);
            } if (sortOptionPerk == SortOptionPerk.COINS) {
                openSecondPerkInventory(player,perkType,SortOptionPerk.RANK,sortOptionPlayer);
            }if (sortOptionPerk == SortOptionPerk.RANK) {
                openSecondPerkInventory(player,perkType,SortOptionPerk.NORMAL,sortOptionPlayer);
            }

        });

        // sort player perk
        inventory.setItem(sortPlayer.build(),(inventorySize - 9) + 6, event -> {
            if (sortOptionPlayer == SortOptionPlayer.ALL) {
                openSecondPerkInventory(player,perkType,sortOptionPerk,SortOptionPlayer.OWNED);
            } if (sortOptionPlayer == SortOptionPlayer.OWNED) {
                openSecondPerkInventory(player,perkType,sortOptionPerk,SortOptionPlayer.UNOWNED);
            }if (sortOptionPlayer == SortOptionPlayer.UNOWNED) {
                openSecondPerkInventory(player,perkType,sortOptionPerk,SortOptionPlayer.ALL);
            }
        });

        showPerks(player,perkType,sortOptionPerk,sortOptionPlayer, inventory);

        player.openInventory(inventory.getInventory());
    }

    private void showPerks(Player player, PerkType perkType, PerkManager.SortOptionPerk sortOptionPerk, PerkManager.SortOptionPlayer sortOptionPlayer, Inventory inventory) {
        PerkPlayerProfile perkPlayerProfile = BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().get(player.getUniqueId());

        List<Perk> perks = new ArrayList<>();
        BukkitCore.getInstance().getPerkCache().getPerkHashMap().values().stream().filter(perk -> perk.getPerkType() == perkType).collect(Collectors.toList()).addAll(perks);


        BukkitCore.getInstance().getPerkCache().getPerkHashMap().values().stream().filter(perk -> switch (sortOptionPlayer) {
            case OWNED -> (perk.isBuyAble() && perkPlayerProfile.getOwnedPerks().contains(perk.getId())) || (!perk.isBuyAble() && player.hasPermission(perk.getPerkRankType().getPermission()));
            case UNOWNED -> (perk.isBuyAble() && !perkPlayerProfile.getOwnedPerks().contains(perk.getId())) || (!perk.isBuyAble() && !player.hasPermission(perk.getPerkRankType().getPermission()));
            default -> true;
        }).collect(Collectors.toList()).addAll(perks);



        if (sortOptionPerk == SortOptionPerk.RANK) {
            perks.removeIf(Perk::isBuyAble);
            List<PerkRankType> rankOrder = Arrays.asList(PerkRankType.PLAYER, PerkRankType.PREMIUM, PerkRankType.VIP, PerkRankType.HOLY);
            perks.sort(Comparator.comparingInt(p -> rankOrder.indexOf(p.getPerkRankType())));
        } else if (sortOptionPerk == SortOptionPerk.COINS) {
            perks.removeIf(perk -> !perk.isBuyAble());
            perks.sort(Comparator.comparing(Perk::getPrice));
        } else  {
            perks.sort(Comparator.comparing(Perk::getId));
        }


        int i = 0;
        for (Perk perk : perks) {
            String name = perk.getName();

            if (perkType == PerkType.CHAT) {
                String[] temp = perk.getName().split("-");
                name = temp[0] + temp[1];
            }


            ItemBuilder itemBuilder = new ItemBuilder(perk.getMaterial(),1,perk.getSubId()).setName(name);
            if (perk.isBanner()) {
                itemBuilder.setBannerMeta(perk.getBaseColor(),perk.getPatterns());
            }

            List<String> list = new ArrayList<>();
            if (perk.isBuyAble()) {
                list.add("§7This perk costs §e" + perk.getPrice() + " §6coins");
            } else {
                list.add("§7Available for " + perk.getPerkRankType().getRankName() + "§7 and above");
            }
            if (perkPlayerProfile.getOwnedPerks().contains(perk.getId()) && perk.isBuyAble() || !perk.isBuyAble() && player.hasPermission(perk.getPerkRankType().getPermission())) {
                list.add("§ayou own this perk, click to select");
            }

            if (perkPlayerProfile.getChatPerk() == perk.getId() || perkPlayerProfile.getBlockPerk() == perk.getId() || perkPlayerProfile.getStickPerk() == perk.getId()) {
                itemBuilder.setLore("§2selected");
                itemBuilder.setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL,1).setAttributs();
            } else {
                itemBuilder.setLore(list);
            }


            String finalName = name;
            inventory.setItem(itemBuilder.build(),i, event -> {

                if (perk.isBuyAble()) {
                    if (perkPlayerProfile.getOwnedPerks().contains(perk.getId())) {
                    } else {
                        buyPerk(player,perkPlayerProfile,perk, finalName);
                        return;
                    }
                } else {
                    if (!player.hasPermission(perk.getPerkRankType().getPermission())) return;
                }


                if (perkType == PerkType.STICK) {
                    perkPlayerProfile.setStickPerk(perk.getId());
                } else if (perkType == PerkType.BLOCK) {
                    perkPlayerProfile.setBlockPerk(perk.getId());
                } else if (perkType == PerkType.CHAT) {
                    perkPlayerProfile.setChatPerk(perk.getId());
                }
                player.closeInventory();
                player.sendMessage("§7You selected the §e" + finalName + " §6perk!");
                player.playSound(player.getLocation(), Sound.NOTE_PLING,2f,2f);

                BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().put(player.getUniqueId(),perkPlayerProfile);
                BukkitCore.getAPI().getPerkPlayerService().saveEntity(perkPlayerProfile,true,false);

            });

            i++;
        }


    }


    private void buyPerk(Player player,PerkPlayerProfile perkPlayerProfile, Perk perk, String name) {
        Inventory inventory = new Inventory("§8» §6Perks",9);


        for (int i = 0; i < 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        ItemBuilder itemBuilder = new ItemBuilder(perk.getMaterial(),1,perk.getSubId());

        if (perk.isBanner()) {
            itemBuilder.setBannerMeta(perk.getBaseColor(),perk.getPatterns());
        }

        itemBuilder.setName(name);
        List<String> list = new ArrayList<>();
        list.add("");
        list.add("§7Do you want to buy this perk?");
        list.add("§7price §8» §e" + perk.getPrice());
        list.add("");
        if (perk.getNotSupportedGamemodes() != null) {
            list.add("§c§lNOTE §7this perk isn't supported in§8:");
            perk.getNotSupportedGamemodes().forEach(gamemodes -> list.add(" " + gamemodes.getColor() + gamemodes.toString().toLowerCase(Locale.ROOT)));
        }

        inventory.setItem(new ItemBuilder(Material.INK_SACK, 1, (byte) 10).setName("§8» §aYes").build(), 6, event -> {
            player.closeInventory();
            PlayerProfile playerProfile = BukkitCore.getAPI().getPlayerService().getEntity(player.getUniqueId(),() -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId()));
            if (!(playerProfile.getCoins() >= perk.getPrice())) {
                player.sendMessage("§cYou dont have enough coins!");
                player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 50f, 50f);
                return;
            }
            playerProfile.setCoins(playerProfile.getCoins() - perk.getPrice());
            player.sendMessage("§aYou successfully bought the §e" + perk.getName() + " §7perk for §a" + perk.getPrice() + " §6coins!");

            perkPlayerProfile.getOwnedPerks().add(perk.getId());
            BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().put(player.getUniqueId(),perkPlayerProfile);
            BukkitCore.getAPI().getPerkPlayerService().saveEntity(perkPlayerProfile,true,true);
            BukkitCore.getAPI().getPlayerService().saveEntity(playerProfile,true,true);
        });


        inventory.setItem(new ItemBuilder(Material.INK_SACK, 1, (byte) 1).setName("§8» §cNo").build(), 6, event -> {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 50f, 50f);
            player.sendMessage("§cAborted!");
        });

        itemBuilder.setLore(list);
        player.openInventory(inventory.getInventory());
    }



    private int checkInventorySize(int items) {
        int size = 0;
        if (items <= 9) {
            size = 9;
        } else if (items <= 18) {
            size = 18;
        } else if (items <= 27) {
            size = 27;
        } else if (items <= 36) {
            size = 36;
        } else if (items <= 45) {
            size = 45;
        } else {
            size = 54;
        }
        return size;
    }



    enum SortOptionPerk {
        NORMAL, COINS, RANK;
    }

    enum SortOptionPlayer {
        ALL, OWNED, UNOWNED;
    }

}
