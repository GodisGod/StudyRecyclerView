package study.com.purerecyclerview;

import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class TestActivity extends AppCompatActivity {

    private FrameLayout framTop;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        framTop = findViewById(R.id.fram_top);
        initListener();
    }

    private void initListener() {
        findViewById(R.id.go_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewCompat.setTranslationY(framTop, (i = i + 2));
            }
        });
        findViewById(R.id.go_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewCompat.setTranslationY(framTop, (i = i - 2));
            }
        });
    }

}
