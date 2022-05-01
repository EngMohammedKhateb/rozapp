package rozapp.roz.app.models;

public class PeerConnectedEvent {

    String message;

    public PeerConnectedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
