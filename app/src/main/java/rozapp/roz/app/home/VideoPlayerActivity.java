package rozapp.roz.app.home;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.MyMediaController;

public class VideoPlayerActivity extends AppCompatActivity {

    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.arrow_back)
    ImageView arrow_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            setTheme(R.style.DarkTheme_RozApp);
        }else{
            setTheme(R.style.Theme_RozApp);
        }


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_player);
        ButterKnife.bind(this);
        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Uri uri = Uri.parse(getIntent().getStringExtra("video_url"));
        MyMediaController mediaController = new MyMediaController(this);

        mediaController.setAnchorView(this.videoView);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.start();


    }
}