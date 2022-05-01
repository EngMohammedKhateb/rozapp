package rozapp.roz.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RefreshInfo {

    @SerializedName("pk")
    @Expose
    private String pk;
    @SerializedName("min")
    @Expose
    private String min;
    @SerializedName("max")
    @Expose
    private String max;
    @SerializedName("block")
    @Expose
    private String block;

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }
}
