package de.teamholy.core.api.entities.punish;

import eu.koboo.en2do.repository.entity.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class PunishProfile {

    @Id
    UUID playerId;

    String reason;
    UUID authorId;
    long createDate;
    long duration;
    String evidence;
    PunishType type;

    public long getValidUntilDate() {
        return createDate + duration;
    }

    public boolean isActive() {
        if(duration == -1) {
            return true;
        }
        return getValidUntilDate() > System.currentTimeMillis();
    }

    public long getMillisLeft() {
        if(duration == -1) {
            return duration;
        }
        return getValidUntilDate() - System.currentTimeMillis();
    }
}