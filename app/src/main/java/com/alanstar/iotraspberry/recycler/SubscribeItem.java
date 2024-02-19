package com.alanstar.iotraspberry.recycler;

public class SubscribeItem {

    String broker;
    String topic;
    String protocol;

    int image;

    public SubscribeItem(String broker, String topic, String protocol, int image) {
        this.broker = broker;
        this.topic = topic;
        this.protocol = protocol;
        this.image = image;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
