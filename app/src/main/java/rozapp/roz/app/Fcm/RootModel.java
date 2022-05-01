package rozapp.roz.app.Fcm;

import com.google.gson.annotations.SerializedName;

public class RootModel {


        @SerializedName("to") //  "to" changed to token
        private String to;

        @SerializedName("data")
        private DataModel data;

        public RootModel(String to , DataModel data) {
            this.to = to;
            this.data = data;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String token) {
            this.to = token;
        }


        public DataModel getData() {
            return data;
        }

        public void setData(DataModel data) {
            this.data = data;
        }

}
