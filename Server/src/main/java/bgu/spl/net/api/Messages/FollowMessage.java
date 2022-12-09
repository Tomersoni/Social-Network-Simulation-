package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Message;

public class FollowMessage extends Message
{
    private int opCode=4;
    boolean followOrUnfollow;
    String userName;

    public FollowMessage(short followCode, String username) {
        followOrUnfollow=followCode==0;
        this.userName=username;
    }

    public int getOpCode() {
        return opCode;
    }

    public boolean isFollowOrUnfollow() {
        return followOrUnfollow;
    }

    public String getUserName() {
        return userName;
    }


}
