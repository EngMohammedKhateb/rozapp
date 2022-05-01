package rozapp.roz.app.adabters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rozapp.roz.app.R;
import rozapp.roz.app.chat.ChatActivity;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.Contact;
import rozapp.roz.app.models.DashboardUser;

public class AllUsersAdabter extends RecyclerView.Adapter<AllUsersAdabter.ViewHolder>  implements Filterable {

    private Context context;

    private List<DashboardUser> movieListFiltered;
    private List<DashboardUser> movieList;


    private AuthResponse authResponse;

    public AllUsersAdabter(Context context, List<DashboardUser> users){
        this.context=context;
        this.movieList = users;
        this.movieListFiltered = movieList;
        authResponse=new CallData(context).getAuthResponse();
    }
    public void setMovieList(Context context,final List<DashboardUser> movieList){
        this.context = context;
        if(this.movieList == null){
            this.movieList = movieList;
            this.movieListFiltered = movieList;
            notifyItemChanged(0, movieListFiltered.size());
        } else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return AllUsersAdabter.this.movieList.size();
                }

                @Override
                public int getNewListSize() {
                    return movieList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return AllUsersAdabter.this.movieList.get(oldItemPosition).getName() == movieList.get(newItemPosition).getName();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                    DashboardUser newMovie = AllUsersAdabter.this.movieList.get(oldItemPosition);

                    DashboardUser oldMovie = movieList.get(newItemPosition);

                    return newMovie.getName() == oldMovie.getName() ;
                }
            });
            this.movieList = movieList;
            this.movieListFiltered = movieList;
            result.dispatchUpdatesTo(this);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_tile,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DashboardUser user=movieListFiltered.get(position);
        Picasso.with(context).load(Constants.Image_URL_Slashed+user.getImage()).into(holder.iv);
        holder.tv.setText(user.getName().toString());


        Contact contact=new Contact(user.getId(),user.getName(),user.getType(),user.getCoins(),user.getOnline(),user.getCoinsPerMinute(),user.getImage(),user.getStatue());

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(authResponse.getUser().getName().equals("guest")){
                    return;
                }
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("target",(Serializable) contact);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(movieList != null){
            return movieListFiltered.size();
        } else {
            return 0;
        }
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


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    movieListFiltered = movieList;
                } else {
                    List<DashboardUser> filteredList = new ArrayList<>();
                    for (DashboardUser movie : movieList) {
                        if (movie.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(movie);
                        }
                    }
                    movieListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = movieListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                movieListFiltered = (ArrayList<DashboardUser>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
