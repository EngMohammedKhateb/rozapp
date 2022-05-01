package rozapp.roz.app.home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.pwittchen.swipe.library.rx2.Swipe;
import com.github.pwittchen.swipe.library.rx2.SwipeListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.Media;

public class ImageViewrActivity extends AppCompatActivity  {

    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    @BindView(R.id.arrow_back)
    ImageView arrow_back;

    @BindView(R.id.detector)
    RelativeLayout root;
    private Swipe swipe;

    List<Media> images;

    int current_index=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_viewr);
        ButterKnife.bind(this);

        String imageUrl = getIntent().getStringExtra("image");

        if(getIntent().hasExtra("album")){
            images= (List<Media>) getIntent().getSerializableExtra("album");
            for(int i=0;i<images.size();i++){
                if(imageUrl.equals(images.get(i).getSrc())){
                    current_index=i;
                    break;
                }
            }
        }


        SharedPreferences pref=getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String language=pref.getString("language","en");
        if(language.equals("en")) {
            arrow_back.setImageDrawable(getDrawable(R.drawable.arrow_left));
        }else{
            arrow_back.setImageDrawable(getDrawable(R.drawable.arrow_right));
        }
        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setInitialScale(100);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.loadUrl(imageUrl);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

                if (webView != null) {

                    String htmlData = "<html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"></head><body><div align=\"center\" >" + "your internet connection is low :" + "\n Try watch this Image Later " + "</div></body>";

                    webView.loadUrl("about:blank");
                    webView.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null);
                    webView.invalidate();

                }

            }
        });
        webView.setDrawingCacheEnabled(true);


        webView.setWebViewClient(new myWebClientImage());

        if(getIntent().hasExtra("album")){
            swipe = new Swipe();
            swipe.setListener(new SwipeListener() {

                @Override
                public void onSwipingLeft(MotionEvent event) {

                }

                @Override
                public boolean onSwipedLeft(MotionEvent event) {
                    if(current_index==0){
                        return false;
                    }
                    else{
                        current_index=current_index-1;
                        webView.stopLoading();
                        String imageUrl = images.get(current_index).getSrc() ;
                        webView.loadUrl(imageUrl);
                    }


                    return false;
                }

                @Override
                public void onSwipingRight(MotionEvent event) {

                }

                @Override
                public boolean onSwipedRight(MotionEvent event) {
                    if(current_index==images.size()-1){
                        return false;
                    }
                    else{
                        current_index=current_index+1;
                        webView.stopLoading();
                        String imageUrl = images.get(current_index).getSrc() ;
                        webView.loadUrl(imageUrl);
                    }
                    return false;
                }

                @Override
                public void onSwipingUp(MotionEvent event) {

                }

                @Override
                public boolean onSwipedUp(MotionEvent event) {

                    return false;
                }

                @Override
                public void onSwipingDown(MotionEvent event) {

                }

                @Override
                public boolean onSwipedDown(MotionEvent event) {

                    return false;
                }
            });
        }
    }


    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        if(getIntent().hasExtra("album")) {
            swipe.dispatchTouchEvent(event);
        }
        return super.dispatchTouchEvent(event);
    }
    public class myWebClientImage extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            super.onPageStarted(view, url, favicon);
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub


            return true;


        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

            progressbar.setVisibility(View.GONE);
        }
    }





}