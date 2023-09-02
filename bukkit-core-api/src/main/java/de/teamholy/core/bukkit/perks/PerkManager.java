package de.teamholy.core.bukkit.perks;

import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.manager.CustomBannerManager;
import de.teamholy.core.bukkit.utils.Inventory;
import de.teamholy.core.bukkit.utils.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/* copyright by Yassino */
public class PerkManager {

    private String prefix = "§6Perks§8× §7";

    private List<Pattern> patterns;

    private CustomBannerManager customBannerManager;

    public PerkManager(BukkitCore bukkitCore) {
        this.customBannerManager = new CustomBannerManager(bukkitCore);
    }

    public ItemBuilder getPerk(Player player, PerkType perkType) {
        PerkPlayerProfile perkPlayerProfile = BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().get(player.getUniqueId());
        Perk perk;
        ItemBuilder itemBuilder = null;
        if (perkPlayerProfile == null) return null;


        if (perkType == PerkType.STICK) {
            perk = BukkitCore.getInstance().getPerkCache().getPerkHashMap().get(perkPlayerProfile.getStickPerk());

            if (perk == null) {
                perk = BukkitCore.getInstance().getPerkCache().getPerkHashMap().get(100);
            } else if (perk.getNotSupportedGamemodes() != null) {
                for (Gamemodes notSupportedGamemode : perk.getNotSupportedGamemodes()) {
                    if (notSupportedGamemode.getCloudGroups().contains(BukkitCore.getInstance().getGroup())) {
                        perk = BukkitCore.getInstance().getPerkCache().getPerkHashMap().get(100);
                    }
                }
            }
            itemBuilder = new ItemBuilder(perk.getMaterial(), 1, perk.getSubId());

            if (perk.isBanner()) {
                itemBuilder.setBannerMeta(perk.getBaseColor(), perk.getPatterns()).setAttribut(ItemFlag.HIDE_POTION_EFFECTS);
            }

        } else if (perkType == PerkType.BLOCK) {
            perk = BukkitCore.getInstance().getPerkCache().getPerkHashMap().get(perkPlayerProfile.getBlockPerk());
            if (perk == null) {
                perk = BukkitCore.getInstance().getPerkCache().getPerkHashMap().get(0);
            } else if (perk.getNotSupportedGamemodes() != null) {
                for (Gamemodes notSupportedGamemode : perk.getNotSupportedGamemodes()) {
                    if (notSupportedGamemode.getCloudGroups().contains(BukkitCore.getInstance().getGroup())) {
                        perk = BukkitCore.getInstance().getPerkCache().getPerkHashMap().get(0);
                    }
                }
            }

            itemBuilder = new ItemBuilder(perk.getMaterial(), 1, perk.getSubId());
        }

        return itemBuilder;
    }

