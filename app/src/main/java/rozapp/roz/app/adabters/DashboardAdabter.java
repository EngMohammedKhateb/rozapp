package rozapp.roz.app.adabters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.DashboardUser;
import rozapp.roz.app.profile.TargetProfileActivity;
import rozapp.roz.app.videocall.RequestVideoCall;

public class DashboardAdabter extends RecyclerView.Adapter<DashboardAdabter.DashboardViewHolder> {

    private List<DashboardUser> data;
    private Context context;
    private AuthResponse authResponse;

    public DashboardAdabter(Context context,List<DashboardUser> data){
        this.context=context;
        this.data=data;
        authResponse=new CallData(context).getAuthResponse();
    }

    @NonNull
    @Override
    public DashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         return new DashboardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_grid_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.video_call.setVisibility(View.VISIBLE);

           if (Integer.parseInt(data.get(position).getOnline().toString()) == 1 ) {
               holder.statue_dot.setBackground(context.getResources().getDrawable(R.drawable.circlegreen));

           }else{
               holder.statue_dot.setBackground(context.getResources().getDrawable(R.drawable.circle_grey));

           }
        Picasso.with(context).load(Constants.Image_URL+data.get(position).getImage()).resize(160,200).centerCrop().into(holder.user_image);



        holder.tv_coins.setText(data.get(position).getCoinsPerMinute()+" token/minute");
        holder.name_tv.setText(data.get(position).getName().toString());


        holder.user_image.buildDrawingCache();

        Glide.with(context)
                .asBitmap()
                .load(Constants.Image_URL+data.get(position).getImage())
                .apply(new RequestOptions().override(160,200))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Palette.from(resource)
                                .generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(@Nullable Palette palette) {
                                        Palette.Swatch darkVibrantSwatch = palette.getDarkVibrantSwatch();
                                        Palette.Swatch dominantSwatch = palette.getDominantSwatch();
                                        Palette.Swatch lightVibrantSwatch = palette.getLightVibrantSwatch();


                                        if (darkVibrantSwatch != null) {
                                            try{
                                                holder.tv_coins.setTextColor(palette.getLightVibrantColor(lightVibrantSwatch.getRgb()));
                                                holder.name_tv.setTextColor(palette.getLightVibrantColor(lightVibrantSwatch.getRgb()));
                                                holder.row.setCardBackgroundColor(darkVibrantSwatch.getRgb());
                                            }catch (Exception exception){

                                            }


                                        }else if (dominantSwatch != null) {

                                            holder.row.setCardBackgroundColor(dominantSwatch.getRgb());

                                        }else {

                                            holder.row.setCardBackgroundColor(lightVibrantSwatch.getRgb());

                                        }

                                    }
                                });

                        return false;
                    }
                })
                .into(holder.user_image);


        if(data.get(position).getImage().equals("/uploads/site/default_user.jpg")){
            holder.tv_coins.setTextColor(context.getResources().getColor(R.color.black));
            holder.name_tv.setTextColor(context.getResources().getColor(R.color.black));
        }



        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(authResponse.getUser().getName().equals("guest")){
                    return;
                }
                context.startActivity(new Intent(context, TargetProfileActivity.class).putExtra("target_id",data.get(position).getId()+""));

            }
        });

        holder.video_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(authResponse.getUser().getName().equals("guest")){
                    return;
                }
                context.startActivity(new Intent(context , RequestVideoCall.class)
                        .putExtra("target_id",data.get(position).getId()+"")
                        .putExtra("target_name",data.get(position).getName()+"")
                        .putExtra("target_image",data.get(position).getImage()+"")
                );
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DashboardViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row)
        CardView row;
        @BindView(R.id.name_tv)
        TextView name_tv;
        @BindView(R.id.tv_coins)
        TextView tv_coins;
        @BindView(R.id.user_image)
        RoundedImageView user_image;
        @BindView(R.id.video_call)
        RelativeLayout video_call;
        @BindView(R.id.statue_dot)
        RelativeLayout statue_dot;

        public DashboardViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
