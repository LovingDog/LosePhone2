package com.example.wanghanp.losephone.playService;

import java.io.IOException;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

public class SlideSettings {

    private static final String TAG = "SlideSettings";
    private static final long MUSIC_WEAKEN_TIME = 1000;
    private static final long MUSIC_ENHANCEMENT_TIME =1000;
    public static final String KEY_DISPLAY_FORM_MAGAZINE = "Display_Form_Magazine";
    public static final String KEY_DISPLAY_FORM = "DisplayForm";
    public static final String KEY_BACKGROUND_MUSIC = "Background_music";
    public static final String KEY_PALY_MODE = "PlayMode";
    public static final String KEY_PLAY_SPEED = "PlaySpeed";
    public static final String KEY_SPEED_SCALE = "speedScale";
    public static final String KEY_FADE_OUT = "Fade_out";
    public static final String PLAY_MUSIC_NAME_MUSIC1 = "fly.mp3";
    public static final String PLAY_MUSIC_NAME_MUSIC2 = "Weekend.mp3";
    public static final String PLAY_MUSIC_NAME_MUSIC3 = "Epic.mp3";
    public static final String PLAY_MUSIC_NAME_MUSIC4 = "Gentle.mp3";
    public static final String KEY_MUSIC_TYPE_MUSIC0 = "null_misic";
    public static final String KEY_MUSIC_TYPE_MUSIC1 = "misic1";
    public static final String KEY_MUSIC_TYPE_MUSIC2 = "misic2";
    public static final String KEY_MUSIC_TYPE_MUSIC3 = "misic3";
    public static final String KEY_MUSIC_TYPE_MUSIC4 = "misic4";
    public static final String BACKGROUND_MUSIC_URI = "background_music_uri";

    public static final int DEFAULT_SPEED = 5;
    /* Modify By Zhangjie,20171108,PRODUCTION-5889,begin */
    public static final float HALF_SPEED_SCALE = 0.5f;
    /* Modify By Zhangjie,20171108,PRODUCTION-5889,end */
    public static final int KEY_MUSIC_NUll = 0;
    public static final int KEY_MUSIC_MUSIC1 = 1;
    public static final int KEY_MUSIC_MUSIC2 = 2;
    public static final int KEY_MUSIC_MUSIC3 = 3;
    public static final int KEY_MUSIC_MUSIC4 = 4;

    private SharedPreferences mPreference;
    private MediaPlayer mediaPlayer;
    private Context mContext;
    /*modify for bug 8760 liuxiaoshuan 20171129 start*/
    private boolean isPlayEnhancementMusic;
    private boolean mPlayingWeakenMusic;
    /*modify for bug 8760 liuxiaoshuan 20171129 end*/

    private static SlideSettings slideSettings = null;

    public static synchronized SlideSettings getInstance(Context context) {
        if (slideSettings == null) {
            slideSettings = new SlideSettings(context);
        }
        return slideSettings;
    }

    private SlideSettings(Context context) {
        mContext = context.getApplicationContext();
        mPreference = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public void setLoopPlayback(boolean isLoop) {
        if (mPreference == null)
            return;
        Editor editor = mPreference.edit();
        if (editor == null)
            return;
        editor.remove(KEY_PALY_MODE);
        editor.putBoolean(KEY_PALY_MODE, isLoop);
        editor.commit();
    }


    public void setPlaySpeed(int speed) {
        if (mPreference == null)
            return;
        Editor editor = mPreference.edit();
        if (editor == null)
            return;
        editor.remove(KEY_PLAY_SPEED);
        editor.putInt(KEY_PLAY_SPEED, speed);
        editor.commit();
    }

    public void setSpeedScale(float speedScale) {
        if (mPreference == null)
            return;
        Editor editor = mPreference.edit();
        if (editor == null)
            return;
        editor.remove(KEY_SPEED_SCALE);
        editor.putFloat(KEY_SPEED_SCALE, speedScale);
        editor.commit();
    }

    /* add by zhangjie,20170927,GMOS-8844,end */

    public void playWeakenMusic(Activity activity) {
        /*modify for bug 8760 liuxiaoshuan 20171129 start*/
        isPlayEnhancementMusic = false;
        mPlayingWeakenMusic = true;
        /*modify for bug 8760 liuxiaoshuan 20171129 end*/
        if (activity != null) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        if (mediaPlayer == null) {
            return;
        }

        long interval = MUSIC_WEAKEN_TIME / 10;
        new CountDownTimer(MUSIC_WEAKEN_TIME, interval) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (mediaPlayer == null) {
                    return;
                }
                float volume = millisUntilFinished * 1.0f / MUSIC_WEAKEN_TIME;
                if (volume < 0) {
                    volume = 0;
                }
                if (volume > 1) {
                    volume = 1;
                }
                mediaPlayer.setVolume(volume, volume);
            }

            @Override
            public void onFinish() {
                /*modify for bug 8760 liuxiaoshuan 20171129 start*/
                mPlayingWeakenMusic = false;
//                if(isPlayEnhancementMusic){
//                    return;
//                }
                /*modify for bug 8760 liuxiaoshuan 20171129 end*/
                if (mediaPlayer == null) {
                    return;
                }
                mediaPlayer.setVolume(0f, 0f);
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                muteAudioFocus(false);
            }
        }.start();

    }

    public void pauseMusic() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public boolean isPlayerPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    public void startMusic() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.start();
    }

    public void playEnhancementMusic(Activity activity) {
        /*modify for bug 8760 liuxiaoshuan 20171129 start*/
        mediaPlayer = new MediaPlayer();
        setMediaResource(PLAY_MUSIC_NAME_MUSIC1);
//        isPlayEnhancementMusic = true;
//        /*modify for bug 8760 liuxiaoshuan 20171129 end*/
//        destroyMediaPaly();
//        if (activity != null) {
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        }
        long interval = MUSIC_ENHANCEMENT_TIME / 10;
