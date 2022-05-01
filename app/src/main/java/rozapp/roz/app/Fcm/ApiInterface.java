package rozapp.roz.app.Fcm;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    @Headers({"Authorization: key=AAAAKBAa96g:APA91bG4KWJez8i_CO2DBVtNDK7GGAiHirs0ipJpnfJS40Q7GPRBSMPGG0jl4r20CR-Z04t-zE-6CkUctV7qLB9UVfEsnqjo2fxZao9ROceov1pTxCm5Eq1CvW67jX_AbuQb4kEThbVl", "Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body RootModel root);
}
