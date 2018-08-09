package com.vta.virtualtour.ui.activities.videoMapScreen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.model.TravelMode;
import com.vta.virtualtour.R;
import com.vta.virtualtour.adapters.InfoWindowCustom;
import com.vta.virtualtour.interfaces.AsyncTaskJsonResultListener;
import com.vta.virtualtour.managers.RouteManager;
import com.vta.virtualtour.models.MarkerInfo;
import com.vta.virtualtour.models.Stop;
import com.vta.virtualtour.models.VideoGeoPoint;
import com.vta.virtualtour.ui.activities.BaseActivity;
import com.vta.virtualtour.ui.activities.navigationScreen.NavigationActivity;
import com.vta.virtualtour.ui.activities.placeOfInterestScreen.PlaceOfInterestActivity;
import com.vta.virtualtour.ui.customView.CustomVideoView;
import com.vta.virtualtour.ui.customView.VideoControlView;
import com.vta.virtualtour.utility.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.vta.virtualtour.utility.Constants.ROUTE_DETAILS_VIDEO_GEOPOINTS_BACK;
import static com.vta.virtualtour.utility.Constants.ROUTE_DETAILS_VIDEO_GEOPOINTS_FRONT;
import static com.vta.virtualtour.utility.Constants.ROUTE_DETAILS_VIDEO_GEOPOINTS_LEFT;
import static com.vta.virtualtour.utility.Constants.ROUTE_DETAILS_VIDEO_GEOPOINTS_LOW_RES_BACK;
import static com.vta.virtualtour.utility.Constants.ROUTE_DETAILS_VIDEO_GEOPOINTS_LOW_RES_FRONT;
import static com.vta.virtualtour.utility.Constants.ROUTE_DETAILS_VIDEO_GEOPOINTS_LOW_RES_LEFT;
import static com.vta.virtualtour.utility.Constants.ROUTE_DETAILS_VIDEO_GEOPOINTS_LOW_RES_RIGHT;
import static com.vta.virtualtour.utility.Constants.ROUTE_DETAILS_VIDEO_GEOPOINTS_RIGHT;

public class VideoMapActivity extends BaseActivity implements OnMapReadyCallback, VideoControlView.VideoControlClickListener, VideoMapContract.View, CustomVideoView.VideoControlListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, AsyncTaskJsonResultListener, Player.EventListener/*, AdapterView.OnItemSelectedListener*/ {
    private static final int GOOGLE_API_CLIENT_ID = 0;
    //    private final static int VIDEO_FRONT_VIEW = 0;
//    private final static int VIDEO_LEFT_VIEW = 1;
//    private final static int VIDEO_RIGHT_VIEW = 2;

    private static final int markerSize = 70;
    private MapFragment mapFragment;
    private VideoControlView videoControlView;
    private SimpleExoPlayer simpleExoPlayer;
    private SimpleExoPlayerView simpleExoPlayerView;
    private ProgressBar bufferingProgressBar;
    private VideoMapContract.Presenter presenter;
    private GoogleMap mMap;
    private Marker carMarker = null;
    private Marker stopsMarker = null;
    private Marker meetupMarker = null;
    private Marker coordMarker = null;
    private GoogleApiClient googleApiClient;
    private int selectedViewPosition = -1;
    private long currentVideoSecond = 0;
    private String selectedVideoUrl = "";

    private TextView navigationDetailsText;
    private ImageView fullscreenVideoImageView;
    private ImageView fullscreenMapImageView;
    private RelativeLayout videoViewLayout;
    private RelativeLayout mapViewLayout;
    private boolean isFullscreen;
    private boolean isRestart;
    private LinearLayout progressBarLayout;
    private TextView progressBarTextView;
    private int videoPausedStateApiCalls = 0;
    private LatLng carCurrentLocation = null;
    private RelativeLayout exoPlayerView;

    private List<Marker> poiMarkers = new ArrayList<>();
//    private ProgressBar videoProgressBar;
    private SeekBar videoSeekBar;
    private List<VideoGeoPoint> videoGeoPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_video_map);

        Log.d("VideoMapActivity", "VideoMapActivity On Create 1");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
