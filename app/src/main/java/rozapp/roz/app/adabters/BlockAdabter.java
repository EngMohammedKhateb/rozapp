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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.DashboardUser;
import rozapp.roz.app.models.ErrorHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockAdabter extends RecyclerView.Adapter<BlockAdabter.ViewHolder> {

    private Context context;
    private List<DashboardUser> users;
    Dialog dialog;
    AuthResponse authResponse;
    public BlockAdabter(Context context, List<DashboardUser> users) {
        this.context = context;
        this.users = users;
        dialog=new Dialog(context);
        authResponse=new CallData(context).getAuthResponse();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_follow, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.name_tv.setText(users.get(position).getName());
        Picasso.with(context).load(Constants.Image_URL+users.get(position).getImage()).into(holder.image);

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    private void confirm(int position){
        Button btn_delete , btn_close;
        TextView tv;


        dialog.setContentView(R.layout.confirm);

        btn_close = (Button) dialog.findViewById(R.id.close_btn);
        btn_delete = (Button) dialog.findViewById(R.id.delete_btn);
        tv=(TextView) dialog.findViewById(R.id.dialog_message);
        tv.setText("do you want to unblock this user");
        btn_delete.setText("unblock user");

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String idtod = users.get(position).getId() + "";
                users.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, users.size());

                KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).unBlockUser(idtod).enqueue(new Callback<ErrorHandler>() {
                    @Override
                    public void onResponse(Call<ErrorHandler> call, Response<ErrorHandler> response) {
                        if(response.code()==200){
                            DynamicToast.makeSuccess(context,response.body().getMessage()).show();
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row)
        CardView row;
        @BindView(R.id.image)
        CircleImageView image;
        @BindView(R.id.name_tv)
        TextView name_tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
