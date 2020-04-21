package info.itloser.audio.test;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import info.itloser.audio.R;


public class AudioRecordActivity extends AppCompatActivity implements View.OnClickListener {

    AudioRecord audioRecord;

    Button btnRecord, btnStopRecord, btnPlayRecord;

    TextView tvFilePath;

    FileOutputStream fileOutputStream;

    int mBufferSize;

    //文件保存的路径
    static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/";

    String fileName;

    AudioTrack audioTrack;
    FileInputStream fileInputStream;
    DataInputStream dataInputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);

        btnRecord = findViewById(R.id.btn_record);
        btnStopRecord = findViewById(R.id.btn_stop_record);
        btnPlayRecord = findViewById(R.id.btn_play_record);

        btnRecord.setOnClickListener(this);
        btnStopRecord.setOnClickListener(this);
        btnPlayRecord.setOnClickListener(this);

        tvFilePath = findViewById(R.id.tv_file_path);

        mBufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize);

        audioRecord = new AudioRecord.Builder()
                .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                .setBufferSizeInBytes(mBufferSize)
                .setAudioFormat(new AudioFormat.Builder()
                        .setSampleRate(44100)
                        .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .build())
                .build();

        audioTrack = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(44100)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build())
                .setTransferMode(AudioTrack.MODE_STREAM)
                .setSessionId(0)
                .build();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record:
                record();
                break;
            case R.id.btn_stop_record:
                stop();
                break;
            case R.id.btn_play_record:
                playRecord();
                break;
        }
    }

    /**
     * AudioTrack录制pcm
     */
    public void record() {

        if (audioRecord == null) {
            //为空
            return;
        }
        Log.i("ddd", "不为空" + audioRecord.getState());
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            //尚未初始化
            return;
        }
        Log.i("ddd", "已初始化");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("ddd", "thread run");
                try {
                    //文件名
                    fileName = System.currentTimeMillis() + ".pcm";
                    String filePath = PATH + fileName;
                    //初始化outPutStream
                    fileOutputStream = new FileOutputStream(filePath);
                    Log.i("ddd", "保存的文件名" + fileName);
                    byte[] audioDataArray = new byte[mBufferSize];//一次写入的最小单位数
                    audioRecord.startRecording();

                    while ((audioRecord.read(audioDataArray, 0, mBufferSize)) > 0) {
                        Log.i("ddd", "录了一波");
                        fileOutputStream.write(audioDataArray);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != fileOutputStream) {
                            fileOutputStream.close();
                            fileOutputStream = null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("ddd", "thread end");
            }
        }).start();

    }

    /**
     * 停止播放
     */
    @SuppressLint("SetTextI18n")
    public void stop() {
        tvFilePath.setText(PATH + fileName);
        if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
            audioRecord.stop();
        }

    }


    /**
     * 播放刚才录制的pcm
     */
    public void playRecord() {
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
                    File file = new File(tvFilePath.getText().toString());
                    fileInputStream = new FileInputStream(file);
                    dataInputStream = new DataInputStream(new BufferedInputStream(fileInputStream));
                    byte[] bytes = new byte[mBufferSize];
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


}
