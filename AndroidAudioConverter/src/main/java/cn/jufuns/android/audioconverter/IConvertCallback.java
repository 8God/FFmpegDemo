package cn.jufuns.android.audioconverter;

import java.io.File;

/**
 * | version | date        | author         | description
 * 0.0.1     2018/1/21       cth          init
 * <p>
 * desc:
 *
 * @author cth
 */

public interface IConvertCallback {

    void onSuccess(File convertedFile);

    void onFailure(Exception error);
}
