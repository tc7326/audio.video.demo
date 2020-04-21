package info.itloser.audio.test;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import info.itloser.audio.R;


public class SoundPoolActivity extends AppCompatActivity implements SoundPool.OnLoadCompleteListener, View.OnClickListener {

    Button btnSingle,btnMulti;

    SoundPool soundPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_pool);

        btnSingle = findViewById(R.id.btn_single);
        btnMulti = findViewById(R.id.btn_multi);

        btnSingle.setOnClickListener(this);
        btnMulti .setOnClickListener(this);



        //初始化soundpool
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()//用于封装描述音频流信息的属性集合的类
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)//音频的用途，比如音乐，导航，通知，游戏等。
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)//音频的内容，比如是聊天语音，还是音乐，还是按键音效，等。
                        .build())
                .setMaxStreams(4)//设置可以同时播放的最大同时流数
                .build();

        //设置加载完成的监听
        soundPool.setOnLoadCompleteListener(this);

    }

    public void load(){
        soundPool.load(this,R.raw.msg,0);
    }

    public void stop(){
        soundPool.stop(nowPlayId);
    }

    @Override
    protected void onDestroy() {
        stop();
        super.onDestroy();
    }

    /**
     * @param soundPool 来自load（）方法的SoundPool对象
     * @param sampleId  加载的声音的样本ID。
     * @param status    加载操作的状态（0 =成功）
     */
    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        //播放后会返回一个资源id，可以根据资源id，释放或者停止
        nowPlayId = soundPool.play(sampleId, 0.5f, 1f, 16, 0, 1.0f);
    }

    int nowPlayId;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_single:
                load();
                break;
            case R.id.btn_multi:
                break;
        }
    }
}
