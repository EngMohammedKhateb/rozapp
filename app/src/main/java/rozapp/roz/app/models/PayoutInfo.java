package rozapp.roz.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PayoutInfo {
    @SerializedName("min")
    @Expose
    private String min;
    @SerializedName("coins_usd")
    @Expose
    private String coinsUsd;
    @SerializedName("gates")
    @Expose
    private List<Gate> gates = new ArrayList<>();

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getCoinsUsd() {
        return coinsUsd;
    }

    public void setCoinsUsd(String coinsUsd) {
        this.coinsUsd = coinsUsd;
    }

    public List<Gate> getGates() {
        return gates;
    }

    public void setGates(List<Gate> gates) {
        this.gates = gates;
    }
}
