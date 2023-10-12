package de.teamholy.core.api.utility;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Report {

    UUID sender, target;
    UUID viewer;
    Long time, viewerSince;
    String reason, chatlogID;
    boolean targetOnline;
}