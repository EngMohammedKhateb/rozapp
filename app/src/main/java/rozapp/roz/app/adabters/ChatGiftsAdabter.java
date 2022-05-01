package rozapp.roz.app.adabters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.models.ChatGift;

public class ChatGiftsAdabter extends RecyclerView.Adapter<ChatGiftsAdabter.GiftsViewHolder> {

    private Context context;
    private List<ChatGift> data;

    public ChatGiftsAdabter(Context context, List<ChatGift> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public GiftsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GiftsViewHolder(LayoutInflater.from(context).inflate(R.layout.row_chat_gift,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull GiftsViewHolder holder, int position) {

        holder.coins.setText(" "+data.get(position).getCoins()+" ");
        Picasso.with(context).load(Constants.Image_URL_Slashed+data.get(position).getImage()).into(holder.image);
        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(data.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class GiftsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.coins)
        TextView coins;
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.row)
        RelativeLayout row;
        public GiftsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