    public void openMainPerkInventory(Player player) {
        Inventory inventory = new Inventory("§8» §6Perks Type", 9);
        for (int i = 0; i < 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        inventory.setItem(new ItemBuilder(Material.STICK, 1).setName("§8» §6Stick").build(), 1, (event) ->
            openSecondPerkInventory(player, PerkType.STICK, SortOptionPerk.NORMAL, SortOptionPlayer.ALL));

        inventory.setItem(new ItemBuilder(Material.PAPER, 1).setName("§8» §6Chat").build(), 3, (event) ->
            openSecondPerkInventory(player, PerkType.CHAT, SortOptionPerk.NORMAL, SortOptionPlayer.ALL));

        inventory.setItem(new ItemBuilder(Material.SANDSTONE, 1).setName("§8» §6Block").build(), 5, (event) ->
            openSecondPerkInventory(player, PerkType.BLOCK, SortOptionPerk.NORMAL, SortOptionPlayer.ALL));



        PerkPlayerProfile perkPlayerProfile = BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().get(player.getUniqueId());

        String dieLore = perkPlayerProfile.getCustomBanner().isActivated() ? "§l§aselected" : "§7Click to §l§aselect";


        inventory.setItem(new ItemBuilder(Material.BANNER, 1).setName("§8» §6Custom Head Banner")
                .setLore(" " , " §7A custom banner on your head ", " §7with your own design! " , " §7you can change them ", " §7on the §6§lwebsite! ", " §7(§ehttps://teamholy.de/profile/" + player.getDisplayName() + "§7)", " ",
                    ( perkPlayerProfile.getOwnedPerks().contains(99999) ? dieLore : "§7This perk costs §e10000 §6coins")
                    )
            .setBannerMeta(DyeColor.WHITE, new ArrayList<>()).build(), 7, (event) -> {

            PlayerProfile playerProfile = BukkitCore.getAPI().getPlayerService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId()));

            if (perkPlayerProfile.getOwnedPerks().contains(99999)) {

                if (!perkPlayerProfile.getCustomBanner().isActivated()) {

                    player.sendMessage(prefix + "§7You successfully activated the §eCustom Banner §7perk!");
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 2f, 2f);
                    perkPlayerProfile.getCustomBanner().setActivated(true);
                    customBannerManager.setAndPlaceCustomBanner(player, perkPlayerProfile.getCustomBanner().getBaseColor());
                } else {

                    player.sendMessage(prefix + "§7You successfully deactivated the §eCustom Banner §7perk!");
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 2f, 2f);
                    perkPlayerProfile.getCustomBanner().setActivated(false);
                    customBannerManager.removeCustomBanner(player);
                }

                BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().put(player.getUniqueId(), perkPlayerProfile);
                BukkitCore.getAPI().getPerkPlayerService().saveEntity(perkPlayerProfile, true, true);
                player.closeInventory();
            } else {
                if (!(playerProfile.getCoins() >= 10000)) {
                    player.sendMessage(prefix + "§cYou dont have enough coins!");
                    player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 2f, 2f);
                    return;
                }

                perkPlayerProfile.getOwnedPerks().add(99999);
                playerProfile.setCoins(playerProfile.getCoins() - 10000);
                BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().put(player.getUniqueId(), perkPlayerProfile);
                BukkitCore.getAPI().getPerkPlayerService().saveEntity(perkPlayerProfile, true, true);
                BukkitCore.getAPI().getPlayerService().saveEntity(playerProfile, true, true);

