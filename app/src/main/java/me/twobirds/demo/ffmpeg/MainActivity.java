package me.twobirds.demo.ffmpeg;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;

import cn.jufuns.android.audioconverter.AudioConvertedService;

public class MainActivity extends AppCompatActivity {

    private TextView tv_file_path;
    private Button btn_transform;
    private Button btn_transform_all;

    private String selectedFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        requestPermissions();
//        initFFmpeg();
        initData();
        initUI();
    }

    private void initFFmpeg() {
        FFmpeg ffmpeg = FFmpeg.getInstance(this);

        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.i("cth", "开始加载");
                }

                @Override
                public void onFailure() {
                    Log.i("cth", "加载失败");
                }

                @Override
                public void onSuccess() {
                    Log.i("cth", "加载成功");
                }

                @Override
                public void onFinish() {
                    Log.i("cth", "加载工作结束");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }
    }

    private void initUI() {
        tv_file_path = (TextView) findViewById(R.id.tv_file_path);
        btn_transform = (Button) findViewById(R.id.btn_transform);
        btn_transform_all = (Button) findViewById(R.id.btn_transform_all);

        tv_file_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 1);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_transform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String convertedFilePath = selectedFilePath.substring(0, selectedFilePath.lastIndexOf('.')) + "_" + System.currentTimeMillis() + ".mp3";
                FFmpeg ffmpeg = FFmpeg.getInstance(MainActivity.this);
                try {
                    // to execute "ffmpeg -version" command you just need to pass "-version"
                    ffmpeg.execute(new String[]{"-y", "-i", selectedFilePath, convertedFilePath}, new ExecuteBinaryResponseHandler() {

                        @Override
                        public void onStart() {
                            Log.i("cth", "onStart");
                        }

                        @Override
                        public void onProgress(String message) {
                            Log.i("cth", "onProgress : message = " + message);
                        }

                        @Override
                        public void onFailure(String message) {
                            Log.i("cth", "onFailure : message = " + message);
                        }

                        @Override
                        public void onSuccess(String message) {
                            Log.i("cth", "onSuccess : message = " + message);
                        }

                        @Override
                        public void onFinish() {
                            Log.i("cth", "onSuccess");
                        }
                    });
                } catch (FFmpegCommandAlreadyRunningException e) {
                    // Handle if FFmpeg is already running
                }
            }
        });

        btn_transform_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] audioFilePathArray = new String[]{"/storage/emulated/0/Download/jqm.amr", "/storage/emulated/0/Download/qlx.amr"};

                Intent startConvertedService = new Intent(MainActivity.this, AudioConvertedService.class);
                startConvertedService.putExtra(AudioConvertedService.KEY_AUDIO_FILE_ARRAY, audioFilePathArray);
                startConvertedService.setAction(AudioConvertedService.ACTION_START_AUDIO_CONVERTED_THREAD);

                startService(startConvertedService);
            }
        });
    }

    private void initData() {

    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            Log.i("cth", "uri = " + uri);
            String filePath = UriToPathUtil.getRealFilePath(this, uri);
            Log.i("cth", "filePath is " + filePath);

            if (!TextUtils.isEmpty(filePath)) {
                tv_file_path.setText(filePath);
                selectedFilePath = filePath;
            }
        }
    }
}
