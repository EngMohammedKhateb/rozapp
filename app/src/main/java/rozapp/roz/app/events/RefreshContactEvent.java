package rozapp.roz.app.events;

public class RefreshContactEvent {


    String message;

    public RefreshContactEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
