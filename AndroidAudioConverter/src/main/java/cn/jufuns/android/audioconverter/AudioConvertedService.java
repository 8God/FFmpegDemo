package cn.jufuns.android.audioconverter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.service.autofill.AutofillService;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * | version | date        | author         | description
 * 0.0.1     2018/1/21     cth              init
 * <p>
 * desc:
 *
 * @author cth
 */

public class AudioConvertedService extends Service {

    private static final String TAG = "AudioConvertedService";

    public static final String KEY_AUDIO_FILE_ARRAY = "KEY_AUDIO_FILE_ARRAY";

    public static final String ACTION_START_AUDIO_CONVERTED_THREAD = "ACTION_START_AUDIO_CONVERTED_THREAD";
    public static final String ACTION_STOP_AUDIO_CONVERTED_THREAD = "ACTION_STOP_AUDIO_CONVERTED_THREAD";

    private AudioConvertedThread mAudioConvertedThread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        Log.i(TAG, "[AudioConvertedService] - onStartCommand : action = " + action);

        if (ACTION_START_AUDIO_CONVERTED_THREAD.equals(action)) {

            if (mAudioConvertedThread == null) {
                mAudioConvertedThread = new AudioConvertedThread();
                mAudioConvertedThread.initAudioConverter(AudioConvertedService.this);

                mAudioConvertedThread.start();
            }

            String[] audioFileArray = intent.getStringArrayExtra(KEY_AUDIO_FILE_ARRAY);
            mAudioConvertedThread.addConvertedTask(audioFileArray);


        } else if (ACTION_STOP_AUDIO_CONVERTED_THREAD.equals(action)) {
            if (mAudioConvertedThread != null) {
                mAudioConvertedThread.interrupt();
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mAudioConvertedThread = null;
    }
}
