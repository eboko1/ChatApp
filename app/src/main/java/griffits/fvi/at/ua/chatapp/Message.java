package griffits.fvi.at.ua.chatapp;

/**
 * Created by Vika on 24.07.2017.
 */


public class Message {
    private String text;
    private String userName;
    private String photoURL;

    // for firebase
    public Message(){
    }

    public Message(String text, String userName, String photoURL) {
        this.text = text;
        this.userName = userName;
        this.photoURL = photoURL;
    }

    public String getText() {
        return text;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
}