                player.sendMessage(prefix + "§7You successfully bought the §eCustom Banner §7perk for §e10000 §6coins!");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 2f, 2f);
                player.closeInventory();
            }



            });


        player.openInventory(inventory.getInventory());
    }

    public void openSecondPerkInventory(Player player, PerkType perkType, PerkManager.SortOptionPerk sortOptionPerk, PerkManager.SortOptionPlayer sortOptionPlayer) {
        int inventorySize = checkInventorySize((int) BukkitCore.getInstance().getPerkCache().getPerkHashMap().values().stream().filter(perk -> perk.getPerkType() == perkType).count()) + 9;
        Inventory inventory = new Inventory("§8» §6Perks", inventorySize);

        player.playSound(player.getLocation(), Sound.CLICK, 1F, 100F);
        for (int i = 0; i < inventorySize; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        for (int i = inventorySize - 9; i < inventorySize; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName("§8//").build(), i);
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
        inventory.setItem(sortPerk.build(), (inventorySize - 9) + 1, event -> {

            if (sortOptionPerk == SortOptionPerk.NORMAL) {
                openSecondPerkInventory(player, perkType, SortOptionPerk.COINS, sortOptionPlayer);
            }
            if (sortOptionPerk == SortOptionPerk.COINS) {
                openSecondPerkInventory(player, perkType, SortOptionPerk.RANK, sortOptionPlayer);
            }
            if (sortOptionPerk == SortOptionPerk.RANK) {
                openSecondPerkInventory(player, perkType, SortOptionPerk.NORMAL, sortOptionPlayer);
            }

        });

        inventory.setItem(new ItemBuilder(Material.BARRIER, 1).setName("§8» §cReset Filter & Sort").build(), (inventorySize - 9) + 4, event -> {
            openSecondPerkInventory(player, perkType, SortOptionPerk.NORMAL, SortOptionPlayer.ALL);
        });

        // sort player perk
        inventory.setItem(sortPlayer.build(), (inventorySize - 9) + 7, event -> {
            if (sortOptionPlayer == SortOptionPlayer.ALL) {
                openSecondPerkInventory(player, perkType, sortOptionPerk, SortOptionPlayer.OWNED);
            }
            if (sortOptionPlayer == SortOptionPlayer.OWNED) {
                openSecondPerkInventory(player, perkType, sortOptionPerk, SortOptionPlayer.UNOWNED);
            }
            if (sortOptionPlayer == SortOptionPlayer.UNOWNED) {
                openSecondPerkInventory(player, perkType, sortOptionPerk, SortOptionPlayer.ALL);
            }
        });

        showPerks(player, perkType, sortOptionPerk, sortOptionPlayer, inventory);

        player.openInventory(inventory.getInventory());
    }


    private void showPerks(Player player, PerkType perkType, PerkManager.SortOptionPerk sortOptionPerk, PerkManager.SortOptionPlayer sortOptionPlayer, Inventory inventory) {
        PerkPlayerProfile perkPlayerProfile = BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().get(player.getUniqueId());


        List<Perk> perks = BukkitCore.getInstance().getPerkCache().getPerkHashMap().values().stream().filter(perk -> perk.getPerkType() == perkType).filter(perk -> switch (sortOptionPlayer) {
            case OWNED ->
                (perk.isBuyAble() && perkPlayerProfile.getOwnedPerks().contains(perk.getId())) || (!perk.isBuyAble() && player.hasPermission(perk.getPerkRankType().getPermission()));
            case UNOWNED ->
                (perk.isBuyAble() && !perkPlayerProfile.getOwnedPerks().contains(perk.getId())) || (!perk.isBuyAble() && !player.hasPermission(perk.getPerkRankType().getPermission()));
            default -> true;
        }).collect(Collectors.toList());


        if (sortOptionPerk == SortOptionPerk.RANK) {
            perks.removeIf(Perk::isBuyAble);
            List<PerkRankType> rankOrder = Arrays.asList(PerkRankType.PLAYER, PerkRankType.PREMIUM, PerkRankType.VIP, PerkRankType.HOLY);
            perks.sort(Comparator.comparingInt(p -> rankOrder.indexOf(p.getPerkRankType())));
        } else if (sortOptionPerk == SortOptionPerk.COINS) {
            perks.removeIf(perk -> !perk.isBuyAble());
            perks.sort(Comparator.comparing(Perk::getPrice));
        } else if (sortOptionPerk == SortOptionPerk.SPECIAL) {
            perks.removeIf(perk -> !perk.isSpecial());
            perks.sort(Comparator.comparing(Perk::getId));
        } else {
            perks.sort(Comparator.comparing(Perk::getId));
        }


        int i = 0;
        for (Perk perk : perks) {
            String name = perk.getName();

            if (perkType == PerkType.CHAT) {
                String[] temp = perk.getName().split("-");
                name = "§" + temp[0] + temp[1];
            }


            ItemBuilder itemBuilder = new ItemBuilder(perk.getMaterial(), 1, perk.getSubId()).setName("§8» §6" + name);
            if (perk.isBanner()) {
                itemBuilder.setBannerMeta(perk.getBaseColor(), perk.getPatterns()).setAttribut(ItemFlag.HIDE_POTION_EFFECTS);
                ;
            }


            List<String> list = new ArrayList<>();
            if (perk.isBuyAble()) {
                list.add("§7This perk costs §e" + perk.getPrice() + " §6coins");
            } else if (perk.isSpecial()) {
                list.add(perk.getSpecialText());
            } else {
                list.add("§7Available for " + perk.getPerkRankType().getRankName() + "§7 and above");
            }

            if (
                perkPlayerProfile.getOwnedPerks().contains(perk.getId()) && perk.isBuyAble()
                    || !perk.isBuyAble() && player.hasPermission(perk.getPerkRankType().getPermission())
                    || perkPlayerProfile.getOwnedPerks().contains(perk.getId()) && perk.isSpecial()
            ) {

                list.clear();
                list.add("§ayou own this perk, click to select");
            }


            if (perk.getNotSupportedGamemodes() != null) {
                list.add("");
                list.add("§c§lNOTE §7this perk isn't supported in§8:");
                perk.getNotSupportedGamemodes().forEach(gamemodes -> list.add(" §" + gamemodes.getColor() + gamemodes.toString().toLowerCase(Locale.ROOT)));
            }

            if (perkPlayerProfile.getChatPerk() == perk.getId() || perkPlayerProfile.getBlockPerk() == perk.getId() || perkPlayerProfile.getStickPerk() == perk.getId()) {
                itemBuilder.setLore("§2selected");
                itemBuilder.setEnchantments(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setAttributs();
            } else {
                itemBuilder.setLore(list);
            }


            String finalName = name;
            inventory.setItem(itemBuilder.build(), i, event -> {

                if (perk.isBuyAble()) {
                    if (!perkPlayerProfile.getOwnedPerks().contains(perk.getId())) {
                        buyPerk(player, perkPlayerProfile, perk, finalName);
                        return;
                    }
                } else if (perk.isSpecial()) {
                    if (!perkPlayerProfile.getOwnedPerks().contains(perk.getId())) return;
                } else if (!player.hasPermission(perk.getPerkRankType().getPermission())) return;

                if (perkType == PerkType.STICK) {
                    perkPlayerProfile.setStickPerk(perk.getId());
                } else if (perkType == PerkType.BLOCK) {
                    perkPlayerProfile.setBlockPerk(perk.getId());
                } else if (perkType == PerkType.CHAT) {
                    perkPlayerProfile.setChatPerk(perk.getId());
                }
                player.closeInventory();
                player.sendMessage(prefix + "§7You selected the §e" + finalName + " §6perk!");
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 2f, 2f);

                BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().put(player.getUniqueId(), perkPlayerProfile);
                BukkitCore.getAPI().getPerkPlayerService().saveEntity(perkPlayerProfile, true, false);

            });

            i++;
        }


    }


    private void buyPerk(Player player, PerkPlayerProfile perkPlayerProfile, Perk perk, String name) {
        Inventory inventory = new Inventory("§8» §6Perks", 9);


        for (int i = 0; i < 9; i++) {
            inventory.setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 15).setName("§8//").build(), i);
        }

        ItemBuilder itemBuilder = new ItemBuilder(perk.getMaterial(), 1, perk.getSubId());

        if (perk.isBanner()) {
            itemBuilder.setBannerMeta(perk.getBaseColor(), perk.getPatterns()).setAttribut(ItemFlag.HIDE_POTION_EFFECTS);
            ;
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

        itemBuilder.setLore(list);
        itemBuilder.setName("§8» §6" + name);
        inventory.setItem(itemBuilder.build(), 4);

        inventory.setItem(new ItemBuilder(Material.INK_SACK, 1, (byte) 10).setName("§8» §aYes").build(), 2, event -> {
            player.closeInventory();
            PlayerProfile playerProfile = BukkitCore.getAPI().getPlayerService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId()));
            if (!(playerProfile.getCoins() >= perk.getPrice())) {
                player.sendMessage(prefix + "§cYou dont have enough coins!");
                player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 2f, 2f);
                return;
            }
            playerProfile.setCoins(playerProfile.getCoins() - perk.getPrice());
            player.sendMessage(prefix + "§aYou successfully bought the §e" + perk.getName() + " §aperk for §a" + perk.getPrice() + " §6coins!");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 2f, 2f);

            perkPlayerProfile.getOwnedPerks().add(perk.getId());
            BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().put(player.getUniqueId(), perkPlayerProfile);
            BukkitCore.getAPI().getPerkPlayerService().saveEntity(perkPlayerProfile, true, true);
            BukkitCore.getAPI().getPlayerService().saveEntity(playerProfile, true, true);
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


    public enum SortOptionPerk {
        NORMAL, COINS, RANK, SPECIAL;
    }

    public enum SortOptionPlayer {
        ALL, OWNED, UNOWNED;
    }

}
