package rozapp.roz.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthResponse {


    @SerializedName("error")
    @Expose
    private boolean error;
    @SerializedName("pk")
    @Expose
    private String pk;
    @SerializedName("min")
    @Expose
    private String min;
    @SerializedName("max")
    @Expose
    private String max;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("user")
    @Expose
    private AuthUser user;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public AuthUser getUser() {
        return user;
    }

    public void setUser(AuthUser user) {
        this.user = user;
    }

}

