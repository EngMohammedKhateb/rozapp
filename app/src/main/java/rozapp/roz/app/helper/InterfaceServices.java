package rozapp.roz.app.helper;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.AuthedUserProfile;
import rozapp.roz.app.models.ChatCategory;
import rozapp.roz.app.models.ChatMessage;
import rozapp.roz.app.models.Contact;
import rozapp.roz.app.models.DashboardUser;
import rozapp.roz.app.models.ErrorHandler;
import rozapp.roz.app.models.Media;
import rozapp.roz.app.models.MyGifys;
import rozapp.roz.app.models.PayResponse;
import rozapp.roz.app.models.PayoutInfo;
import rozapp.roz.app.models.Plan;
import rozapp.roz.app.models.RefreshInfo;
import rozapp.roz.app.models.TargetProfile;
import rozapp.roz.app.models.TargetProfileGifts;
import rozapp.roz.app.models.TopUser;


public interface InterfaceServices {


    //////////////////// Authentication /////////////


    @POST("register")
    @FormUrlEncoded
    Call<AuthResponse> createAccount(@Field("name") String name,
                                     @Field("email") String email,
                                     @Field("password") String password,
                                     @Field("gender") String gender,
                                     @Field("country_image") String country_image,
                                     @Field("country_code") String country_code,
                                     @Field("country_name") String country_name,
                                     @Field("image") String image
                                     );

    @POST("login")
    @FormUrlEncoded
    Call<AuthResponse> Login(@Field("email") String email, @Field("password") String password);

    @POST("report/store")
    @FormUrlEncoded
    Call<ErrorHandler> report(@Field("target_id") String target_id, @Field("description") String description);


    @POST("check/account/exists")
    @FormUrlEncoded
    Call<AuthResponse> checkAccount(@Field("email") String email );


    @POST("confirm/account")
    @FormUrlEncoded
    Call<ErrorHandler> activeAccount(@Field("email") String email, @Field("digits") String digits);

    @POST("resend/otp")
    @FormUrlEncoded
    Call<ResponseBody> sendOtp(@Field("email") String email);


    //logout
    @GET("logout")
    Call<ResponseBody> logOut();





    //guest
    @GET("guest/login")
    Call<AuthResponse> guestLogin();
    //privacy
    @GET("privacy/policy")
    Call<ErrorHandler> privacyPolicy();


    //////////////////// Users toggle filters /////////////

    @GET("getToggle/{filter}")
    Call<List<DashboardUser>> getDashboardUsers(@Path("filter") String filter);
    @GET("get/contact")
    Call<List<Contact>> getContacts();
    @GET("get/latest")
    Call<List<DashboardUser>> getLatest();
    @GET("get/user/images")
    Call<List<Media>> getUserImages();
    @GET("get/user/videos")
    Call<List<Media>> getUserVideos();
    @GET("get/user/gifts")
    Call<List<MyGifys>> getUserGifts();
    @GET("get/auth/user/profile")
    Call<AuthedUserProfile> getAuthedUserProfile();
    @GET("get/messages/by/room/{target}")
    Call<List<ChatMessage>> getChatMessages(@Path("target") String target);

    @GET("get/categories/with/gifts")
    Call<List<ChatCategory>> getCategoriesWithGifts();

    @GET("get/target/profile/{id}")
    Call<TargetProfile> getTargetProfile(@Path("id") String id);

    @GET("get/target/media/{id}")
    Call<List<Media>> getTargetMedia(@Path("id") String id);
    @GET("get/target/gifts/{id}")
    Call<List<TargetProfileGifts>> getTargetGifts(@Path("id") String id);
    @GET("toggle/follow/{id}")
    Call<ResponseBody> toggleFollow(@Path("id") String id);

    @POST("change/statue")
    @FormUrlEncoded
    Call<ResponseBody> changeStatue(@Field("statue") String statue );


    @Multipart
    @POST("change/profile/image")
    Call<ResponseBody> changeProfileImage(@Part MultipartBody.Part part);

    //imageuploader
    @Multipart
    @POST("image/uploader/image")
    Call<ErrorHandler> uploadImage(@Part MultipartBody.Part part);

    @Multipart
    @POST("add/image/media")
    Call<ResponseBody> addMediaImage( @Part MultipartBody.Part part);
    @Multipart
    @POST("add/video/media")
    Call<ResponseBody> addMediaVideo( @Part MultipartBody.Part part);

    @POST("change/user/password")
    @FormUrlEncoded
    Call<ResponseBody> changePassword(@Field("password") String password );

    @POST("change/user/name")
    @FormUrlEncoded
    Call<ResponseBody> changeUserName(@Field("name") String name );

    @POST("change/user/coinsper")
    @FormUrlEncoded
    Call<ResponseBody> changeCoinsPerMinute(@Field("coins") int coins );
    @POST("toggle/user/chat")
    @FormUrlEncoded
    Call<ResponseBody> toggleChat(@Field("action") int action );
    @POST("toggle/user/video")
    @FormUrlEncoded
    Call<ResponseBody> toggleVideo(@Field("action") int action );

    @POST("v1/tokens")
    @FormUrlEncoded
    Call<ResponseBody> getStripToken(@Field("card[number]") String number, @Field("card[exp_month]") String month,@Field("card[exp_year]") String year,@Field("card[cvc]") String cvc);

    @POST("android/charge/plan")
    @FormUrlEncoded
    Call<PayResponse> payPlan(@Field("plan") String plan, @Field("stripeToken") String stripeToken, @Field("amount") String amount );

    @POST("request/client")
    @FormUrlEncoded
    Call<ResponseBody> requestClient(@Field("name") String name, @Field("phone") String phone, @Field("birthday") String birthday );

    @GET("get/all/plans")
    Call<List<Plan>> getAllPlans();

    @GET("get/dashboard/by/country/{country}")
    Call<List<DashboardUser>> searchUsers(@Path("country") String country);

    @GET("delete/user/contact/{user_id}")
    Call<ErrorHandler> deleteUserContact(@Path("user_id") String user_id);

    @GET("top/ten")
    Call<List<TopUser>> getTopTen();

    @GET("payout/info")
    Call<PayoutInfo> getPayoutInfo();


    @GET("refresh/general/info")
    Call<RefreshInfo> refreshInfo();

    @POST("payout/request/store")
    @FormUrlEncoded
    Call<ErrorHandler> requestPayout(@Field("gate") String gate, @Field("phone") String phone, @Field("description") String description );

    @POST("delete/message")
    @FormUrlEncoded
    Call<ErrorHandler> deleteMessage(@Field("id") String id );

    @POST("block/user")
    @FormUrlEncoded
    Call<ErrorHandler> blockUser(@Field("id") String id );
    @POST("unblock/user")
    @FormUrlEncoded
    Call<ErrorHandler> unBlockUser(@Field("id") String id );



    @GET("block/list")
    Call<List<DashboardUser>> getBlockList();

    // ephemeral

    @POST("v1/customers")
    Call<ResponseBody> getCustomerId();

    @POST("v1/ephemeral_keys")
    @FormUrlEncoded
    Call<ResponseBody> getEphemeralKey( @Field("customer") String customer);

    @POST("v1/payment_intents")
    @FormUrlEncoded
    Call<ResponseBody> getClientSecret(@Field("customer") String customer,@Field("amount") String amount,@Field("currency") String currency,@Field("automatic_payment_methods[enabled]") String automatic);
}
