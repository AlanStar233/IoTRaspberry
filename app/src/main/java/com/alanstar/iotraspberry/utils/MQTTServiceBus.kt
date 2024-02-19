package com.alanstar.iotraspberry.utils

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

class MQTTServiceBus {

    private lateinit var mqttClient: MqttAndroidClient

    companion object {
        const val TAG = "MQTTServiceBus"
    }

    /**
     * 连接到 MQTT Broker
     * @param context 上下文
     * @param serverAddress  MQTT Broker 地址 (e.g.: tcp://broker.emqx.io:1883  mqtt://broker.biliforum.cn:1883)
     * @param clientId 客户端 ID (e.g.: IoTRaspberry)
     * @param userName 用户名, 如果无就传空
     * @param password 密码, 如果无就传空
     */
    fun connect(context: Context, serverAddress: String, clientId: String, userName: String, password: String) {

        mqttClient = MqttAndroidClient(context, serverAddress, clientId)
        mqttClient.setCallback(object : MqttCallback {

            // Light: 接收到 broker 消息
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d(TAG, "接收到 $topic 话题消息: ${message.toString()}")
            }

            // Light: 连接丢失
            override fun connectionLost(cause: Throwable?) {
                Log.d(TAG, "连接丢失: ${cause?.message}")
            }

            // Light: 消息到 broker 传递完成
            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d(TAG, "消息发送成功")
            }
        })

        // Light: 连接选项
        val options = MqttConnectOptions()
        options.userName = userName
        options.password = password.toCharArray()

        // Light: 尝试连接
        try {
            mqttClient.connect(options, null, object : IMqttActionListener {

                // Light: 连接成功
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "连接成功")
                }

                // Light: 连接失败
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "连接失败: ${exception?.message}")
                }
            })
        } catch (e: MqttException) {
            Log.d(TAG, "连接时出现异常: ${e.message}")
        }
    }

    /**
     * 断开连接
     */
    fun disconnect() {
        try {

            mqttClient.disconnect(null, object : IMqttActionListener {

                // Light: 断开成功
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "断开连接成功")
                }

                // Light: 断开失败
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "断开连接失败: ${exception?.message}")
                }
            })
        } catch (e:MqttException) {
            Log.d(TAG, "断开连接时出现异常: ${e.message}")
        }
    }

    /**
     * 订阅 topic
     * @param topic 话题名
     * @param qos 质量等级 (默认为 1)
     */
    fun subscribe(topic: String, qos: Int = 1) {

        try {
            mqttClient.subscribe(topic, qos, null, object: IMqttActionListener {

                // Light: 订阅成功
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "订阅 $topic 话题成功")
                }

                // Light: 订阅失败
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "订阅 $topic 话题失败: ${exception?.message}")
                }
            })
        } catch (e: MqttException) {
            Log.d(TAG, "订阅时出现异常: ${e.message}")
        }
    }

    /**
     * 取消订阅 topic
     * @param topic 话题名
     */
    fun unSubscribe(topic: String) {

        try {
            mqttClient.unsubscribe(topic, null, object : IMqttActionListener {

                // Light: 取消订阅成功
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "取消订阅 $topic 话题成功")
                }

                // Light: 取消订阅失败
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "取消订阅 $topic 话题失败: ${exception?.message}")
                }
            })
        } catch (e:MqttException) {
            Log.d(TAG, "取消订阅时出现异常: ${e.message}")
        }
    }

    /**
     * 发布消息到 topic
     * @param topic 话题名
     * @param message 消息内容
     * @param qos 质量等级 (默认为 1)
     * @param retained 是否保留消息 (默认为 false)
     */
    fun publish(topic: String, message: String, qos: Int = 1, retained: Boolean = false) {

        try {
            val mqttMessage = MqttMessage()
            mqttMessage.payload = message.toByteArray()
            mqttMessage.qos = qos
            mqttMessage.isRetained = retained

            mqttClient.publish(topic, mqttMessage, null, object : IMqttActionListener {

                // Light: 发布消息成功
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "发布消息到 $topic 话题成功, 内容: $message")
                }

                // Light: 发布消息失败
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "发布消息到 $topic 话题失败: ${exception?.message}")
                }
            })
        } catch (e: MqttException) {
            Log.d(TAG, "发布消息时出现异常: ${e.message}")
        }
    }
}