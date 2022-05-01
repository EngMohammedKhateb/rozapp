package rozapp.roz.app.adabters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.models.DashboardUser;

public class StroyAdabter extends RecyclerView.Adapter<StroyAdabter.StoryViewHolder> {

    private Context context;
    private List<DashboardUser> users;

    public StroyAdabter(Context context, List<DashboardUser> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_story,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {

        Picasso.with(context).load(Constants.Image_URL+users.get(position).getImage()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        CircleImageView image;
        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
