package rozapp.roz.app.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoGiftEvent {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("from_user")
    @Expose
    private int from_user;

    @SerializedName("to_user")
    @Expose
    private int to_user;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("payload")
    @Expose
    private String payload;

    @SerializedName("coins")
    @Expose
    private String coins;

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFrom_user() {
        return from_user;
    }

    public void setFrom_user(int from_user) {
        this.from_user = from_user;
    }

    public int getTo_user() {
        return to_user;
    }

    public void setTo_user(int to_user) {
        this.to_user = to_user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
