package rozapp.roz.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Media implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("src")
    @Expose
    private String src;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Media(int id, String src) {
        this.id = id;
        this.src = src;
    }

    private boolean isblaying=false;

    public boolean isIsblaying() {
        return isblaying;
    }

    public void setIsblaying(boolean isblaying) {
        this.isblaying = isblaying;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
