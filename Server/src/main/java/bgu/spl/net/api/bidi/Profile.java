package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Message;

import java.time.LocalDate;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Profile {
    private String username;
    private String password;
    private int id;
    private Queue<Profile> following;
    private Queue<Profile> followers;
    private boolean isLoggedIn=false;
    private LocalDate birthday;
//    private int age= Period.between(birthday,LocalDate.now()).getYears();
    private int postsCounter=0;
    private Queue<Message> awaitingMessages;
    private Queue<Profile> blockedBy;

    public Profile(String username,String password,String birthday, int id) {
        this.username=username;
        this.password = password;
        this.id = id;
        this.following= new ConcurrentLinkedQueue<>();
        this.followers= new ConcurrentLinkedQueue<>();
        String[] bday= birthday.split("-",3);
        this.birthday= LocalDate.of(Integer.parseInt(bday[2]),Integer.parseInt(bday[1]),Integer.parseInt(bday[0]));
        this.awaitingMessages= new ConcurrentLinkedQueue<>();
        this.blockedBy= new ConcurrentLinkedQueue<>();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Queue<Profile> getFollowing() {
        return following;
    }

    public void setFollowing(Queue<Profile> following) {
        this.following = following;
    }

    public void LogIn(){
        isLoggedIn=true;
    }

    public void LogOut(){
        isLoggedIn=false;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public String getUsername() {
        return username;
    }

    public Queue<Profile> getFollowers() {
        return followers;
    }

    public void incrementPosts()
    {
        postsCounter++;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public int getPostsCounter() {
        return postsCounter;
    }

    public void addMessage(Message msg)
    {
        awaitingMessages.add(msg);
    }

    public void addBlocker(Profile profile){
        blockedBy.add(profile);
    }

    public Queue<Profile> getBlockedBy() {
        return blockedBy;
    }

    public Queue<Message> getAwaitingMessages() {
        return awaitingMessages;
    }

    public void clearAwaitingMessages()
    {
        awaitingMessages.clear();
    }
}
