package com.dakual;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

public class App 
{
    private static final String mysqlHost = "jdbc:mysql://localhost:3306/tutorial?serverTimezone=UTC";
    private static final String redisHost = "localhost";
    private static final Integer redisPort = 6379;
    private static JedisPool pool = null;
    private static String sql  = "SELECT * FROM planet";
    
    public static void main( String[] args )
    {
        JedisPoolConfig poolCfg = new JedisPoolConfig();
        poolCfg.setMaxTotal(3);
        pool = new JedisPool(poolCfg, redisHost, redisPort);     
        
        try (Jedis jedis = pool.getResource()) {
            //jedis.del(sql);
            if(jedis.exists(sql)) {
                String cachedResponse = jedis.get(sql);
                System.out.println("From Cache:");
                System.out.println(cachedResponse);
            } else {
                retrieveFromDB();
            }
        }
    }
    
    public static void retrieveFromDB() {
        try (Connection connection = DriverManager.getConnection(mysqlHost, "username", "password"); 
            PreparedStatement ps = connection.prepareStatement(sql); 
            ResultSet rs = ps.executeQuery()) {

            JSONArray json = new JSONArray();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                int numColumns = rsmd.getColumnCount();
                JSONObject obj = new JSONObject();
                for (int i=1; i<=numColumns; i++) {
                    String column_name = rsmd.getColumnName(i);
                    obj.put(column_name, rs.getObject(column_name));
                }

                json.put(obj);
            }
            
            try (Jedis jedis = pool.getResource()) {
                SetParams params = new SetParams();
                params.ex(10);
                jedis.set(sql, json.toString(), params);
            }

            System.out.println("From Database:");
            System.out.println(json);

        } catch (SQLException e) {
            e.printStackTrace();
        }       
    }
}
