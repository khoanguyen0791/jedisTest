package com.github.khoanguyen0791;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Program {

	public static final String CHANNEL_NAME = "commonChannel";

	public static Logger logger = LoggerFactory.getLogger(Program.class);

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		final JedisPoolConfig poolConfig = new JedisPoolConfig();
		final JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379, 0);
		final Jedis subscriberJedis = jedisPool.getResource();
		final Subscriber subscriber = new Subscriber();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("Subscribing to \"commonChannel\". This thread will be blocked.");
					subscriberJedis.subscribe(subscriber, CHANNEL_NAME);
					logger.info("Subscription ended.");
				} catch (Exception e) {
					logger.error("Subscribing failed.", e);
				}
			}
		}).start();

		final Jedis publisherJedis = jedisPool.getResource();

		new Publisher(publisherJedis, CHANNEL_NAME).start();

		subscriber.unsubscribe();
		jedisPool.close();
	}

}
