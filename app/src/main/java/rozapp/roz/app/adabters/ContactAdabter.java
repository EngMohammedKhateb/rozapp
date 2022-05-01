package rozapp.roz.app.adabters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rozapp.roz.app.R;
import rozapp.roz.app.chat.ChatActivity;
import rozapp.roz.app.events.DeleteContactEvent;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.Contact;
import rozapp.roz.app.models.ErrorHandler;

public class ContactAdabter extends RecyclerView.Adapter<ContactAdabter.ContactViewHolder> implements Filterable {

    private Context context;

    private List<Contact> movieListFiltered;
    private List<Contact> movieList;
    private AuthResponse authResponse;
    Dialog dialog;

    public ContactAdabter(Context context,List<Contact> exampleList){
        this.context=context;
        this.movieList = exampleList;
        this.movieListFiltered = movieList;
        authResponse=new CallData(context).getAuthResponse();
        dialog=new Dialog(context);
    }
    public void setMovieList(Context context,final List<Contact> movieList){
        this.context = context;
        if(this.movieList == null){
            this.movieList = movieList;
            this.movieListFiltered = movieList;
            notifyItemChanged(0, movieListFiltered.size());
        } else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return ContactAdabter.this.movieList.size();
                }

                @Override
                public int getNewListSize() {
                    return movieList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return ContactAdabter.this.movieList.get(oldItemPosition).getName() == movieList.get(newItemPosition).getName();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                    Contact newMovie = ContactAdabter.this.movieList.get(oldItemPosition);

                    Contact oldMovie = movieList.get(newItemPosition);

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
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_contact,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Contact contact = movieListFiltered.get(position);
        Picasso.with(context).load(Constants.Image_URL+contact.getImage()).into(holder.leading);
        holder.title.setText(contact.getName());
        holder.subtitle.setText(KhateebPattern.LaravelDate(contact.getUpdatedAt()));
        if(contact.getOnline().equals("0")){
            holder.status_dot.setBackground(context.getResources().getDrawable(R.drawable.circle_grey));
        }else{
            holder.status_dot.setBackground(context.getResources().getDrawable(R.drawable.circlegreen));
        }
        if(contact.getNotReaded()==0){
            holder.count_container.setVisibility(View.GONE);
        }else{
            holder.count_container.setVisibility(View.VISIBLE);
            holder.count.setText(contact.getNotReaded()+"");
        }

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(authResponse.getUser().getName().equals("guest")   ){
                   return;
                }
                contact.setNotReaded(0);
                notifyDataSetChanged();
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("target",(Serializable) contact);
                context.startActivity(intent);
            }
        });

        holder.row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(authResponse.getUser().getName().equals("guest") || authResponse.getUser().getType().equals("client")){
                    return false;
                }
                confirm(position);
                return false;
            }
        });
    }

    private void confirm(int position){
        Button btn_delete , btn_close;
        TextView tv;


        dialog.setContentView(R.layout.confirm);

        btn_close = (Button) dialog.findViewById(R.id.close_btn);
        btn_delete = (Button) dialog.findViewById(R.id.delete_btn);
        tv=(TextView) dialog.findViewById(R.id.dialog_message);
        tv.setText("do you want to delete this conversation");
        btn_delete.setText("delete");

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int contact_id = movieListFiltered.get(position).getId();

                EventBus.getDefault().post(new DeleteContactEvent(contact_id));

                dialog.dismiss();
            }});

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public int getItemCount() {
         if(movieList != null){
            return movieListFiltered.size();
        } else {
            return 0;
        }
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.leading)
        RoundedImageView leading;
        @BindView(R.id.row )
        CardView row;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.subtitle)
        TextView subtitle;
        @BindView(R.id.status_dot)
        RelativeLayout status_dot;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.count_container)
        RelativeLayout count_container;

        public ContactViewHolder(@NonNull View itemView) {
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
                    List<Contact> filteredList = new ArrayList<>();
                    for (Contact movie : movieList) {
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
                movieListFiltered = (ArrayList<Contact>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
