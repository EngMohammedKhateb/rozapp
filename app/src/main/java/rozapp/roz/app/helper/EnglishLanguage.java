package rozapp.roz.app.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class EnglishLanguage extends TextView {

    public EnglishLanguage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"Lexend-Regular.ttf"));
    }

}
