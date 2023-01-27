package de.teamholy.core.api.entities.friend;

import de.teamholy.core.api.utility.PartyInviteAllowance;
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
public class FriendProfile {

    @Id
    UUID playerId;

    boolean allowFriendRequests;
    boolean allowFriendJump;
    PartyInviteAllowance partyInviteAllowance;
    List<UUID> friendList;
    List<UUID> friendReqeustsList;
    
}