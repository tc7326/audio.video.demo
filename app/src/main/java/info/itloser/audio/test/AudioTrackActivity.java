package info.itloser.audio.test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import info.itloser.audio.R;


public class AudioTrackActivity extends AppCompatActivity implements View.OnClickListener {

    AudioTrack audioTrack, staticAudioTrack;

    Button btnPath, btnPlay, btnStop;
    Button btnMode0, btnMode1;
    Button btnStaticPlay;

    TextView tvPath;

    FileInputStream fileInputStream;
    DataInputStream dataInputStream;

    int minBufferSize;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_track);

        btnPath = findViewById(R.id.btn_path);
        btnPlay = findViewById(R.id.btn_play);
        btnStop = findViewById(R.id.btn_stop);

        btnMode0 = findViewById(R.id.btn_mode_0);
        btnMode1 = findViewById(R.id.btn_mode_1);

        btnStaticPlay = findViewById(R.id.btn_static_play);


        tvPath = findViewById(R.id.tv_path);

        btnPath.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        btnMode0.setOnClickListener(this);
        btnMode1.setOnClickListener(this);
        btnStaticPlay.setOnClickListener(this);


        tvPath.setText(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/beijing.pcm");

    }

    public void initAudioTrack(int i) {

        //这里 setAudioFormat 已近传入相关参数了 builder构造会自动计算，无需调用此方法计算， 而 new 构造 需要填充此方法的值。
        minBufferSize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

        //若 已经 准备就绪， 还重复构造的话，之前的构造将无法释放。退出activity都不能释放。如果配置不发生变化的话，尽量不要重新构造。

        switch (i) {
            case 0:
                //第一种构造 builder
                audioTrack = new AudioTrack.Builder()
                        .setAudioAttributes(new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build())
                        .setAudioFormat(new AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(8000)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build())
                        .setTransferMode(AudioTrack.MODE_STREAM)
                        .setSessionId(0)
                        .build();
                break;
            case 1:
                //第二种构造 new
                audioTrack = new AudioTrack(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                                .build(),
                        new AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(8000)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build(),
                        minBufferSize,
                        AudioTrack.MODE_STREAM, 0);

                break;
            default:
                break;
        }

    }

    /**
     * 流式播放
     */
    public void play() {
        if (audioTrack == null) {
            //为空
            return;
        }

        if (audioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
            //尚未初始化
            return;
        }

        if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            //正在播放
            return;
        }

        //播放需要在线程中执行。
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("ddd", "thread run");
                try {

                    File file = new File(tvPath.getText().toString());
                    fileInputStream = new FileInputStream(file);
                    dataInputStream = new DataInputStream(new BufferedInputStream(fileInputStream));
                    byte[] bytes = new byte[minBufferSize];
                    int len;
                    audioTrack.play();
                    while ((len = dataInputStream.read(bytes)) != -1) {
                        //每次读取minBufferSize的长度
                        audioTrack.write(bytes, 0, len);
                    }
                    audioTrack.stop();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("ddd", "thread end");
            }
        }).start();


    }

    public void stop() {
        if (audioTrack == null) {
            //为空
            return;
        }

        if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
            //没播放
            return;
        }

        audioTrack.stop();

    }

    /**
     * 静态播放
     */
    public void staticPlay() {

        //加载资源
        byte[] staticData = new byte[0];
        InputStream in = getResources().openRawResource(R.raw.msg);

        try {
            int sizeOfInputStram  = in.available();
            staticData = new byte[sizeOfInputStram];
            Log.i("ddd", "文件大小：" + sizeOfInputStram);
            ByteArrayOutputStream out = new ByteArrayOutputStream(sizeOfInputStram);

//            for (int b; (b = in.read()) != -1; ) {
//                out.write(b);
//            }

            int rc;
            while ((rc = in.read(staticData, 0, sizeOfInputStram)) != -1) {
                out.write(staticData, 0, rc);
            }

            staticData = out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        staticAudioTrack = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(9220)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build())
                .setBufferSizeInBytes(staticData.length)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .setSessionId(0)
                .build();

        Log.i("ddd", "静态大小：" + staticData.length);

        //一次性写入AudioTrack
        staticAudioTrack.write(staticData, 0, staticData.length);
        //播放
        staticAudioTrack.play();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_path:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_play:
                play();
                break;
            case R.id.btn_stop:
                stop();
                break;
            case R.id.btn_mode_0:
                initAudioTrack(0);
                break;
            case R.id.btn_mode_1:
                initAudioTrack(1);
                break;
            case R.id.btn_static_play:
                staticPlay();
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                assert data != null;
                Uri uri = data.getData();
                assert uri != null;
                tvPath.setText(uri.getPath());
            }
        }

    }

    @Override
    protected void onDestroy() {
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();//释放资源
        }

        if (staticAudioTrack != null) {
            staticAudioTrack.stop();
            staticAudioTrack.release();//释放资源
        }
        super.onDestroy();
    }
}
