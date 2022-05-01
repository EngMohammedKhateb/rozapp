package rozapp.roz.app.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import rozapp.roz.app.R;
import rozapp.roz.app.helper.EnglishLanguage;
import rozapp.roz.app.helper.GradientTextView;

public class FirstWelcomActvity extends AppCompatActivity {

    GradientTextView textView;
    LinearLayout linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_RozApp);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_welcom_actvity);

        textView = (GradientTextView) findViewById(R.id.getstarted);
        linear = (LinearLayout) findViewById(R.id.linear);




        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstWelcomActvity.this,SecondWelcomeActivity.class));
                finish();
            }
        });


    }
}