//
//        int key = getBgMusicType();
//
//        switch (key) {
//        case KEY_MUSIC_NUll:
//            return;
//        case KEY_MUSIC_MUSIC1:
//            setMediaResource(PLAY_MUSIC_NAME_MUSIC1);
//            break;
//        case KEY_MUSIC_MUSIC2:
//            setMediaResource(PLAY_MUSIC_NAME_MUSIC1);
//            break;
//        case KEY_MUSIC_MUSIC3:
//            setMediaResource(PLAY_MUSIC_NAME_MUSIC1);
//            break;
//        case KEY_MUSIC_MUSIC4:
//            setMediaResource(PLAY_MUSIC_NAME_MUSIC1);
//            break;
//        default:
//            break;
//        }

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0.3f, 0.8f);
        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (muteAudioFocus(true)) {
            mediaPlayer.start();
        }

        new CountDownTimer(MUSIC_ENHANCEMENT_TIME, interval) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (mediaPlayer == null) {
                    return;
                }
                float volume = 1f - millisUntilFinished * 1.0f / MUSIC_ENHANCEMENT_TIME;
                if (volume < 0) {
                    volume = 0;
                }
                if (volume > 1) {
                    volume = 1;
                }
                mediaPlayer.setVolume(volume, volume);
            }

            @Override
            public void onFinish() {
                /*modify for bug 8760 liuxiaoshuan 20171129 start*/
                isPlayEnhancementMusic = false;
                /*modify for bug 8760 liuxiaoshuan 20171129 end*/
                if (mediaPlayer == null) {
                    return;
                }
                mediaPlayer.setVolume(1f, 1f);
            }
        }.start();

    }

    private void setMediaResource(String musicName) {
        AssetFileDescriptor fileDescriptor;
        try {
            fileDescriptor = mContext.getAssets().openFd(musicName);
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            fileDescriptor.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void setBgMusicType(int musicType) {
        if (mPreference == null)
            return;

        Editor editor = mPreference.edit();
        if (editor == null)
            return;
        editor.remove(KEY_BACKGROUND_MUSIC);
        editor.putInt(KEY_BACKGROUND_MUSIC, musicType);
        editor.commit();

    }

    public int getBgMusicType() {
        if (mPreference == null) {
            return KEY_MUSIC_MUSIC1;
        }
        return mPreference.getInt(KEY_BACKGROUND_MUSIC, KEY_MUSIC_MUSIC1);
    }

    public void setSysMusicUri(String uri) {
        Editor editor = mPreference.edit();
        editor.remove(BACKGROUND_MUSIC_URI);
        editor.putString(BACKGROUND_MUSIC_URI, uri == null ? "" : uri.toString());
        editor.commit();
    }

    public String getSysMusicUri() {
        return mPreference.getString(BACKGROUND_MUSIC_URI, "");

    }

    public void destroyMediaPaly() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        muteAudioFocus(false);
    }

    public boolean muteAudioFocus(boolean bMute) {
        if (mContext == null) {
            Log.d(TAG, "context is null.");
            return false;
        }

        boolean bool = false;
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (bMute) {
            int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            int result = am.abandonAudioFocus(null);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        Log.d(TAG, "pauseMusic bMute=" + bMute + " result=" + bool);
        return bool;
    }
    /*modify for bug 8760 liuxiaoshuan 20171129 start*/
    public boolean getPlayingWeakenMusic(){
        return mPlayingWeakenMusic;
    }
    /*modify for bug 8760 liuxiaoshuan 20171129 end*/
}
