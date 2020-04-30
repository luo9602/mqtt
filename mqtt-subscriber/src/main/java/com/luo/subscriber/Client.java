package com.luo.subscriber;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author: Administrator
 * @date: 2020/4/30
 * @description:
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
     * 客户端唯一标识
     */
    public static final String MQTT_CLIENT_ID = "subscriber";

    public static final String USERNAME = "admin";

    public static final String PASSWORD = "password";

    /**
     * topic 过滤
     */
    public static final String[] TOPIC_FILTER = new String[]{"test/topic", "test/luo"};

    private volatile static MqttClient MQTT_CLIENT;

    private static MqttConnectOptions MQTT_CONNECT_OPTIONS;

    public void startUp(String clientId) {
        try {
            // host为主机名，clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，
            // MemoryPersistence设置clientid的保存形式，默认为以内存保存
            MQTT_CLIENT = new MqttClient(MQTT_BROKER_HOST, clientId, new MemoryPersistence());
            // 配置参数信息
            MQTT_CONNECT_OPTIONS = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
            // 这里设置为true表示每次连接到服务器都以新的身份连接
            MQTT_CONNECT_OPTIONS.setCleanSession(true);
            // 设置用户名
            MQTT_CONNECT_OPTIONS.setUserName(USERNAME);
            // 设置密码
            MQTT_CONNECT_OPTIONS.setPassword(PASSWORD.toCharArray());
            // 设置超时时间 单位为秒
            MQTT_CONNECT_OPTIONS.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            MQTT_CONNECT_OPTIONS.setKeepAliveInterval(20);
            // 连接
            MQTT_CLIENT.connect(MQTT_CONNECT_OPTIONS);
            // 订阅
            MQTT_CLIENT.subscribe(TOPIC_FILTER);
            // 设置回调
            MQTT_CLIENT.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("connectionLost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                    log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    log.info("client receive message");
                    log.info("Topic: " + topic);
                    log.info("Message: " + mqttMessage.toString());
                    log.info("-----------------------------------------------------------------------------------");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            log.info("client start...");
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