//        getSupportActionBar().setTitle(getIntent().getStringExtra("Direction") + "/" + getIntent().getStringExtra("RouteName"));
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_title_layout);
        ((TextView) findViewById(R.id.action_bar_title)).setText(
                getIntent().getStringExtra("Direction") + "/" + getIntent().getStringExtra("RouteName"));
        presenter = new VideoMapPreseneter(this);

        setupViews();
        loadDefaultValues();
        allSetup();
    }


    private void allSetup() {

        keepScreenOn(false);
        initVideoPlayer();
        initMap();
        setupVideoControls();
        presenter.fetchIntegrations();

        SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(new SensorEventListener() {
            int orientation = -1;

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[1] < 6.5 && event.values[1] > -6.5) {
                    if (orientation != 1) {
                        if (isFullscreen) {
                            Log.d("Sensor", "Landscape fullscreen");
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        } else {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }
                        Log.d("Sensor", "Landscape");
                    }
                    orientation = 1;
                } else {
                    if (orientation != 0) {
                        Log.d("Sensor", "Portrait");
                    }
                    orientation = 0;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

        }, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_video_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_item_left:
                if (Utils.isConnectedToWifi(this)) {
                    presenter.fetchRouteDetailsVideoGeoPoints(ROUTE_DETAILS_VIDEO_GEOPOINTS_LEFT);
                    selectedVideoUrl = RouteManager.getSharedInstance().getRouteDetails().getVideoLeftUrl();
                } else {
                    presenter.fetchRouteDetailsVideoGeoPoints(ROUTE_DETAILS_VIDEO_GEOPOINTS_LOW_RES_LEFT);
                    selectedVideoUrl = RouteManager.getSharedInstance().getRouteDetails().getVideoLeftUrlLowRes();
                }
                presenter.onVideoViewChanged();
                return true;
            case R.id.menu_item_right:
                if (Utils.isConnectedToWifi(this)) {
                    presenter.fetchRouteDetailsVideoGeoPoints(ROUTE_DETAILS_VIDEO_GEOPOINTS_RIGHT);
                    selectedVideoUrl = RouteManager.getSharedInstance().getRouteDetails().getVideoRightUrl();
                } else {
                    presenter.fetchRouteDetailsVideoGeoPoints(ROUTE_DETAILS_VIDEO_GEOPOINTS_LOW_RES_RIGHT);
                    selectedVideoUrl = RouteManager.getSharedInstance().getRouteDetails().getVideoRightUrlLowRes();
                }
                presenter.onVideoViewChanged();
                return true;
            case R.id.menu_item_front:
                if (Utils.isConnectedToWifi(this)) {
                    presenter.fetchRouteDetailsVideoGeoPoints(ROUTE_DETAILS_VIDEO_GEOPOINTS_FRONT);
                    selectedVideoUrl = RouteManager.getSharedInstance().getRouteDetails().getVideoFrontUrl();
                } else {
                    presenter.fetchRouteDetailsVideoGeoPoints(ROUTE_DETAILS_VIDEO_GEOPOINTS_LOW_RES_FRONT);
                    selectedVideoUrl = RouteManager.getSharedInstance().getRouteDetails().getVideoFrontUrlLowRes();
                }
                presenter.onVideoViewChanged();
                return true;
            case R.id.menu_item_back:
                if (Utils.isConnectedToWifi(this)) {
                    presenter.fetchRouteDetailsVideoGeoPoints(ROUTE_DETAILS_VIDEO_GEOPOINTS_BACK);
                    selectedVideoUrl = RouteManager.getSharedInstance().getRouteDetails().getVideoBackUrl();
                } else {
                    presenter.fetchRouteDetailsVideoGeoPoints(ROUTE_DETAILS_VIDEO_GEOPOINTS_LOW_RES_BACK);
                    selectedVideoUrl = RouteManager.getSharedInstance().getRouteDetails().getVideoBackUrlLowRes();
                }
                presenter.onVideoViewChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        simpleExoPlayer.seekTo(currentVideoSecond);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        simpleExoPlayer.setPlayWhenReady(false);
        currentVideoSecond = simpleExoPlayer.getCurrentPosition();
        onPaused();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onStopCalled();
        presenter.stopPolling();
        if (mMap != null) {
            mMap.clear();
        }
        mMap = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        this.mMap.getUiSettings().setZoomControlsEnabled(true);
        this.mMap.getUiSettings().setCompassEnabled(false);

        this.mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                plotVideoGeoPoints(RouteManager.getSharedInstance().getCustomVideoGeoPoints());
                presenter.getRouteDetailStopsGeoPoints();
                mMap.setInfoWindowAdapter(new InfoWindowCustom(VideoMapActivity.this));

                loadInfoMarkers();
            }
        });
    }

    private void plotVideoGeoPoints(List<VideoGeoPoint> videoGeoPoints) {
        for (int i = 0; i < videoGeoPoints.size() - 1; i++) {
            VideoGeoPoint startVideoGeoPoint = videoGeoPoints.get(i);
            VideoGeoPoint endVideoGeoPoint = videoGeoPoints.get(i + 1);
            mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(startVideoGeoPoint.getLat(), startVideoGeoPoint.getLng()), new LatLng(endVideoGeoPoint.getLat(), endVideoGeoPoint.getLng()))
                    .width(5)
                    .color(getResources().getColor(R.color.colorPrimary)));

            // Zoom to Points
            LatLngBounds.Builder bc = new LatLngBounds.Builder();
            for (VideoGeoPoint item : videoGeoPoints) {
                LatLng latLng = new LatLng(item.getLat(), item.getLng());
                bc.include(latLng);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 60));

        }

        presenter.findStartSecondForSelectedRoute();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    //region private methods
    private void setupViews() {
//        simpleExoPlayer = findViewById(R.id.video_view);
        progressBarLayout = findViewById(R.id.progress_layout);
        progressBarTextView = findViewById(R.id.progress_bar_textview);
//        videoProgressBar = findViewById(R.id.video_progress_bar);
        videoSeekBar = findViewById(R.id.video_seek_bar);
        exoPlayerView = findViewById(R.id.simple_exo_player_view);

        videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int seekBarProgress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int videoSecond;
                int startSecond = videoGeoPoints.get(0).getSec();
                int endSecond = videoGeoPoints.get(videoGeoPoints.size() - 1).getSec();
                videoSecond = (seekBarProgress * (endSecond - startSecond) / 100) + startSecond;
                System.out.println("onStopTrackingTouch   startSecond = " + startSecond + "  endSecond = " + endSecond + "  progress = " + seekBarProgress + "  videoSecond = " + videoSecond);
                seekToMillis(videoSecond * 1000);
            }
        });

