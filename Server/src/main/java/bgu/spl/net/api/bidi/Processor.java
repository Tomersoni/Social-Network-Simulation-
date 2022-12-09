package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Message;
import bgu.spl.net.api.Messages.*;
import bgu.spl.net.api.Messages.ACKMessages.FollowACK;
import bgu.spl.net.api.Messages.ACKMessages.LogstatACK;
import bgu.spl.net.api.Messages.ACKMessages.StatACK;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Processor {

    private ConnectionsImpl connections;
    private HashMap<String , Profile> usernametoprofileList;
    private Queue<Message> postsAndPMs;
    private String[] forbiddenWords;
//    private HashMap<String,Integer> usernameToId;
//    private HashMap<String,String> usernameToPassword;

    public Processor(ConnectionsImpl connections, String[] forbiddenWords) {
        this.connections = connections;
        usernametoprofileList=new HashMap<>();
        this.postsAndPMs=new ConcurrentLinkedQueue<>();
        this.forbiddenWords=forbiddenWords;
        //this.usernameToId=new HashMap<>();
    }

    public void process(Message msg, int connectionId,BidiMessagingProtocolImpl protocol){
        switch (msg.getOpCode()){
            case 1:
                process1(msg, connectionId, protocol);
                break;
            case 2:
                process2(msg, connectionId, protocol);
                break;
            case 3:
                process3(msg, connectionId, protocol);
                break;
            case 4:
                process4(msg, connectionId,protocol);
                break;
            case 5:
                process5(msg, connectionId, protocol);
                break;
            case 6:
                process6(msg, connectionId,protocol);
                break;
            case 7:
                process7(msg, connectionId,protocol);
                break;
            case 8:
                process8(msg, connectionId,protocol);
                break;
            case 12:
                process12(msg, connectionId,protocol);
                break;
        }
    }

    private void process1(Message msg, int connectionId,BidiMessagingProtocolImpl protocol){
          ConnectionHandler handler= (ConnectionHandler) connections.getClients().get(connectionId);
          if(handler.isRegistered()){
              ErrorMessage error=new ErrorMessage(1);
              handler.send(error);
          }
          else{
              protocol.start(connectionId,connections);
              RegisterMessage rmsg=(RegisterMessage) msg;
              Profile profile=new Profile(rmsg.getUserName(),rmsg.getPassword(),rmsg.getBirthday(), connectionId);
              protocol.setProfile(profile);
              usernametoprofileList.put(rmsg.getUserName(),profile);
//              usernameToId.put(rmsg.getUserName(),connectionId);
//              usernameToPassword.put(rmsg.getUserName(), rmsg.getPassword());
              handler.send(new ACKMessage(1));
          }
    }

    private void process2(Message msg,int connectionId, BidiMessagingProtocolImpl protocol){
        ConnectionHandler handler= (ConnectionHandler) connections.getClients().get(connectionId);
        LoginMessage lmsg=(LoginMessage) msg;
        if(usernametoprofileList.get(lmsg.getUsername())==null ||usernametoprofileList.get(lmsg.getUsername()).isLoggedIn() ||
                !lmsg.isCaptcha() ||
                !usernametoprofileList.get(lmsg.getUsername()).getPassword().equals(lmsg.getPassword())){
            ErrorMessage error=new ErrorMessage(2);
            handler.send(error);
        }
        else{
            if(protocol.getProfile()==null){
                protocol.setProfile(usernametoprofileList.get(lmsg.getUsername()));
            }
            protocol.getProfile().LogIn();
            handler.send(new ACKMessage(2));
            Queue<Message> awaiting= protocol.getProfile().getAwaitingMessages();
            for(Message currmsg:awaiting){
                handler.send(currmsg);
            }
            protocol.getProfile().clearAwaitingMessages();
        }
    }

    private void process3(Message msg,int connectionId, BidiMessagingProtocolImpl protocol){
        ConnectionHandler handler= (ConnectionHandler) connections.getClients().get(connectionId);
        if(protocol.getProfile()==null || !protocol.getProfile().isLoggedIn()) {
            ErrorMessage error = new ErrorMessage(3);
            handler.send(error);
        }
        else{
            protocol.getProfile().LogOut();
            handler.send(new ACKMessage(3));
        }
    }

    private void process4(Message msg,int connectionId,BidiMessagingProtocolImpl protocol ){
        ConnectionHandler handler= (ConnectionHandler) connections.getClients().get(connectionId);
        if(protocol.getProfile()==null || !protocol.getProfile().isLoggedIn()) {
            ErrorMessage error = new ErrorMessage(4);
            handler.send(error);
        }
        else if (usernametoprofileList.get(((FollowMessage) msg).getUserName())==null) {
            ErrorMessage error = new ErrorMessage(4);
            handler.send(error);
        }
        else{
            FollowMessage followMessage = (FollowMessage) msg;
            Queue<Profile> following = protocol.getProfile().getFollowing();
            if (followMessage.isFollowOrUnfollow()) { //FOLLOW
                if (following.contains(usernametoprofileList.get(followMessage.getUserName()))) {
                    ErrorMessage error = new ErrorMessage(4);
                    handler.send(error);
                } else {
                    following.add(usernametoprofileList.get(followMessage.getUserName()));
                    usernametoprofileList.get(followMessage.getUserName()).getFollowers().add(protocol.getProfile());
                    handler.send(new FollowACK(4, followMessage.getUserName(),followMessage.isFollowOrUnfollow()));
                }

            } else if (!followMessage.isFollowOrUnfollow()) { //UNFOLLOW
                if (!following.contains(usernametoprofileList.get(followMessage.getUserName()))) {
                    ErrorMessage error = new ErrorMessage(4);
                    handler.send(error);
                } else {
                    following.remove(usernametoprofileList.get(followMessage.getUserName()));
                    usernametoprofileList.get(followMessage.getUserName()).getFollowers().remove(protocol.getProfile());
                    handler.send(new FollowACK(4, followMessage.getUserName(),followMessage.isFollowOrUnfollow()));
                }

            }
        }
    }

    private void process5(Message msg,int connectionId, BidiMessagingProtocolImpl protocol){
        ConnectionHandler handler= (ConnectionHandler) connections.getClients().get(connectionId);
        if(protocol.getProfile()==null || !protocol.getProfile().isLoggedIn() ) {
            ErrorMessage error = new ErrorMessage(5);
            handler.send(error);
        }
        else{
            PostMessage postMessage=(PostMessage) msg;
            NotificationMessage notification=new NotificationMessage(NotificationMessage.Type.Public,
                    protocol.getProfile().getUsername(),postMessage.getContent());
            for(Profile curr: protocol.getProfile().getFollowers()){

                ConnectionHandler currhandler= (ConnectionHandler) connections.getClients().get(curr.getId());
                if(!curr.isLoggedIn()){
                    curr.addMessage(notification);
                }
                else {
                    currhandler.send(notification);
                }
            }
            String content=postMessage.getContent();
            for(int i=0;i<content.length();i++){
                if(content.charAt(i)=='@'){
                    String currcontent=content.substring(i+1,content.length());
                    String username;
                    if(currcontent.indexOf(" ")==-1) {
                        username = currcontent;
                    }
                    else {
                        username = content.substring(i+1, currcontent.indexOf(" "));
                    }

                    if(!protocol.getProfile().getBlockedBy().contains(usernametoprofileList.get(username))) {
                        if (!usernametoprofileList.get(username).isLoggedIn()) {
                            protocol.getProfile().addMessage(notification);
                        } else {
                            ConnectionHandler currhandler = (ConnectionHandler) connections.getClients().get(usernametoprofileList.get(username).getId());
                            currhandler.send(notification);
                        }
                    }
                    i = i + username.length();
                }
            }
            postsAndPMs.add(msg);
            protocol.getProfile().incrementPosts();
            handler.send(new ACKMessage(5));
        }
    }

    private void process6(Message msg,int connectionId, BidiMessagingProtocolImpl protocol){

        ConnectionHandler handler= (ConnectionHandler) connections.getClients().get(connectionId);
        PMMessage pmMessage=(PMMessage) msg;
        if(protocol.getProfile()==null || !protocol.getProfile().isLoggedIn() ||
                usernametoprofileList.get(pmMessage.getUsername())==null ||
                !protocol.getProfile().getFollowing().contains(usernametoprofileList.get(pmMessage.getUsername()))) {
            ErrorMessage error = new ErrorMessage(6);
            handler.send(error);
        }

        else{
            for(String word: forbiddenWords){
                if(pmMessage.getContent().contains(word)){
                    pmMessage.setContent(pmMessage.getContent().replace(word, "<filtered>"));
                }
            }
            ConnectionHandler handler2= (ConnectionHandler) connections.getClients().get(usernametoprofileList.get(pmMessage.getUsername()).getId());
            NotificationMessage notification=new NotificationMessage(NotificationMessage.Type.PM,
                    protocol.getProfile().getUsername(),pmMessage.getContent());
            if(!protocol.getProfile().getBlockedBy().contains(usernametoprofileList.get(pmMessage.getUsername()))) {

                if (!usernametoprofileList.get(pmMessage.getUsername()).isLoggedIn()) {
                    usernametoprofileList.get(pmMessage.getUsername()).addMessage(notification);
                } else {
                    handler2.send(notification);
                }
            }
            postsAndPMs.add(msg);
            handler.send(new ACKMessage(5));

        }
    }

    private void process7(Message msg,int connectionId,BidiMessagingProtocolImpl protocol){
        ConnectionHandler handler= (ConnectionHandler) connections.getClients().get(connectionId);
        if(protocol.getProfile()==null || !protocol.getProfile().isLoggedIn()){
                ErrorMessage error = new ErrorMessage(7);
                handler.send(error);

        }
        else
        {
            for(Map.Entry<String,Profile> entry:usernametoprofileList.entrySet()){
                if(entry.getValue().isLoggedIn()&& !protocol.getProfile().getBlockedBy().contains(entry.getValue())
                        && !entry.getValue().getBlockedBy().contains(protocol.getProfile())){
                    int age=Period.between(entry.getValue().getBirthday(),LocalDate.now()).getYears();
                    handler.send(new LogstatACK(7,age,entry.getValue().getPostsCounter(),
                            entry.getValue().getFollowers().size(),entry.getValue().getFollowing().size()));
                }
            }

        }

    }

    private void process8(Message msg,int connectionId, BidiMessagingProtocolImpl protocol){
        ConnectionHandler handler= (ConnectionHandler) connections.getClients().get(connectionId);
        if(protocol.getProfile()==null || !protocol.getProfile().isLoggedIn()){
            ErrorMessage error = new ErrorMessage(8);
            handler.send(error);
        }
        else{
            StatMessage statMessage=(StatMessage) msg;
            boolean terminate=false;
            for(String username: statMessage.getUsernames()){
                if(usernametoprofileList.get(username)==null){
                    ErrorMessage error = new ErrorMessage(8);
                    handler.send(error);
                    terminate=true;
                }
            }
            if(!terminate) {
                for (String username : statMessage.getUsernames()) {
                    Profile curr = usernametoprofileList.get(username);
                    if (protocol.getProfile().getBlockedBy().contains(curr) || curr.getBlockedBy().contains(protocol.getProfile())) {
                        ErrorMessage error = new ErrorMessage(8);
                        handler.send(error);
                    } else {
                        int age = Period.between(curr.getBirthday(), LocalDate.now()).getYears();
                        handler.send(new StatACK(8, age, curr.getPostsCounter(), curr.getFollowers().size(), curr.getFollowing().size()));
                    }
                }
            }
        }

    }



    private void process12(Message msg,int connectionId, BidiMessagingProtocolImpl protocol){

        ConnectionHandler handler= (ConnectionHandler) connections.getClients().get(connectionId);
        BlockMessage blockMessage= (BlockMessage) msg;
        if(usernametoprofileList.get(blockMessage.getUsername())==null)
        {
            handler.send(new ErrorMessage(12));
        }
        else
        {
            Profile blockedProfile=usernametoprofileList.get(blockMessage.getUsername());
            blockedProfile.addBlocker(protocol.getProfile());
            blockedProfile.getFollowers().remove(protocol.getProfile());
            protocol.getProfile().getFollowing().remove(blockedProfile);
            blockedProfile.getFollowing().remove(protocol.getProfile());
            protocol.getProfile().getFollowers().remove(blockedProfile);
        }

    }


}
