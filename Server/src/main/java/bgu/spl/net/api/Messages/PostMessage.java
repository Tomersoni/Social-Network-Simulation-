package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

public class PostMessage extends Message
{
    private int opCode=5;
    private String content;

    public PostMessage(String msg) {
        content= msg.substring(0,msg.length()-1);
    }

    public int getOpCode() {
        return opCode;
    }

    public String getContent() {
        return content;
    }
}