//        simpleExoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//
//                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                    @Override
//                    public void onSeekComplete(MediaPlayer mp) {
//                        //Seek completed. Move seekbar
//
//                        if (isRestart) {
//                            isRestart = false;
//                            simpleExoPlayer.start();
//                        }
//                    }
//                });
//
//            }
//        });
        bufferingProgressBar = findViewById(R.id.video_buffering_progress_bar);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        navigationDetailsText = findViewById(R.id.video_map_text_navigation_details);
        fullscreenVideoImageView = findViewById(R.id.fullscreen_video_imageview);
        fullscreenMapImageView = findViewById(R.id.fullscreen_map_imageview);
        videoViewLayout = findViewById(R.id.video_view_layout);
        mapViewLayout = findViewById(R.id.map_view_layout);
        fullscreenVideoImageView.setOnClickListener(this);
        fullscreenMapImageView.setOnClickListener(this);
    }

    private void loadDefaultValues() {
        if (Utils.isConnectedToWifi(this)) {
            selectedVideoUrl = RouteManager.getSharedInstance().getRouteDetails().getVideoFrontUrl();
        } else {
            selectedVideoUrl = RouteManager.getSharedInstance().getRouteDetails().getVideoFrontUrlLowRes();
        }
        videoGeoPoints = RouteManager.getSharedInstance().getCustomVideoGeoPoints();
    }


    private void initVideoPlayer() {

//        simpleExoPlayer.setOnInfoListener(onInfoToPlayStateListener);
//        simpleExoPlayer.setOnCompletionListener(this);
//        simpleExoPlayer.setOnErrorListener(this);

        // Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        //Initialize the player
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        //Initialize simpleExoPlayerView
        simpleExoPlayerView = findViewById(R.id.exoplayer);
        simpleExoPlayerView.setPlayer(simpleExoPlayer);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, Util.getUserAgent(this, "CloudinaryExoplayer"));

        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        loadVideo(dataSourceFactory, extractorsFactory);
    }

    private void initMap() {
        mapFragment.getMapAsync(this);
    }

    private void setupVideoControls() {
        videoControlView = findViewById(R.id.video_control);
        videoControlView.setVideoControlClickListener(this);
    }

    private void loadVideo(DataSource.Factory dataSourceFactory, ExtractorsFactory extractorsFactory) {
//        Uri vidUri = Uri.parse(videoUrl);
//        simpleExoPlayer.setVideoURI(vidUri);

        // This is the MediaSource representing the media to be played.
//        Uri videoUri = Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
        Uri videoUri = Uri.parse(selectedVideoUrl);
        MediaSource videoSource = new ExtractorMediaSource(videoUri,
                dataSourceFactory, extractorsFactory, null, null);

        // Prepare the player with the source.
        simpleExoPlayer.prepare(videoSource);
//        player.seekTo(1000);
        simpleExoPlayer.setPlayWhenReady(false);
        simpleExoPlayer.addListener(this);
//        simpleExoPlayer.setPlayPauseListener(this);

    }

    private LatLng getLatLongForSecond(int videoSecond) {
        List<VideoGeoPoint> videoGeoPoints = RouteManager.getSharedInstance().getCustomVideoGeoPoints();
        for (int i = 0; i < videoGeoPoints.size(); i++) {
            if (videoGeoPoints.get(i).getSec() == videoSecond) {
                return new LatLng(videoGeoPoints.get(i).getLat(), videoGeoPoints.get(i).getLng());
            }
        }
        return null;
    }

    private void moveMarker(LatLng toPosition) {

        try {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
            if (carMarker != null) {
                carMarker.remove();
            }
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

//            mMap.setMyLocationEnabled(false);
            mMap.setTrafficEnabled(false);
            mMap.setIndoorEnabled(false);
            mMap.setBuildingsEnabled(true);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(toPosition));
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(mMap.getCameraPosition().target)
                    .zoom(mMap.getCameraPosition().zoom)
//                    .bearing(30)
//                    .tilt(45)
                    .build()));

            carMarker = mMap.addMarker(new MarkerOptions()
                    .position(toPosition)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus))
                    .title("Car"));

            // TODO: 6/16/2018 fetch directions
