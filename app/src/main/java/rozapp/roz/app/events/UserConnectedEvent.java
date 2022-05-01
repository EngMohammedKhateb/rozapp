package rozapp.roz.app.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserConnectedEvent {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("email_verified_at")
    @Expose
    private Object emailVerifiedAt;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("coins")
    @Expose
    private String coins;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("online")
    @Expose
    private String online;
    @SerializedName("block")
    @Expose
    private String block;
    @SerializedName("coins_per_minute")
    @Expose
    private int coinsPerMinute;
    @SerializedName("disable_chat")
    @Expose
    private int disableChat;
    @SerializedName("disable_video")
    @Expose
    private int disableVideo;
    @SerializedName("messages")
    @Expose
    private int messages;
    @SerializedName("in_call")
    @Expose
    private int inCall;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("statue")
    @Expose
    private String statue;
    @SerializedName("country_name")
    @Expose
    private String countryName;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("country_image")
    @Expose
    private String countryImage;
    @SerializedName("gender")
    @Expose
    private int gender;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("role_id")
    @Expose
    private int roleId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(Object emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public int getCoinsPerMinute() {
        return coinsPerMinute;
    }

    public void setCoinsPerMinute(int coinsPerMinute) {
        this.coinsPerMinute = coinsPerMinute;
    }

    public int getDisableChat() {
        return disableChat;
    }

    public void setDisableChat(int disableChat) {
        this.disableChat = disableChat;
    }

    public int getDisableVideo() {
        return disableVideo;
    }

    public void setDisableVideo(int disableVideo) {
        this.disableVideo = disableVideo;
    }

    public int getMessages() {
        return messages;
    }

    public void setMessages(int messages) {
        this.messages = messages;
    }

    public int getInCall() {
        return inCall;
    }

    public void setInCall(int inCall) {
        this.inCall = inCall;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryImage() {
        return countryImage;
    }

    public void setCountryImage(String countryImage) {
        this.countryImage = countryImage;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
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

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }


}
