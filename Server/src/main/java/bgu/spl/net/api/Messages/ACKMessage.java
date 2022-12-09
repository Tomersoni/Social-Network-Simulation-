package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

public class ACKMessage extends Message {

    private int opCode=10;
    private int messageopCode;


    public ACKMessage(int messageopCode) {
        this.messageopCode = messageopCode;
    }

    public int getOpCode() {
        return opCode;
    }

    public int getMessageopCode() {
        return messageopCode;
    }

    public String toString()
    {
        return "ACK "+messageopCode;
    }
}
