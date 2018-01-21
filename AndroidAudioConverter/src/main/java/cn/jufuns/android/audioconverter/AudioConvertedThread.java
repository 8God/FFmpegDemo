package cn.jufuns.android.audioconverter;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * | version | date        | author         | description
 * 0.0.1     2018/1/21     cth              init
 * <p>
 * desc:
 *
 * @author cth
 */

public class AudioConvertedThread extends Thread {

    private static final String TAG = "AudioConvertedThread";

    private WeakReference<Context> mWeakReference;

    private LinkedBlockingQueue<ConvertedTask> convertedTaskList;

    public AudioConvertedThread() {
        convertedTaskList = new LinkedBlockingQueue<>();
    }

    public void initAudioConverter(Context context) {
//        File externalStorageDir = Environment.getExternalStorageDirectory();
//        if (externalStorageDir.exists()) {
//            audioFilePath = externalStorageDir.getAbsolutePath() + "/AudioFile";
//        }
//
//        Log.i(TAG, "[AudioConvertedThread] - initAudioConverter : audioFilePath = " + audioFilePath);

        mWeakReference = new WeakReference<Context>(context);

        AndroidAudioConverter.load(context, new ILoadCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "[AudioConvertedThread] - initAudioConverter : load successfully");
            }

            @Override
            public void onFailure(Exception error) {
                Log.d(TAG, "[AudioConvertedThread] - initAudioConverter : load failed, error msg : " + error.getMessage());
            }
        });
    }

    @Override
    public void run() {
        super.run();

        while (true) {

            if (AndroidAudioConverter.isLoaded()) {
                try {
                    Log.i(TAG,"从队列取任务");
                    ConvertedTask convertedTask = convertedTaskList.take();
                    Log.i(TAG,"从队列取到了任务！！");
                    convertAudioFileToMP3(convertedTask, new IConvertCallback() {
                        @Override
                        public void onSuccess(File convertedFile) {
                            Log.i(TAG, "[AudioConvertedThread] - convertAudioFileToMP3 : converted success, converted file path is " + convertedFile.getAbsolutePath());
                        }

                        @Override
                        public void onFailure(Exception error) {

                        }
                    });
                    Log.i(TAG, "after convert : convertedTaskList size = " + convertedTaskList.size());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



            }
        }
    }

    public synchronized void addConvertedTask(String[] audioFileArray) {
        if (audioFileArray != null && audioFileArray.length > 0) {
            for (int i = 0; i < audioFileArray.length; i++) {
                ConvertedTask convertedTask = new ConvertedTask();
                convertedTask.audioFilePath = audioFileArray[i];

                try {
                    convertedTaskList.put(convertedTask);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void convertAudioFileToMP3(ConvertedTask convertedTask, IConvertCallback callback) {
        if (convertedTask != null && AndroidAudioConverter.isLoaded()) {

            Log.i("cth","convertAudioFileToMP3");
            File audioFile = new File(convertedTask.audioFilePath);

            AndroidAudioConverter.with(mWeakReference.get())
                    // Your current audio file
                    .setFile(audioFile)
                    // Your desired audio format
                    .setFormat(AudioFormat.MP3)
                    // An callback to know when conversion is finished
                    .setCallback(callback)
                    // Start conversion
                    .convert();
        }
    }

    class ConvertedTask {
        public String audioFilePath;
        public String convertedFilePath;
    }
}
