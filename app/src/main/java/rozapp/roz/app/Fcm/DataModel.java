package rozapp.roz.app.Fcm;

public class DataModel {

    int id;
    String name;
    String type;
    String image;
    String online="1";

    public DataModel(int id,String type, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.type=type;
        this.online="1";
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
