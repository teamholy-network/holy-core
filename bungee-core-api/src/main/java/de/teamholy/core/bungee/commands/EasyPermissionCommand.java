package de.teamholy.core.bungee.commands;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionGroup;
import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/* copyright by Yassino */
public class EasyPermissionCommand extends Command {

    private String prefix = "§9EasyPerms §8× §7";

    public EasyPermissionCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        UUID author = BungeeUtil.parseAuthorUUID(commandSender);

        if (!commandSender.hasPermission("teamholy.easyperms")) return;

        if (args.length != 3) {
            sendHelp(commandSender);
        } else {
            if (args[0].equalsIgnoreCase("addpermission")) {
                IPermissionGroup permissionGroup = CloudNetDriver.getInstance().getPermissionManagement().getGroup(args[1]);


                if (permissionGroup == null) {
                    commandSender.sendMessage(prefix + "Diese gruppe gibt es nicht");
                    return;
                }

                String permission = args[2];


                AtomicInteger i = new AtomicInteger();
                CloudNetDriver.getInstance().getPermissionManagement().getGroups().forEach(group -> {
                    if (group.getSortId() <= permissionGroup.getSortId()) {
                        if (!group.getGroupPermissions().containsKey(permission)) {
                            group.addPermission(permission);
                            CloudNetDriver.getInstance().getPermissionManagement().updateGroup(permissionGroup);
                            i.getAndIncrement();
                        }
                    }
                });

                commandSender.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(author, "Added the permission §e{} §7to §a{} §7groups!", permission, String.valueOf(i.get())));
            } else if (args[0].equalsIgnoreCase("removepermission")) {
                IPermissionGroup permissionGroup = CloudNetDriver.getInstance().getPermissionManagement().getGroup(args[1]);


                if (permissionGroup == null) {
                    commandSender.sendMessage(prefix + "Diese gruppe gibt es nicht");
                    return;
                }

                String permission = args[2];


                AtomicInteger i = new AtomicInteger();

                CloudNetDriver.getInstance().getPermissionManagement().getGroups().forEach(group -> {
                    if (group.getSortId() <= permissionGroup.getSortId()) {
                        if (group.getGroupPermissions().containsKey(permission)) {
                            group.removePermission(permission);
                            CloudNetDriver.getInstance().getPermissionManagement().updateGroup(permissionGroup);
                            i.getAndIncrement();
                        }
                    }
                });

                commandSender.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(author, "Removed the permission §e{} §7from §c{} §7groups!", permission, String.valueOf(i.get())));
            }
        }
    }


    private void sendHelp(CommandSender commandSender) {
        UUID author = BungeeUtil.parseAuthorUUID(commandSender);
        commandSender.sendMessage(prefix + "/easyperms addpermission (" + BungeeTranslateAPI.translatePlaceholder(author, "group") + ") (" + BungeeTranslateAPI.translatePlaceholder(author, "permission") + ")");
        commandSender.sendMessage(prefix + "/easyperms removepermission (" + BungeeTranslateAPI.translatePlaceholder(author, "group") + ") (" + BungeeTranslateAPI.translatePlaceholder(author, "permission") + ")");
        commandSender.sendMessage(prefix + BungeeTranslateAPI.translate(author, "Easyperms wenn du eine permission vergibst oder entfernst wird sie für alle darüber auch!"));
    }
}
