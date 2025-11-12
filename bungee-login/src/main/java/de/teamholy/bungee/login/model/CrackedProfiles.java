package de.teamholy.bungee.login.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CrackedProfiles {
    boolean premium = false;
    boolean bedrock = false;
    String name = "";
    String ip;
    long time = System.currentTimeMillis();
}
