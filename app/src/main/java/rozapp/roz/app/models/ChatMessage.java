package rozapp.roz.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatMessage {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("from_user")
    @Expose
    private int fromUser;
    @SerializedName("to_user")
    @Expose
    private int toUser;
    @SerializedName("room_id")
    @Expose
    private String roomId;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("msg_type")
    @Expose
    private String msgType;
    @SerializedName("src")
    @Expose
    private String src;
    @SerializedName("statue")
    @Expose
    private int statue;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("friend_name")
    @Expose
    private String friend_name;


    public ChatMessage(int id, int fromUser, int toUser, String roomId, String message, String msgType, String src, int statue, Object deletedAt, String createdAt, String updatedAt, String friend_name) {
        this.id = id;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.roomId = roomId;
        this.message = message;
        this.msgType = msgType;
        this.src = src;
        this.statue = statue;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.friend_name = friend_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromUser() {
        return fromUser;
    }

    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    public void setFromUser(int fromUser) {
        this.fromUser = fromUser;
    }

    public int getToUser() {
        return toUser;
    }

    public void setToUser(int toUser) {
        this.toUser = toUser;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public int getStatue() {
        return statue;
    }

    public void setStatue(int statue) {
        this.statue = statue;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Object getCreatedAt() {
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
