package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Message;

public class BidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<T> {


    private boolean shouldTerminate=false;
    private Processor processor;
    private int connectionId;
    private Profile profile;

    public BidiMessagingProtocolImpl(Processor processor) {
        this.processor=processor;
    }

    @Override
    public void start(int connectionId, Connections<T> connections) {
        connections.getClients().get(connectionId).register();
    }

    @Override
    public void process(T message, int connectionId) {
        this.connectionId=connectionId;
        processor.process((Message) message,connectionId,this);
    }



    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }
}
