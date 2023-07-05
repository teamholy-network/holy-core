package de.teamholy.core.bungee.commands;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionGroup;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.atomic.AtomicInteger;

/* copyright by Yassino */
public class EasyPermissionCommand extends Command {

    private String prefix = "§9EasyPerms §8× §7";

    public EasyPermissionCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

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

                commandSender.sendMessage(prefix + "Added the permission §e" + permission + " §7to §a" + i.get() + " §7groups!");
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

                commandSender.sendMessage(prefix + "Removed the permission §e" + permission + " §7from §c" + i.get() + " §7groups!");
            }
        }
    }


    private void sendHelp(CommandSender commandSender) {
        commandSender.sendMessage(prefix + "/easyperms addpermission (group) (permission)");
        commandSender.sendMessage(prefix + "/easyperms removepermission (group) (permission)");
        commandSender.sendMessage(prefix + "Easyperms wenn du eine permission vergibst oder entfernst wird sie für alle darüber auch!");
    }
}
