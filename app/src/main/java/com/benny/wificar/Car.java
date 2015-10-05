package com.benny.wificar;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by groupsky on 04.02.15.
 */
public class Car {

    private static final String TAG = "rc.car";
    final IMqttAsyncClient mqtt;
    final String topic;

    int steering = 0;
    int engine = 0;

    public Car(IMqttAsyncClient mqtt, String topic) {
        this.mqtt = mqtt;
        this.topic = topic;
    }

    boolean check(int newVal, int oldVal) {
        return Math.abs(newVal - oldVal) > 10;
    }

    public void left() {
        steering = -100;
        publish("l 100");
    }

    public void right() {
        steering = 100;
        publish("r 100");
    }

    public void forward() {
        engine = 100;
        publish("f 100");
    }

    public void reverse() {
        engine = -100;
        publish("b 100");
    }

    public void left(int power) {
        if (!check(-power, steering)) return;
        steering = -power;
        publish("l" + power);
    }

    public void right(int power) {
        if (!check(power, steering)) return;
        steering = power;
        publish("r" + power);
    }

    public void forward(int power) {
        if (!check(power, engine)) return;
        engine = power;
        publish("f" + power);
    }

    public void reverse(int power) {
        if (!check(-power, engine)) return;
        engine = -power;
        publish("b" + power);
    }

    public void neutral() {
        engine = 0;
        publish("n");
    }

    public void central() {
        steering = 0;
        publish("c");
    }

    public void stop() {
        engine = 0;
        publish("s");
    }

    private void publish(String msg) {
        try {
            mqtt.publish(topic, msg.getBytes(), 0, false);
        } catch (MqttException e) {
            Log.e(TAG, "problem sending " + msg + " through MQTT", e);
            throw new RuntimeException(e);
        }
    }
}
