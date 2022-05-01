package rozapp.roz.app.adabters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.home.ImageViewrActivity;
import rozapp.roz.app.models.Media;

public class ImageAdabter extends RecyclerView.Adapter<ImageAdabter.ImageViewHolder> {

    private List<Media> images;
    private List<Media> fullimages;
    private Context context;

    public ImageAdabter(Context context,List<Media> images){
        this.context=context;
        this.images=images;
        fullimages=new ArrayList<>();
    }

    @NonNull
    @Override
    public ImageAdabter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_image,parent ,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdabter.ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Picasso.with(context).load(Constants.Image_URL+images.get(position).getSrc()).into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(Media image:images){
                   fullimages.add(new Media(image.getId(),Constants.Image_URL+image.getSrc()));
                }

                context.startActivity(new Intent(context, ImageViewrActivity.class)
                        .putExtra("image",Constants.Image_URL+images.get(position).getSrc().toString())
                        .putExtra("album",(Serializable) fullimages)
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        RoundedImageView image;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
