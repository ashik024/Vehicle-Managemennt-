package com.example.takeComplain;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "mycamera";
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
    }
    public boolean support_focus(Camera camera){
        Camera.Parameters parameters = camera.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
            return true;
        else
            return false;
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setDisplayOrientation(90);

            Camera.Parameters parameters = mCamera.getParameters();
            // sett focus
          //  Log.d(TAG, "surfaceCreated: " + parameters.getSupportedFocusModes());
            if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                if(support_focus(mCamera))
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            Camera.Parameters params = mCamera.getParameters();

            if (params.getFlashMode() != null)
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

            else if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            } else if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
            }
            //
//            Camera.Size bestSize;
//            List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
//            bestSize = sizeList.get(0);
//            for (int i = 1; i < sizeList.size(); i++) {
//                if ((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)) {
//                    bestSize = sizeList.get(i);
//                }
//            }

            List<Integer> supportedPreviewFormats = parameters.getSupportedPreviewFormats();
            for (Integer previewFormat : supportedPreviewFormats) {
                if (previewFormat == ImageFormat.YV12) {
                    parameters.setPreviewFormat(previewFormat);
                }
            }
//            List<Camera.Size> allSizes = mCamera.getParameters().getSupportedPreviewSizes();
//           // List<Camera.Size> allSizes = parameters.getSupportedPictureSizes();
//            Camera.Size size = allSizes.get(0); // get top size
//            for (int i = 0; i < allSizes.size(); i++) {
//                if (allSizes.get(i).width > size.width)
//                    size = allSizes.get(i);
//            }
//            //support_focus(mCamera);
//            parameters.setPictureSize(size.width, size.height);
//            parameters.setPreviewSize(size.width, size.height);
            // parameters.setPictureSize(bestSize.width, bestSize.height);

            mCamera.setParameters(parameters);
            mCamera.startPreview();
        } catch (IOException e) {
          //  Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
          //  Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}
