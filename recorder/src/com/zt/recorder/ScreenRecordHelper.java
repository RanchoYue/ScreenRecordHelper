package com.zt.recorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ScreenUtils;

import java.io.IOException;

/**
 * @Describe:
 * @Author: Created by yue on 2020/03/31.
 */
public class ScreenRecordHelper {
    private static final ScreenRecordHelper INSTANCE = new ScreenRecordHelper();
    private static final String TAG = ScreenRecordHelper.class.getSimpleName();
    public static final int RECORD_REQUEST_CODE = 101;
    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private MediaRecorder mediaRecorder;
    private boolean isRecording;

    public static ScreenRecordHelper getInstance() {
        return INSTANCE;
    }

    private ScreenRecordHelper() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startRecord() {
        final Activity activity = ActivityUtils.getTopActivity();
        projectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        PermissionUtils.permission(PermissionConstants.STORAGE, PermissionConstants.MICROPHONE)
                .callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        Intent captureIntent = projectionManager.createScreenCaptureIntent();
                        activity.startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
                    }

                    @Override
                    public void onDenied() {

                    }
                }).request();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mediaProjection = projectionManager.getMediaProjection(resultCode, data);
                initRecorder();
                mediaRecorder.start();
                isRecording = true;
            }
        }
    }

    private void initRecorder() {
        final int width = ScreenUtils.getScreenWidth();
        final int height = ScreenUtils.getScreenHeight();
        final int dpi = ScreenUtils.getScreenDensityDpi();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(PathUtils.getExternalAppCachePath() + "/tmp.mp4");
        mediaRecorder.setVideoSize(width, height);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        mediaRecorder.setVideoFrameRate(30);
        try {
            mediaRecorder.prepare();
            virtualDisplay = mediaProjection.createVirtualDisplay("MainScreen", width, height, dpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "IOException " + e);
        }
    }

    public void stopRecord() {
        if (isRecording) {
            isRecording = false;
            try {
                mediaRecorder.setOnErrorListener(null);
                mediaRecorder.setOnInfoListener(null);
                mediaRecorder.setPreviewDisplay(null);
                mediaRecorder.stop();
                Log.d(TAG, "stop success");
            } catch (Exception e) {
                Log.e(TAG, "stopRecorder error " + e.getMessage());
            } finally {
                mediaRecorder.reset();
                virtualDisplay.release();
                mediaProjection.stop();
            }
        }
    }

}
