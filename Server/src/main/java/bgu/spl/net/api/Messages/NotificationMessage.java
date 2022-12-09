package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

public class NotificationMessage extends Message {
    private int opCode=9;
    public enum Type{PM,Public};
    private Type type;
    private String postingUser;
    private String content;

    public NotificationMessage(Type type, String postingUser,String content) {

        this.type= type;
        this.postingUser=postingUser;
        this.content=content;
    }

    public int getOpCode() {
        return opCode;
    }

    public Type getType() {
        return type;
    }

    public String getPostingUser() {
        return postingUser;
    }

    public String getContent() {
        return content;
    }

    public String toString()
    {
        String sType = "PM";
        if(type==Type.Public)
            sType="Public";

        return "NOTIFICATION "+ sType+ " "+postingUser+ " "+ content;
    }
}
