package de.teamholy.core.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.ICommandSender;
import de.dytanic.cloudnet.common.Properties;
import de.teamholy.core.CloudModuleCore;
import de.teamholy.core.ping.HealthService;

/* copyright by Yassino */
public class HealthServiceCommand extends Command {

    public HealthServiceCommand() {
        super("healthservice", "hs");

        this.permission = "cloudnet.command.healthservice";
        this.usage = "healthservice";
        this.description = "Manage the health service";
        this.prefix = "HealthService -> ";
    }

    @Override
    public void execute(ICommandSender iCommandSender, String s, String[] args, String s1, Properties properties) {

        switch (args.length) {
            case 0 -> iCommandSender.sendMessage("/hs info");

            case 1 -> {
                if (args[0].equalsIgnoreCase("info")) {
                    HealthService healthService = CloudModuleCore.getInstance().getHealthService();
                    iCommandSender.sendMessage("There are currently " + healthService.getServiceInfoSnapshots().size() + " services registered");
                    StringBuilder stringBuilder = new StringBuilder();
                    healthService.getServiceInfoSnapshots().forEach(serviceInfoSnapshot -> stringBuilder.append(serviceInfoSnapshot.getServiceId().getName()).append(", "));
                    iCommandSender.sendMessage("Services: " + stringBuilder);

                }
            }
        }

    }
}
