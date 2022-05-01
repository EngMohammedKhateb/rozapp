package rozapp.roz.app.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import rozapp.roz.app.models.AuthUser;

public class CallClosedEvent {

    @SerializedName("requester_id")
    @Expose
    private int requester_id;
    @SerializedName("requested_id")
    @Expose
    private int requested_id;
    @SerializedName("closer")
    @Expose
    private AuthUser closer;

    public AuthUser getCloser() {
        return closer;
    }

    public void setCloser(AuthUser closer) {
        this.closer = closer;
    }

    public int getRequester_id() {
        return requester_id;
    }

    public void setRequester_id(int requester_id) {
        this.requester_id = requester_id;
    }

    public int getRequested_id() {
        return requested_id;
    }

    public void setRequested_id(int requested_id) {
        this.requested_id = requested_id;
    }
}
