package de.teamholy.core.api.utility;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/* copyright by Yassino */
@NoArgsConstructor @Getter @Setter
public class CustomBanner {

    String baseColor = "WHITE";
    boolean activated = false;
    List<Pattern> patterns = Lists.newArrayList();


    @Getter @NoArgsConstructor @Setter
    public class Pattern {
        private String color;
        private String patternName;
    }

}
