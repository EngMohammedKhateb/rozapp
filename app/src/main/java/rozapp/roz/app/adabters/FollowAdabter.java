package rozapp.roz.app.adabters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.DashboardUser;
import rozapp.roz.app.profile.TargetProfileActivity;

public class FollowAdabter extends RecyclerView.Adapter<FollowAdabter.ViewHolder> {
    private Context context;
    private List<DashboardUser> users;

    private AuthResponse authResponse;
    public FollowAdabter(Context context, List<DashboardUser> users){
        this.context=context;
        this.users=users;
        authResponse=new CallData(context).getAuthResponse();
    }

    @NonNull
    @Override
    public  ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_follow,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Picasso.with(context).load(Constants.Image_URL_Slashed+users.get(position).getImage()).into(holder.iv);
        holder.tv.setText(users.get(position).getName().toString());


        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(authResponse.getUser().getName().equals("guest")){
                    return;
                }
                context.startActivity(new Intent(context, TargetProfileActivity.class).putExtra("target_id",users.get(position).getId()+""));

            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        CircleImageView iv;
        @BindView(R.id.name_tv)
        TextView tv;
        @BindView(R.id.row)
        CardView row;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
