package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

public class ErrorMessage extends Message {

    private int opCode= 11;

    private int messageOpcode;

    public ErrorMessage(int messageOpcode) {
        this.messageOpcode=messageOpcode;
    }

    public int getOpCode() {
        return opCode;
    }

    public int getMessageOpcode() {
        return messageOpcode;
    }

    public String toString()
    {
        return "ERROR "+ messageOpcode;
    }
}
