package com.benny.wificar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Wificar extends ActionBarActivity implements View.OnClickListener  {
    private static final String TAG = "rc.simple";

    private String host = "tcp://192.168.0.10:1883";
    private String userName = "admin";
    private String passWord = "password";

    private Handler handler;
    private MqttClient client;
    private String myTopic = "/hackafe-car";
    private MqttConnectOptions options;
    private ScheduledExecutorService scheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wificar);
        ((Button)findViewById(R.id.leftkey)).setOnClickListener(this);
        ((Button)findViewById(R.id.rightkey)).setOnClickListener(this);
        ((Button)findViewById(R.id.upkey)).setOnClickListener(this);
        ((Button)findViewById(R.id.downkey)).setOnClickListener(this);
        init();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1) {
                    Toast.makeText(Wificar.this, (String) msg.obj,
                            Toast.LENGTH_SHORT).show();
                    System.out.println("-----------------------------");
                } else if(msg.what == 2) {
                    publish("Connect Success!");
                    Toast.makeText(Wificar.this, "连接成功", Toast.LENGTH_SHORT).show();
                    try {
                        client.subscribe(myTopic, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(msg.what == 3) {
                    Toast.makeText(Wificar.this, "连接失败，系统正在重连", Toast.LENGTH_SHORT).show();
                }
            }
        };

        startReconnect();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.leftkey:
                publish("l 100");
                break;
            case R.id.rightkey:
                publish("r 100");
                break;
            case R.id.upkey:
                publish("f 100");
                break;
            case R.id.downkey:
                publish("b 100");
                break;

        }
    }
    private void publish(String msg) {
        try {
            client.publish(myTopic, msg.getBytes(), 0, false);
        } catch (MqttException e) {
            Log.e(TAG, "problem sending " + msg + " through MQTT", e);
//            throw new RuntimeException(e);
        }
    }
    private void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if(!client.isConnected()) {
                    connect();
                }
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }

    private void init() {
        try {
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(host, "test",
                    new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            //设置连接的用户名
//            options.setUserName(userName);
            //设置连接的密码
//            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(120);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(120);
            //设置回调
            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    System.out.println("connectionLost----------");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    //subscribe后得到的消息会执行到这里面
                    System.out.println("messageArrived----------");
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = topicName+"---"+message.toString();
                    handler.sendMessage(msg);
                }
            });
//			connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    client.connect(options);
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 3;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            scheduler.shutdown();
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}


