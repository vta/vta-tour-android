package com.vta.virtualtour.ui.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by Tushar on 23/05/18.
 */

public class CustomVideoView extends VideoView {

    private VideoControlListener mListener;

    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPlayPauseListener(VideoControlListener listener) {
        mListener = listener;
    }

    @Override
    public void pause() {
        super.pause();
        if (mListener != null) {
            mListener.onPaused();
        }
    }

    @Override
    public void start() {
        super.start();
        if (mListener != null) {
            mListener.onPlaying();
        }
    }

    public interface VideoControlListener {
        void onPlaying();
        void onPaused();
    }

}

