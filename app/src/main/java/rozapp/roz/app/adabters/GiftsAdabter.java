package rozapp.roz.app.adabters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.models.MyGifys;

public class GiftsAdabter extends RecyclerView.Adapter<GiftsAdabter.GiftViewHolder> {

    private Context context;
    private List<MyGifys> gifts;

    public GiftsAdabter(Context context, List<MyGifys> gifts) {
        this.context = context;
        this.gifts = gifts;
    }

    @NonNull
    @Override
    public GiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GiftViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gift,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull GiftViewHolder holder, int position) {

        Picasso.with(context).load(Constants.Image_URL_Slashed+gifts.get(position).getImage()).into(holder.image);
        holder.coins.setText(" "+gifts.get(position).getCoins()+" token");
        holder.count.setText("x"+gifts.get(position).getCount());
    }

    @Override
    public int getItemCount() {
        return gifts.size();
    }

    public class GiftViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        RoundedImageView image;
        @BindView(R.id.coins)
        TextView coins;
        @BindView(R.id.count)
        TextView count;

        public GiftViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
