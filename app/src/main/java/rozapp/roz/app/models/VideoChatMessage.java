package rozapp.roz.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoChatMessage {


    @SerializedName("from_id")
    @Expose
    String from_id;

    @SerializedName("from_name")
    @Expose
    String from_name;


    @SerializedName("to_id")
    @Expose
    String to_id;

    @SerializedName("to_name")
    @Expose
    String to_name;

    @SerializedName("message")
    @Expose
    String message;

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getTo_id() {
        return to_id;
    }

    public void setTo_id(String to_id) {
        this.to_id = to_id;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
