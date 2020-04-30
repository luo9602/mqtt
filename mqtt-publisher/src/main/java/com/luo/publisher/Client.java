package com.luo.publisher;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * @author : Administrator
 * @date : 2020/4/30
 * @description :
 */
@Slf4j
@Component
@Order(value = 1)
public class Client implements CommandLineRunner {

    /**
     * mqtt服务器地址
     */
    public static final String MQTT_BROKER_HOST = "tcp://127.0.0.1:1883";

    /**
     * 订阅标识
     */
    public static final String TOPIC = "test/luo";

    private static String USERNAME = "admin";

    private static String PASSWORD = "password";

    /**
     * 客户端唯一标识
     */
    public static final String MQTT_CLIENT_ID = "publisher";

    private static MqttTopic MQTT_TOPIC;

    private static MqttClient MQTT_CLIENT;

    public void startUp(String serverId) {
        // 推送消息
        MqttMessage message = new MqttMessage();
        try {
            MQTT_CLIENT = new MqttClient(MQTT_BROKER_HOST, serverId, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(USERNAME);
            options.setPassword(PASSWORD.toCharArray());
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);

            MQTT_TOPIC = MQTT_CLIENT.getTopic(TOPIC);

            message.setQos(0);
            message.setRetained(false);
            MQTT_CLIENT.connect(options);

            log.info("server start...");

            boolean flag = true;

            while (flag) {
                System.out.println("enter message please");
                Scanner scanner = new Scanner(System.in);
                String next = scanner.next();
                if ("stop".equals(next)) {
                    flag = false;
                } else {
                    message.setPayload(next.getBytes());
                    MqttDeliveryToken token = MQTT_TOPIC.publish(message);
                    token.waitForCompletion();
                    log.info("send successfully");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 组件启动时会执行run, @Async这个注解是让线程异步执行,这样不影响主线程
     */
    @Async
    @Override
    public void run(String... args) {
        startUp(MQTT_CLIENT_ID);
    }

}
