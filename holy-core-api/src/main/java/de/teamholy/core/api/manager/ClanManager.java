package de.teamholy.core.api.manager;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.entities.clan.ClanService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClanManager {

    CoreAPI coreAPI;
    ClanService clanService;


    public ClanManager(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
        this.clanService = coreAPI.getClanService();
    }

    public boolean loadAndForce(UUID profileId, UUID clanId) {
        Clan clan = clanService.getEntity(clanId,
                () -> coreAPI.getClanService().getRepository().findFirstById(clanId));
        if (clan != null)
        ClanImpl clan = clanMapCache.get(clanId);
        if (clan != null) {
            if (clan.getClanMember().contains(profileId)) {

                clanMapCache.put(clan.getClanId(),clan);

                return true;
            }
        }
        Document document = manager.find(COLL, Filters.eq("clanId", clanId.toString()));
        if (document != null && !document.isEmpty()) {
            clan = new ClanImpl();
            readDocument(clan, document);
            if (clan.getClanMember().contains(profileId)) {

                clanMapCache.put(clan.getClanId(),clan);
                return true;
            }
        }
        return false;
    }

    public void unforce(UUID clanId) {
        clanMapCache.remove(clanId);
    }

    @Override
    public boolean existsClanName(String name) {


        for (ClanImpl clan : clanMapCache.values()) {
            if (clan != null && clan.getName().equalsIgnoreCase(name))
                return true;
        }


        Document document = manager.find(COLL, MongoFilters.eqIgn("name", name));
        if (document != null && !document.isEmpty()) {
            ClanImpl clan = new ClanImpl();
            readDocument(clan, document);
            clanMapCache.put(clan.getClanId(),clan,10,TimeUnit.MINUTES);

            return true;
        }
        return false;
    }

    @Override
    public boolean existsClanTag(String tag) {

        for (ClanImpl clan : clanMapCache.values()) {
            if (clan != null && clan.getTag().equalsIgnoreCase(tag))
                return true;
        }

        Document document = manager.find(COLL, MongoFilters.eqIgn("tag", tag));
        if (document != null && !document.isEmpty()) {
            ClanImpl clan = new ClanImpl();
            readDocument(clan, document);
            clanMapCache.put(clan.getClanId(),clan,10,TimeUnit.MINUTES);

            return true;
        }
        return false;
    }

    @Override
    public Clan createClan(String name, String tag, UUID leader) {
        UUID clanId = UUID.randomUUID();
        List<UUID> members = new ArrayList<>();
        members.add(leader);
        long creationDate = System.currentTimeMillis();
        List<String> announcements = new ArrayList<>();
        List<UUID> requestsTo = new ArrayList<>();
        ClanImpl clan = new ClanImpl();
        clan.setClanId(clanId)
                .setTag(tag)
                .setName(name)
                .setColor("§e");
        clan.setCreationDate(creationDate)
                .setMembers(members)
                .setRequestsTo(requestsTo);
        clanMapCache.put(clan.getClanId(),clan,10,TimeUnit.MINUTES);

        manager.update(COLL,Filters.eq("clanId", clanId), toDocument(clan));
        return clan;
    }

    @Override
    public Clan getClanByTag(String tag) {
        for (ClanImpl clan : clanMapCache.values()) {
            if (clan != null && clan.getTag().equalsIgnoreCase(tag))
                return clan;
        }
        // Search for clans in MongoDB
        Document document = manager.find(COLL, MongoFilters.eqIgn("tag", tag));
        if (document != null && !document.isEmpty()) {
            ClanImpl clan = new ClanImpl();
            readDocument(clan, document);
            clanMapCache.put(clan.getClanId(),clan,10,TimeUnit.MINUTES);
            return clan;
        }
        return null;
    }
    @Override
    public Clan getClanById(UUID clanId) {
        ClanImpl clan = clanMapCache.get(clanId);
        if (clan != null) {
            return clan;
        }
        Document document = manager.find(COLL, Filters.eq("clanId", clanId.toString()));
        if (document != null && !document.isEmpty()) {
            clan = new ClanImpl();
            readDocument(clan, document);
            clanMapCache.put(clan.getClanId(),clan,10,TimeUnit.MINUTES);
            return clan;
        }
        return null;
    }

    @Override
    public void updateClan(Clan clan) {
        clanMapCache.put(clan.getClanId(), (ClanImpl) clan,10,TimeUnit.MINUTES);

        manager.update(COLL, Filters.eq("clanId", clan.getClanId().toString()), toDocument(clan));
    }

    @Override
    public void deleteClan(Clan clan) {
        clanMapCache.remove(clan.getClanId());
        manager.delete(COLL, Filters.eq("clanId", clan.getClanId().toString()));
    }

}
