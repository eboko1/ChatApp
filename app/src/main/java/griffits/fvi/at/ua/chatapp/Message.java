package griffits.fvi.at.ua.chatapp;

/**
 * Created by Vika on 24.07.2017.
 */


public class Message {
    private String text;

    // for firebase
    public Message(){
    }

    public Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