//            presenter.fetchDirectionDetails(toPosition.toString(), RouteManager.getSharedInstance().getSelectedDestination().getName(),
//                    TravelMode.DRIVING);
            presenter.getDirectionStep(toPosition);

        } catch (Exception e) {
            e.printStackTrace();
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub

                if (presenter.tappedOnRoute(point)) {

                    if (carMarker != null) {
                        carMarker.remove();
                    }
                    carMarker = mMap.addMarker(new MarkerOptions()
                            .position(point)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus))
                            .title("Car"));
                }
            }
        });
    }

    private void adjustViewsForFullScreen(boolean isVideo) {
        if (isVideo) {

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) exoPlayerView.getLayoutParams();
            params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            exoPlayerView.setLayoutParams(params);

            simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

            navigationDetailsText.setVisibility(View.GONE);
            mapViewLayout.setVisibility(View.GONE);
            videoControlView.setVisibility(View.GONE);
            getSupportActionBar().hide();
//            fullscreenVideoImageView.setVisibility(View.GONE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            navigationDetailsText.setVisibility(View.GONE);
            videoViewLayout.setVisibility(View.GONE);
            videoControlView.setVisibility(View.GONE);
//            fullscreenMapImageView.setVisibility(View.GONE);
            getSupportActionBar().hide();
        }
    }

    private void resetViewsToOriginalState(boolean isVideo) {
        getSupportActionBar().show();
        if (isVideo) {

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) exoPlayerView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            exoPlayerView.setLayoutParams(params);

            simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

            navigationDetailsText.setVisibility(View.VISIBLE);
            mapViewLayout.setVisibility(View.VISIBLE);
            videoControlView.setVisibility(View.VISIBLE);
//            fullscreenVideoImageView.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            navigationDetailsText.setVisibility(View.VISIBLE);
            videoViewLayout.setVisibility(View.VISIBLE);
            videoControlView.setVisibility(View.VISIBLE);
//            fullscreenMapImageView.setVisibility(View.VISIBLE);
        }
    }

    private void checkIfVideoPausedApiCallsCompleted() {
        if (videoPausedStateApiCalls == 4) {
            presenter.hideProgressBar();
        }
    }

    private void keepScreenOn(boolean isOn) {
        if (isOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void showPauseButton() {
        videoControlView.showPauseButton();
    }
    //endregion

    @Override
    public void onRestartClicked() {
//        simpleExoPlayer.seekTo(0);
//        simpleExoPlayer.start();
        this.isRestart = true;
        presenter.findStartSecondForSelectedRoute();
        showPauseButton();
        onPlayOrPauseClicked();

    }

    @Override
    public void onPlayOrPauseClicked() {
        simpleExoPlayer.setPlayWhenReady(!simpleExoPlayer.getPlayWhenReady());

        if (!simpleExoPlayer.getPlayWhenReady()) {
            videoPausedStateApiCalls = 0;
            onPaused();
            loadInfoMarkers();
        } else {
            for (Marker marker : poiMarkers) {
                if (marker != null) {
                    marker.remove();
                }
            }
            onPlaying();
            poiMarkers.clear();
        }
    }

    public void loadInfoMarkers() {
        progressBarTextView.setText(getResources().getString(R.string.loading_poi));
        presenter.showProgressBar();
        presenter.loadPoiNearCar(getLatLongForSecond((int) TimeUnit.MILLISECONDS.toSeconds(simpleExoPlayer.getCurrentPosition())));
        presenter.loadCustomPoi();
        presenter.loadMeetup(getLatLongForSecond((int) TimeUnit.MILLISECONDS.toSeconds(simpleExoPlayer.getCurrentPosition())));
        presenter.loadCoord(getLatLongForSecond((int) TimeUnit.MILLISECONDS.toSeconds(simpleExoPlayer.getCurrentPosition())));

    }

    @Override
    public void onViewPOIsClicked() {
        carCurrentLocation = getLatLongForSecond((int) TimeUnit.MILLISECONDS.toSeconds(simpleExoPlayer.getCurrentPosition()));
        Intent intent = new Intent(VideoMapActivity.this, PlaceOfInterestActivity.class);
        intent.putExtra("latitude", carCurrentLocation.latitude + "");
        intent.putExtra("longitude", carCurrentLocation.longitude + "");
        startActivity(intent);
    }


    @Override
    public void onNavigationClicked() {
        presenter.onMapNavigationButtonClick();
    }

    //region Video View callbacks
    @Override
    public void onPlaying() {
//        simpleExoPlayer.setPlayWhenReady(true);
        keepScreenOn(true);
        System.out.println("Video Played");
        videoControlView.changePlayOrPauseIcon(false);
        presenter.startPolling();
    }

    @Override
    public void onPaused() {
//        simpleExoPlayer.setPlayWhenReady(false);
        keepScreenOn(false);
        System.out.println("Video Paused");
        videoControlView.changePlayOrPauseIcon(true);
        presenter.stopPolling();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        System.out.println("Video Stopped");
        videoControlView.changePlayOrPauseIcon(true);
        presenter.stopPolling();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //hide buffering progress bar
        presenter.hideBufferingProgressBar();
        return false;
    }

    @Override
    public void releasePlayer() {
        simpleExoPlayer.stop();
    }

    //endregion

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public int getCurrentVideoTime() {
        return (int) simpleExoPlayer.getCurrentPosition();
    }

    @Override
    public int getVideoDuration() {
        return (int) simpleExoPlayer.getDuration();
    }

    @Override
    public void addMarkerOnMap(int videoSecond) {
        //add marker on map
        if (this.mMap != null) {
            moveMarker(getLatLongForSecond(videoSecond));
        }
        float startSecond = videoGeoPoints.get(0).getSec();
        float endSecond = videoGeoPoints.get(videoGeoPoints.size() - 1).getSec();
        float progress = (videoSecond - startSecond) / (endSecond - startSecond) * 100;
        videoSeekBar.setProgress((int) progress);
        System.out.println("addMarkerOnMap   startSecond = " + startSecond + "  endSecond = " + endSecond + "  progress = " + progress);
    }

    @Override
    public void seekToMillis(int milliSeconds) {
        simpleExoPlayer.seekTo(milliSeconds);
    }

    @Override
    public void showBufferingProgressBar() {
        bufferingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideBufferingProgressBar() {
        bufferingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showMapNavigation() {
        Intent intent = new Intent(VideoMapActivity.this, NavigationActivity.class);
        intent.putExtra("Direction", getIntent().getStringExtra("Direction"));
        intent.putExtra("RouteName", getIntent().getStringExtra("RouteName"));
        startActivity(intent);
    }

    @Override
    public void setNavigationDetailText(String detail) {
        navigationDetailsText.setText(Utils.fromHtml(detail));
    }

    @Override
    public void showFullscreenVideoViewOrMapView(boolean isVideo) {
        if (isFullscreen) {
            adjustViewsForFullScreen(isVideo);
        } else {
            resetViewsToOriginalState(isVideo);
        }

    }

    @Override
    public void loadPoiNearStop(List<MarkerInfo> poiList) {

        BitmapDrawable bitmapDrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_marker_custom_poi);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, markerSize, markerSize, false);

        for (int i = 0; i < poiList.size(); i++) {
            meetupMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(poiList.get(i).getLatitude(), poiList.get(i).getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    .title(poiList.get(i).getTitle()));

            poiMarkers.add(meetupMarker);
        }
        videoPausedStateApiCalls++;
        checkIfVideoPausedApiCallsCompleted();
    }

    @Override
    public void loadCustomPoi(List<MarkerInfo> customPoiList) {

        BitmapDrawable bitmapDrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_marker_custom_poi);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, markerSize, markerSize, false);

        for (int i = 0; i < customPoiList.size(); i++) {

            if (!customPoiList.get(i).getIcon().isEmpty()) {
                Bitmap markerBitmap = Utils.getBase64Bitmap(customPoiList.get(i).getIcon());
                Bitmap smallMarkerIcon = Bitmap.createScaledBitmap(markerBitmap, markerSize, markerSize, false);

                meetupMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(customPoiList.get(i).getLatitude(), customPoiList.get(i).getLongitude()))
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarkerIcon))
                        .title(customPoiList.get(i).getTitle())
                        .snippet(customPoiList.get(i).getSubtitle()));

            } else {

                meetupMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(customPoiList.get(i).getLatitude(), customPoiList.get(i).getLongitude()))
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                        .title(customPoiList.get(i).getTitle())
                        .snippet(customPoiList.get(i).getSubtitle()));
            }

            poiMarkers.add(meetupMarker);
        }
        videoPausedStateApiCalls++;
        checkIfVideoPausedApiCallsCompleted();
    }

    @Override
    public void plotMeetup(List<MarkerInfo> meetupList) {

//        BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_meetup)
        BitmapDrawable bitmapDrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_marker_meetup);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, markerSize, markerSize, false);

        for (int i = 0; i < meetupList.size(); i++) {
            meetupMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(meetupList.get(i).getLatitude(), meetupList.get(i).getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    .title(meetupList.get(i).getTitle())
                    .snippet(meetupList.get(i).getSubtitle()));
            poiMarkers.add(meetupMarker);
        }
        videoPausedStateApiCalls++;
        checkIfVideoPausedApiCallsCompleted();
    }

    @Override
    public void plotCoord(List<MarkerInfo> coordList) {
        BitmapDrawable bitmapDrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_marker_bike);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, markerSize, markerSize, false);

        for (int i = 0; i < coordList.size(); i++) {
            coordMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(coordList.get(i).getLatitude(), coordList.get(i).getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    .title(coordList.get(i).getTitle())
                    .snippet(coordList.get(i).getSubtitle()));
            poiMarkers.add(coordMarker);
        }
        videoPausedStateApiCalls++;
        checkIfVideoPausedApiCallsCompleted();
    }

    @Override
    public void showProgressBar() {
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBarLayout.setVisibility(View.GONE);
    }

    @Override
    public void hidePauseButton() {
        videoControlView.hidePauseButton();
    }

    @Override
    public void plotRouteDetailVideoGeoPointsOnMap() {
        mMap.clear();
        plotVideoGeoPoints(RouteManager.getSharedInstance().getCustomVideoGeoPoints());
        presenter.getRouteDetailStopsGeoPoints();
    }

    @Override
    public void changeProgressBarText(String progressBarText) {
        progressBarTextView.setText(progressBarText);
    }

    @Override
    public void resetVideoView() {
        simpleExoPlayer.stop();
//        simpleExoPlayer.setOnInfoListener(null);
//        simpleExoPlayer.setOnCompletionListener(null);
//        simpleExoPlayer.setOnErrorListener(null);

        videoControlView.changePlayOrPauseIcon(true);
        presenter.stopPolling();

        initVideoPlayer();
        setupVideoControls();
    }

    @Override
    public void plotRouteDetailStopsOnMap(List<Stop> stops) {

        BitmapDrawable bitmapDrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_map_stop);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, markerSize, markerSize, false);

        for (int i = 0; i < stops.size(); i++) {
            stopsMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(stops.get(i).getLat(), stops.get(i).getLng()))
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    .title(stops.get(i).getName())
                    .snippet("Connections: " + stops.get(i).getRoute_list() + "\nAmenities: " + stops.get(i).getAmenities()));
        }

        com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(RouteManager.getSharedInstance().getSelectedDeparture().getLat(), RouteManager.getSharedInstance().getSelectedDeparture().getLng());
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(RouteManager.getSharedInstance().getSelectedDestination().getLat(), RouteManager.getSharedInstance().getSelectedDestination().getLng());
        presenter.fetchDirectionDetails(origin, destination, TravelMode.DRIVING);
    }

    @Override
    public void stopVideo() {
        presenter.onStopCalled();
    }

    private final MediaPlayer.OnInfoListener onInfoToPlayStateListener = new MediaPlayer.OnInfoListener() {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START == what) {
                presenter.hideBufferingProgressBar();
            }
            if (MediaPlayer.MEDIA_INFO_BUFFERING_START == what) {
                presenter.showBufferingProgressBar();
            }
            if (MediaPlayer.MEDIA_INFO_BUFFERING_END == what) {
                presenter.hideBufferingProgressBar();
            }
            return false;
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fullscreen_video_imageview:
                isFullscreen = !isFullscreen;
                presenter.fullscreenButtonClicked(true);
                break;
            case R.id.fullscreen_map_imageview:
                isFullscreen = !isFullscreen;
                presenter.fullscreenButtonClicked(false);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            if (videoViewLayout.getVisibility() == View.VISIBLE) {
                resetViewsToOriginalState(true);
            } else {
                resetViewsToOriginalState(false);
            }
            isFullscreen = !isFullscreen;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        } else {
            finish();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void asyncTaskResult(JSONObject jsonObject) {
        Log.d("AsyncJsonResult", "Result = " + jsonObject.toString());
        presenter.hideProgressBar();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_BUFFERING){
            presenter.showBufferingProgressBar();
        } else {
            presenter.hideBufferingProgressBar();
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
}
