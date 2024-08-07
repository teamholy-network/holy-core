package de.teamholy.core.api.rabbit;
import com.rabbitmq.client.*;

import java.io.IOException;

public class Rabbit {

    private Connection connection;


    public Rabbit(String connection) {
        ConnectionFactory factory = new ConnectionFactory();

        try {
            factory.setUri(connection);
            this.connection = factory.newConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void subscribeRabbitQueue(String queue, DeliverCallback callback){

        try {
            Channel channel = connection.createChannel();
            channel.queueDeclare(queue, true, false, false, null);

            channel.basicConsume("holy", true, callback, consumerTag ->
            {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
