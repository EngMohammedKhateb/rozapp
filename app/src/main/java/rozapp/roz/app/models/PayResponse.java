package rozapp.roz.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayResponse {

    @SerializedName("status")
    @Expose
    String status;

    @SerializedName("message")
    @Expose
    String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
