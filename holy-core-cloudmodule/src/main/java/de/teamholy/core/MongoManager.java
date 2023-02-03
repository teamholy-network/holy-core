package de.teamholy.core;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Getter
/* copyright by Yassino */
public class MongoManager {


    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private ExecutorService executorService;

    public MongoManager(String url,String database) {
        mongoClient = MongoClients.create(url);
        mongoDatabase = mongoClient.getDatabase(database);
        executorService = Executors.newCachedThreadPool();
    }

    public void getDocumentAsync(String collection, Bson filter, Consumer<Document> documentConsumer) {
        executorService.execute(() -> {
            Document document = mongoDatabase.getCollection(collection).find(filter).first();
            documentConsumer.accept(document);
        });
    }

    public void getDocumentsAsync(String collection, Consumer<Iterable<Document>> iterableConsumer) {
        executorService.execute(() -> iterableConsumer.accept(mongoDatabase.getCollection(collection).find()));
    }

    public void updateDocumentAsync(String collection, Bson filter, Document document) {
        executorService.execute(() -> mongoDatabase.getCollection(collection).updateOne(filter, new BasicDBObject("$set", document)));
    }

    public void updateDocument(String collection, Bson filter, Document document) {
        document.remove("_id");
        Document save = new Document("$set",document);
        save.remove("_id");
        mongoDatabase.getCollection(collection).updateOne(filter, save);
    }

}
