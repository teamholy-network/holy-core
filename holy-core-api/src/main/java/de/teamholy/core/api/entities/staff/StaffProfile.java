package de.teamholy.core.api.entities.staff;

import de.teamholy.core.api.entities.ban.BanProfile;
import de.teamholy.core.api.entities.mute.MuteProfile;
import de.teamholy.core.api.utility.Report;
import eu.koboo.en2do.repository.entity.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class StaffProfile {

    @Id
    UUID playerId;

    boolean notify;
    List<Report> reportList;
    List<BanProfile> banProfileList;
    List<MuteProfile> muteProfileList;
}