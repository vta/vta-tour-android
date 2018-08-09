package com.vta.virtualtour.ui.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vta.virtualtour.R;

/**
 * Created by tushar on 16/04/18.
 */

public class VideoControlView extends LinearLayout implements View.OnClickListener {

    private VideoControlClickListener videoControlClickListener;
    private TextView textViewRestart;
    private TextView textViewPlayOrPause;
    private TextView textViewViewPOIs;
    private TextView textViewNavigation;

    public VideoControlView(Context context) {
        super(context);
        inflateChildViews(context);
    }

    public VideoControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateChildViews(context);
    }

    public VideoControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateChildViews(context);
    }

    public void setVideoControlClickListener(VideoControlClickListener videoControlClickListener) {
        this.videoControlClickListener = videoControlClickListener;
    }

    public void enablePOIButton() {
        textViewViewPOIs.setEnabled(true);
    }

    public void disablePOIButton() {
        textViewViewPOIs.setEnabled(false);
    }

    public void changePlayOrPauseIcon(boolean play) {
        if(play) {
            textViewPlayOrPause.setText(R.string.play);
            textViewPlayOrPause.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_play, 0, 0);
        } else {
            textViewPlayOrPause.setText(R.string.pause);
            textViewPlayOrPause.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_pause, 0, 0);
        }
    }

    public void showPauseButton(){
        textViewPlayOrPause.setVisibility(View.VISIBLE);
    }

    public void hidePauseButton(){
        textViewPlayOrPause.setVisibility(View.GONE);
    }

    private void inflateChildViews(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.video_control_view, this);
        textViewRestart = view.findViewById(R.id.video_control_button_restart);
        textViewRestart.setOnClickListener(this);
        textViewPlayOrPause = view.findViewById(R.id.video_control_button_play_or_pause);
        textViewPlayOrPause.setOnClickListener(this);
        textViewViewPOIs = view.findViewById(R.id.video_control_button_view_pois);
        textViewViewPOIs.setOnClickListener(this);
        textViewNavigation = view.findViewById(R.id.video_control_button_navigation);
        textViewNavigation.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.video_control_button_restart:
                if(videoControlClickListener != null) {
                    videoControlClickListener.onRestartClicked();
                }
                break;

            case R.id.video_control_button_play_or_pause:
                if(videoControlClickListener != null) {
                    videoControlClickListener.onPlayOrPauseClicked();
                }
                break;

            case R.id.video_control_button_view_pois:
                if(videoControlClickListener != null) {
                    videoControlClickListener.onViewPOIsClicked();
                }
                break;

            case R.id.video_control_button_navigation:
                if(videoControlClickListener != null) {
                    videoControlClickListener.onNavigationClicked();
                }
                break;
        }
    }

    public interface VideoControlClickListener {
        void onRestartClicked();
        void onPlayOrPauseClicked();
        void onViewPOIsClicked();
        void onNavigationClicked();
    }
}

