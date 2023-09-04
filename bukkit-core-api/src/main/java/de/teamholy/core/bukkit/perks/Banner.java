package de.teamholy.core.bukkit.perks;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Banner {

    private int id;
    private String name;
    private String baseColor;
    private List<BannerPattern> patterns;

}