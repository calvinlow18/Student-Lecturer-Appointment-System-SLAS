package se.lowkhaiwynn.slas;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class RoleSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.dark));
        }

    }


    public void clickLect(View view) {
        Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        view.startAnimation(animFadein);
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("user", "lecturer");
        startActivity(i);
    }

    public void clickStu(View view) {
        Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        view.startAnimation(animFadein);
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("user", "student");
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
