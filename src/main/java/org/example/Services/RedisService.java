package org.example.Services;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class RedisService {

    private final JedisPool jedisPool;

    public RedisService(String host, int port) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(5);
        this.jedisPool = new JedisPool(host, port);

    }

    public void putWithSerialize(String key, Object value) throws IOException {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key.getBytes(), serialize(value));
        }
    }

    public void put(String key, String json,int exTime) throws IOException {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key.getBytes(), exTime, json.getBytes());
        }
    }

    public Set<String> getKeys()  throws IOException {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys("*");
        }
    }
    public Object getWithDeserialize(String key) throws IOException, ClassNotFoundException {
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] data = jedis.get(key.getBytes());
            if (data != null) {
                return deserialize(data);
            }
            return null;
        }
    }
    public String get(String key) throws IOException, ClassNotFoundException {
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] data = jedis.get(key.getBytes());
            if (data != null) {
                return new String(data, StandardCharsets.UTF_8);
            }
            return null;
        }
    }

    public void delete(String key) throws IOException, ClassNotFoundException {
        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis.exists(key)) jedis.del(key);

        }
    }

    private byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(byteOut)) {
            out.writeObject(obj);
            return byteOut.toByteArray();
        }
    }

    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
             ObjectInputStream in = new ObjectInputStream(byteIn)) {
            return in.readObject();
        }
    }

    public void close() {
        jedisPool.close();
    }
}