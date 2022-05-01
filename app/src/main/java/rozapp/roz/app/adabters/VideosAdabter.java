package rozapp.roz.app.adabters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.home.VideoPlayerActivity;
import rozapp.roz.app.models.Media;

public class VideosAdabter extends RecyclerView.Adapter<VideosAdabter.VideoViewHolder> {

    private Context context;
    private List<Media> videos;

    public VideosAdabter(Context context,List<Media> videos){
        this.context=context;
        this.videos=videos;
    }

    @NonNull
    @Override
    public VideosAdabter.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_video,parent ,false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideosAdabter.VideoViewHolder holder, @SuppressLint("RecyclerView") int position) {

        RequestOptions requestOptions = new RequestOptions();
        Glide.with(context)
                .load(Constants.Image_URL+videos.get(position).getSrc())
                .apply(requestOptions)
                .thumbnail(Glide.with(context).load(Constants.Image_URL+videos.get(position).getSrc()))
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, VideoPlayerActivity.class).putExtra("video_url",Constants.Image_URL+videos.get(position).getSrc()+""));
            }
        });
//        holder.video_player.setVideoPath(Constants.Image_URL+videos.get(position).getSrc());
//        if(videos.get(position).isIsblaying()){
//            holder.play_btn.setVisibility(View.GONE);
//        }else{
//            holder.play_btn.setVisibility(View.VISIBLE);
//        }
//        holder.video_player.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                holder.video_player.stopPlayback();
//                holder.play_btn.setVisibility(View.VISIBLE);
//                videos.get(position).setIsblaying(false);
//            }
//        });
//        holder.play_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                holder.play_btn.setVisibility(View.GONE);
//                holder.video_player.setVideoPath(Constants.Image_URL+videos.get(position).getSrc());
//                holder.video_player.start();
//                videos.get(position).setIsblaying(true);
//            }
//        });
    }



    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.video_player)
//        VideoView video_player;
        @BindView(R.id.play_btn)
        ImageView play_btn;
        @BindView(R.id.imageView)
        ImageView imageView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
