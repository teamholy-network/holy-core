package de.teamholy.core.api.utility;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

/* copyright by Yassino */
@NoArgsConstructor
@Getter
@Setter
public class CustomBanner {

    String baseColor = "WHITE";
    boolean activated = false;
    List<Pattern> patterns = Lists.newArrayList();

    @Getter
    @NoArgsConstructor
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Pattern {
        String color;
        String patternName;
    }
}