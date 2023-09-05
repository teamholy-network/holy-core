package de.teamholy.core.api.entities.banner;

import eu.koboo.en2do.repository.entity.Id;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
public class Banner {

    @Id
    private ObjectId bannerId;



    private String id;
    private String name;
    private String baseColor;
    private List<Pattern> patterns;

    @Getter
    @Setter
    public static class Pattern {
        private String color;
        private String pattern;
    }
}
