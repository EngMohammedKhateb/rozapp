package rozapp.roz.app.adabters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.ChatMessage;
import rozapp.roz.app.models.ErrorHandler;
import rozapp.roz.app.models.GiftMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatAdabter extends RecyclerView.Adapter<ChatAdabter.ChatViewHolder> {

    private Context context;
    private List<ChatMessage> messages;

    private AuthResponse authResponse;
    Dialog dialog;
    public ChatAdabter(Context context, List<ChatMessage> messages) {
        this.context = context;
        this.messages = messages;
        authResponse=new CallData(context).getAuthResponse();
        dialog=new Dialog(context);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_message,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int user_id=authResponse.getUser().getId();
        if (messages.get(position).getFromUser() == user_id ){

            holder.row_my_msg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    alertToDelete(position);
                    return false;
                }
            });

            if(messages.get(position).getMsgType().equals("gift")){
                holder.row_my_msg.setVisibility(View.GONE);
                holder.row_friend_msg.setVisibility(View.GONE);
                holder.row_frien_gift_msg.setVisibility(View.GONE);
                holder.row_my_misscall.setVisibility(View.GONE);
                holder.row_misscall.setVisibility(View.GONE);
                holder.row_my_gift_msg.setVisibility(View.VISIBLE);

                Gson gson=new Gson();
                GiftMessage gift= gson.fromJson(messages.get(position).getMessage().toString(), GiftMessage.class);

                holder.coins.setText(" "+gift.getCoins());
                Picasso.with(context).load(Constants.Image_URL_Slashed+gift.getImage()).into(holder.my_gift_image);

            }else if(messages.get(position).getMsgType().equals("missed_call")){
                holder.row_my_msg.setVisibility(View.GONE);
                holder.row_friend_msg.setVisibility(View.GONE);
                holder.row_frien_gift_msg.setVisibility(View.GONE);
                holder.row_my_gift_msg.setVisibility(View.GONE);
                holder.row_my_misscall.setVisibility(View.VISIBLE);
                holder.row_misscall.setVisibility(View.GONE);

            }else{
                holder.row_my_msg.setVisibility(View.VISIBLE);
                holder.row_friend_msg.setVisibility(View.GONE);
                holder.row_frien_gift_msg.setVisibility(View.GONE);
                holder.row_my_gift_msg.setVisibility(View.GONE);
                holder.row_my_misscall.setVisibility(View.GONE);
                holder.row_misscall.setVisibility(View.GONE);
                holder.my_name_tv.setText("You");
                if(messages.get(position).getUpdatedAt()==null){
                    holder.my_data_tv.setText("");
                }else{
                    holder.my_data_tv.setText(KhateebPattern.LaravelDate(messages.get(position).getUpdatedAt().toString()));
                }
                holder.my_msg_tv.setText(messages.get(position).getMessage());
            }

        }else{

            if(messages.get(position).getMsgType().equals("gift")){
                holder.row_my_msg.setVisibility(View.GONE);
                holder.row_friend_msg.setVisibility(View.GONE);
                holder.row_frien_gift_msg.setVisibility(View.VISIBLE);
                holder.row_my_gift_msg.setVisibility(View.GONE);
                holder.row_my_misscall.setVisibility(View.GONE);
                holder.row_misscall.setVisibility(View.GONE);
                Gson gson=new Gson();
                GiftMessage gift= gson.fromJson(messages.get(position).getMessage().toString(), GiftMessage.class);

                holder.frined_coins.setText(" "+gift.getCoins());
                Picasso.with(context).load(Constants.Image_URL_Slashed+gift.getImage()).into(holder.freind_gift_image);

            }else if(messages.get(position).getMsgType().equals("missed_call")){
                holder.row_my_msg.setVisibility(View.GONE);
                holder.row_friend_msg.setVisibility(View.GONE);
                holder.row_frien_gift_msg.setVisibility(View.GONE);
                holder.row_my_gift_msg.setVisibility(View.GONE);
                holder.row_my_misscall.setVisibility(View.GONE);
                holder.row_misscall.setVisibility(View.VISIBLE);

            }else{
                holder.row_friend_msg.setVisibility(View.VISIBLE);
                holder.row_my_msg.setVisibility(View.GONE);
                holder.row_frien_gift_msg.setVisibility(View.GONE);
                holder.row_my_gift_msg.setVisibility(View.GONE);
                holder.row_my_misscall.setVisibility(View.GONE);
                holder.row_misscall.setVisibility(View.GONE);
                holder.friend_name_tv.setText(messages.get(position).getFriend_name());
                if(messages.get(position).getUpdatedAt()==null){
                    holder.freind_msg_date_tv.setText("");
                }else{
                    holder.freind_msg_date_tv.setText(KhateebPattern.LaravelDate(messages.get(position).getUpdatedAt().toString()));
                }

                holder.freind_msg.setText(messages.get(position).getMessage());
            }


        }

    }

    private void alertToDelete(int position) {


        Button  btn_delete , btn_close;


        dialog.setContentView(R.layout.confirm);

        btn_close = (Button) dialog.findViewById(R.id.close_btn);
        btn_delete = (Button) dialog.findViewById(R.id.delete_btn);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String idtod = messages.get(position).getId() + "";
                messages.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, messages.size());

                KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).deleteMessage(idtod).enqueue(new Callback<ErrorHandler>() {
                    @Override
                    public void onResponse(Call<ErrorHandler> call, Response<ErrorHandler> response) {
                        if(response.code()==200){
                            DynamicToast.makeSuccess(context,"message deleted successfully").show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ErrorHandler> call, Throwable t) {

                    }
                });

                dialog.dismiss();
            }});

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.freind_msg)
        TextView freind_msg;
        @BindView(R.id.my_msg_tv)
        TextView my_msg_tv;
        @BindView(R.id.friend_name_tv)
        TextView friend_name_tv;
        @BindView(R.id.my_name_tv)
        TextView my_name_tv;
        @BindView(R.id.my_data_tv)
        TextView my_data_tv;
        @BindView(R.id.freind_msg_date_tv)
        TextView freind_msg_date_tv;
        @BindView(R.id.frined_coins)
        TextView frined_coins;
        @BindView(R.id.coins)
        TextView coins;
        @BindView(R.id.row_my_msg)
        LinearLayout row_my_msg;
        @BindView(R.id.row_friend_msg)
        LinearLayout row_friend_msg;
        @BindView(R.id.row_my_gift_msg)
        LinearLayout row_my_gift_msg;
        @BindView(R.id.row_frien_gift_msg)
        LinearLayout row_frien_gift_msg;
        @BindView(R.id.freind_gift_image)
        ImageView freind_gift_image;
        @BindView(R.id.my_gift_image)
        ImageView my_gift_image;
        @BindView(R.id.row_my_misscall)
        LinearLayout row_my_misscall;
        @BindView(R.id.row_misscall)
        LinearLayout row_misscall;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
    }
}
