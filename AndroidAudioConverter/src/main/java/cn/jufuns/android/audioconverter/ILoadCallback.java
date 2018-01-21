package cn.jufuns.android.audioconverter;

/**
 * | version | date        | author         | description
 * 0.0.1     2018/1/21       cth          init
 * <p>
 * desc:
 *
 * @author cth
 */

public interface ILoadCallback {
    void onSuccess();

    void onFailure(Exception error);
}
