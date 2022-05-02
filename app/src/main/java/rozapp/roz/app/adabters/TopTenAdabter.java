package rozapp.roz.app.adabters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.models.TopUser;
import rozapp.roz.app.profile.TargetProfileActivity;

public class TopTenAdabter extends RecyclerView.Adapter<TopTenAdabter.TopTenViewHolder> {

    private Context context;
    private List<TopUser> users;

    public TopTenAdabter(Context context, List<TopUser> topUsers) {
        this.context=context;
        this.users=topUsers;
    }


    @NonNull
    @Override
    public TopTenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TopTenViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_top_user,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TopTenViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.name.setText(users.get(position).getName());
        holder.number.setText(position+4+"");
        holder.coins.setText(users.get(position).getRate()+"");
        Picasso.with(context).load(Constants.Image_URL+users.get(position).getImage()).into(holder.image);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, TargetProfileActivity.class).putExtra("target_id",users.get(position).getId()+""));
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class TopTenViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.number)
        TextView number;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.coins)
        TextView coins;
        @BindView(R.id.image)
        CircleImageView image;
        @BindView(R.id.root)
        LinearLayout root;


        public TopTenViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
