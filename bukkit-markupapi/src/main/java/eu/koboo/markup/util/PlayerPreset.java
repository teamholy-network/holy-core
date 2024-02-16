package eu.koboo.markup.util;

import com.mojang.authlib.properties.Property;
import eu.koboo.en2do.repository.entity.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor(force = true)
@Getter
@Setter
public class PlayerPreset {


    private String name;
    @Id
    private UUID uuid;
    private String value;
    private String signature;

}
