package de.teamholy.core.bungee.manager;

import com.google.gson.Gson;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.bungee.model.Lens;
import org.redisson.api.RListAsync;

public class LensManager {

    CoreAPI coreAPI;

    private final RListAsync<String> messagesCollection;

    private final Gson gson;

    public LensManager(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
        this.messagesCollection = coreAPI.getRedissonManager().getRedissonClient().getList("lens_collection");
        this.gson = new Gson();
    }

    public void addMessage(Lens lens) {
        String json = gson.toJson(lens);
        messagesCollection.addAsync(json);
    }
}
