package de.teamholy.core.api.entities;

import de.teamholy.core.api.CoreAPI;
import eu.koboo.en2do.repository.Repository;
import lombok.Getter;
import org.redisson.api.RMapCache;
import org.redisson.api.map.event.EntryExpiredListener;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Getter
public class AbstractService<E, K, R extends Repository<E, K>> {

    CoreAPI coreAPI;
    R repository;
    RMapCache<K, E> redisCache;

    public AbstractService(CoreAPI coreAPI, Class<R> repoClass,, boolean saveOnExpiration) {
        this.coreAPI = coreAPI;
        this.repository = coreAPI.getMongoManager().create(repoClass);
        this.redisCache = coreAPI.getRedissonManager().getRedissonClient().getMapCache(repository.getCollectionName());

        if(saveOnExpiration) {
            redisCache.addListener((EntryExpiredListener<K, E>) event -> {
                E entity = event.getValue();
                coreAPI.getExecutor().submit(() -> repository.save(entity));
            });
        }
    }

    private K getId(E entity) {
        K uniqueId = repository.getUniqueId(entity);
        if(uniqueId == null) {
            throw new RuntimeException("Entity of type \"" + entity.getClass() + "\" is missing @Id field!");
        }
        return uniqueId;
    }

    private E handleRetriever(EntityRetriever<E> retriever) {
        if(retriever == null) {
            return null;
        }
        E entity = retriever.retrieve();
        if(entity == null) {
            return null;
        }
        K uniqueId = getId(entity);
        redisCache.fastPut(uniqueId, entity);
        return entity;
    }

    public E getEntity(K key, EntityRetriever<E> retriever) {
        if(key == null) {
            return handleRetriever(retriever);
        }
        E cacheEntity = redisCache.get(key);
        if(cacheEntity == null) {
            return handleRetriever(retriever);
        }
        return cacheEntity;
    }


    public void getEntityAsync(K key, EntityRetriever<E> retriever, Consumer<E> consumer) {
        coreAPI.getExecutor().execute(() -> consumer.accept(getEntity(key,retriever)));
    }

    public void saveEntity(E entity, boolean forceCache, boolean toDatabase) {
        if (forceCache)
            redisCache.fastPut(getId(entity), entity);
        else
            redisCache.fastPut(getId(entity),entity,15, TimeUnit.MINUTES);

        if(toDatabase) {
            repository.save(entity);
        }
    }

    public void deleteEntity(E entity) {
        K key = getId(entity);
        redisCache.removeAsync(key);
        repository.deleteById(key);
    }
}