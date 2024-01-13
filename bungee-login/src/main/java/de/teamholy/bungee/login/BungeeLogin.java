package de.teamholy.bungee.login;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;


public class BungeeLogin extends Plugin {


        @Override
        public void onEnable() {

            ProxyServer.getInstance().getLogger().info("BungeeLogin enabled");



        }

        @Override
        public void onDisable() {

        }

}
