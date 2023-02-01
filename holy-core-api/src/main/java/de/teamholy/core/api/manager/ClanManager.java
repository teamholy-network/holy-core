package de.teamholy.core.api.manager;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.entities.clan.ClanService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ClanManager {

    CoreAPI coreAPI;
    ClanService clanService;

    public boolean loadAndForce(UUID profileId, UUID clanId) {
        Clan clan = clanService.getEntity(clanId,
                () -> coreAPI.getClanService().getRepository().findFirstById(clanId));
        if (clan != null) {
            if (clan.getMembers().contains(profileId)) {
                clanService.saveEntity(clan, true, false);
                return true;
            }
        }
        return false;
    }

    public void unforce(UUID clanId) {
        clanService.getRedisCache().updateEntryExpiration(clanId, 15, TimeUnit.MINUTES, 0, TimeUnit.SECONDS);
    }

    public boolean existsClanName(String name) {

        for (Clan clan : clanService.getRedisCache().values()) {
            if (clan != null && clan.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }


        Clan clan = clanService.getRepository().findFirstByName(name);
        if (clan != null) {
            clanService.getRedisCache().fastPut(clan.getClanId(), clan, 15, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    public boolean existsClanTag(String tag) {

        for (Clan clan : clanService.getRedisCache().values()) {
            if (clan != null && clan.getTag().equalsIgnoreCase(tag)) {
                return true;
            }
        }

        Clan clan = clanService.getRepository().findFirstByTag(tag);
        if (clan != null) {
            clanService.getRedisCache().fastPut(clan.getClanId(), clan, 15, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    public Clan createClan(String name, String tag, UUID leader) {
        UUID clanId = UUID.randomUUID();
        List<UUID> members = new ArrayList<>();
        members.add(leader);
        long creationDate = System.currentTimeMillis();
        List<UUID> requestsTo = new ArrayList<>();

        Clan clan = new Clan();
        clan.setClanId(clanId);
        clan.setTag(tag);
        clan.setName(name);
        clan.setColor("§e");
        clan.setCreationDate(creationDate);
        clan.setMembers(members);
        clan.setRequestsTo(requestsTo);

        clanService.saveEntity(clan, false, true);

        return clan;
    }

    public Clan getClanByTag(String tag) {

        for (Clan clan : clanService.getRedisCache().values()) {
            if (clan != null && clan.getTag().equalsIgnoreCase(tag)) {
                return clan;
            }
        }

        Clan clan = clanService.getRepository().findFirstByTag(tag);
        if (clan != null) {
            clanService.getRedisCache().fastPut(clan.getClanId(), clan, 15, TimeUnit.MINUTES);
            return clan;
        }
        return null;
    }

    public Clan getClanById(UUID clanId) {
        return clanService.getEntity(clanId, () -> clanService.getRepository().findFirstById(clanId));
    }

    public void updateClan(Clan clan) {
        clanService.saveEntity(clan, false, true);
    }

    public void deleteClan(Clan clan) {
        clanService.deleteEntity(clan);
    }

}
