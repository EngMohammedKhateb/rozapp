package rozapp.roz.app.events;

public class RefreshDashboardEvent {

    String message;

    public RefreshDashboardEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
