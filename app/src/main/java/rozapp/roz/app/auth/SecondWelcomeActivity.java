package rozapp.roz.app.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;

public class SecondWelcomeActivity extends AppCompatActivity {

    @BindView(R.id.btn)
    RelativeLayout btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_RozApp);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_welcome);
        ButterKnife.bind(this);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecondWelcomeActivity.this,SingleAuthActivity.class));
                finish();
            }
        });


    }
}