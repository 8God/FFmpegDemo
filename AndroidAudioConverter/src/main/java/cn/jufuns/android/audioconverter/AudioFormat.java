package cn.jufuns.android.audioconverter;

/**
 * | version | date        | author         | description
 * 0.0.1     2018/1/21       cth          init
 * <p>
 * desc:
 *
 * @author cth
 */

public enum AudioFormat {
    AAC,
    MP3,
    M4A,
    WMA,
    WAV,
    FLAC;

    public String getFormat() {
        return name().toLowerCase();
    }
}
