package rozapp.roz.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatCategory {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("selected")
    @Expose
    private String selected;
    @SerializedName("gifts")
    @Expose
    private List<ChatGift> gifts = null;

    public ChatCategory(ChatCategory chatCategory) {

    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public List<ChatGift> getGifts() {
        return gifts;
    }

    public void setGifts(List<ChatGift> gifts) {
        this.gifts = gifts;
    }

}
