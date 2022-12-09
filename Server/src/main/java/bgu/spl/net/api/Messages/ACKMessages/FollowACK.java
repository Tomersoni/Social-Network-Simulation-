package bgu.spl.net.api.Messages.ACKMessages;

import bgu.spl.net.api.Messages.ACKMessage;

public class FollowACK extends ACKMessage {

    String username;
    int followOrUnfollow;

    public FollowACK(int messageopCode, String username, boolean followOrUnfollow) {
        super(messageopCode);
        this.username=username;
        if(followOrUnfollow)
            this.followOrUnfollow=0;
        else
            this.followOrUnfollow=1;
    }


    public String toString()
    {
        return "ACK "+4+" "+this.followOrUnfollow+ " "+ username;
    }

}
