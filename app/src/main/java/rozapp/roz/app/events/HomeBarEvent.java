package rozapp.roz.app.events;

public class HomeBarEvent {

    String statue;

    public HomeBarEvent(String statue) {
        this.statue = statue;
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }
}
