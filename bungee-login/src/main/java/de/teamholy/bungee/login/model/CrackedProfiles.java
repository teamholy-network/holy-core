package de.teamholy.bungee.login.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CrackedProfiles {
    boolean premium = true;
    boolean bedrock = true;
    String name = "";
    String ip;
    long time = System.currentTimeMillis();
}
