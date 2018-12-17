package com.ali.cs491.carbuds;

import java.io.IOException;

import android.util.Log;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Base class for objects that connect to a RabbitMQ Broker
 */
public class IConnectToRabbitMQ {
    String server;
    String exchange;
    String queue;
    String password;
    String username;
    int host;

    protected Channel channel = null;
    protected Connection  connection;

    protected boolean Running ;

    protected  String exchangeType ;

    /**
     *
     * @param server The server address
     * @param exchange The named exchange
     * @param exchangeType The exchange type name
     */
    public IConnectToRabbitMQ(String server, int host, String username, String password,
                              String exchange, String exchangeType, String queue)
    {
  	  this.server = server;
  	  this.host = host;
  	  this.exchange = exchange;
  	  this.exchangeType = exchangeType;
  	  this.username = username;
  	  this.password = password;
  	  this.queue = queue;
    }

    public void Dispose()
    {
        Running = false;

			try {
				if (connection!=null)
					connection.close();
				if (channel != null)
					channel.abort();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    }

    /**
     * Connect to the broker and create the exchange
     * @return success
     */
    public boolean connectToRabbitMQ()
    {
  	  if(channel!= null && channel.isOpen() )//already declared
  		  return true;
        try
        {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(server);

            factory.setUsername(username);
            factory.setPassword(password);
            factory.setVirtualHost("/");
            factory.setPort(host);

            // Connect to broker
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(exchange, exchangeType, true);

            return true;
        }
        catch (Exception e)
        {
      	  e.printStackTrace();
            return false;
        }
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean isRunning() {
        return Running;
    }

    public void setRunning(boolean running) {
        Running = running;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }
}