package info.itloser.audio;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import info.itloser.audio.test.AudioRecordActivity;
import info.itloser.audio.test.AudioTrackActivity;
import info.itloser.audio.test.SoundPoolActivity;

public class MainListActivity extends AppCompatActivity {

    List<BindBean> beans;
    LinearLayout llMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        llMain = findViewById(R.id.ll_main);

        beans = new ArrayList<>();
        beans.add(new BindBean("AudioTrack", AudioTrackActivity.class));
        beans.add(new BindBean("AudioRecord", AudioRecordActivity.class));
        beans.add(new BindBean("SoundPool", SoundPoolActivity.class));


    }

    class BindBean {
        String text;
        Class<?> cls;

        @SuppressLint("SetTextI18n")
        BindBean(String text, final Class<?> cls) {
            this.text = text;//显示的字
            this.cls = cls;//进入的Act

            TextView tvNow = new TextView(MainListActivity.this);
            tvNow.setText(text);
            tvNow.setGravity(Gravity.CENTER);
            tvNow.setPadding(5, 20, 5, 20);
            tvNow.setTextColor(0xFFFFFFFF);
            tvNow.setBackgroundColor(Color.parseColor(getRandColor()));
            tvNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainListActivity.this, cls));
                }
            });

            llMain.addView(tvNow);

        }
    }

    //随机颜色
    public static String getRandColor() {
        String R, G, B;
        Random random = new Random();
        R = Integer.toHexString(random.nextInt(256)).toUpperCase();
        G = Integer.toHexString(random.nextInt(256)).toUpperCase();
        B = Integer.toHexString(random.nextInt(256)).toUpperCase();

        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;

        return "#" + R + G + B;
    }
}
