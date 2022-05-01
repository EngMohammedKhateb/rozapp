package rozapp.roz.app.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import rozapp.roz.app.models.AuthUser;

public class IncomingRequestEvent {

    @SerializedName("requester_id")
    @Expose
    private String requester_id;
    @SerializedName("requested_id")
    @Expose
    private String requested_id;
    @SerializedName("requester")
    @Expose
    private AuthUser requester;
    @SerializedName("requested")
    @Expose
    private AuthUser requested;

    public String getRequester_id() {
        return requester_id;
    }

    public void setRequester_id(String requester_id) {
        this.requester_id = requester_id;
    }

    public String getRequested_id() {
        return requested_id;
    }

    public void setRequested_id(String requested_id) {
        this.requested_id = requested_id;
    }

    public AuthUser getRequester() {
        return requester;
    }

    public void setRequester(AuthUser requester) {
        this.requester = requester;
    }

    public AuthUser getRequested() {
        return requested;
    }

    public void setRequested(AuthUser requested) {
        this.requested = requested;
    }
}
