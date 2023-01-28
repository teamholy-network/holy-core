package de.teamholy.core.api.entities.ban;

import eu.koboo.en2do.repository.entity.Id;
import eu.koboo.en2do.repository.entity.NonIndex;
import eu.koboo.en2do.repository.entity.compound.CompoundIndex;
import eu.koboo.en2do.repository.entity.compound.Index;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class BanProfile {

    @Id
    @NonIndex
    UUID playerId;

    String reason;
    UUID authorId;
    long createDate;
    long duration;
    String evidence;

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