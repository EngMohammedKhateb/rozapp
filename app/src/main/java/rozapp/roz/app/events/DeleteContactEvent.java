package rozapp.roz.app.events;

public class DeleteContactEvent {

    int contact_id;

    public DeleteContactEvent(int contact_id) {
        this.contact_id = contact_id;
    }

    public int getContact_id() {
        return contact_id;
    }

    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }
}
