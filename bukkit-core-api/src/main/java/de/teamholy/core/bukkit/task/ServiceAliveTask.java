package de.teamholy.core.bukkit.task;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.bukkit.BukkitCore;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class ServiceAliveTask implements Runnable {

    @Override
    public void run() {
        BukkitCore.getAPI().getCloudManager().sendCloudMessage("alive", "ping:response", new JsonDocument().append("server", Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName()).append("response", "online"));
    }


}
