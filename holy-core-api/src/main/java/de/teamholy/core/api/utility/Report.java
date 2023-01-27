package de.teamholy.core.api.utility;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Report {

    private UUID sender, target;
    private UUID viewer;
    private Long time;
    private String reason;


}
