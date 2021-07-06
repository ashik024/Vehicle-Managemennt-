package com.example.takeComplain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.common_class.CommonClass;
import com.example.mbw.R;
import com.example.utils.ToastCustom;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "mycamera";
    private Camera camera;
    FrameLayout cameraPreviewLayout;
    LinearLayout capturedImageHolder;
    ArrayList<Bitmap> capturedImgBitmapList = new ArrayList<>();
    ArrayList<byte[]> capturedImgByteList = new ArrayList<>();
    TextView textView;
    private Realm realm;
    private static final int PERMISSION_STORAGE_CODE = 1000;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        reqPermission();
        SharedPreferences prefs = getSharedPreferences("GlovePharma", MODE_PRIVATE);
        int idName = prefs.getInt("CameraReview", 0);
        if(idName==0)
        {
            textView=findViewById(R.id.tvBackButton);
            textView.setVisibility(View.VISIBLE);
            if(CommonClass.locationSave==7)
            {
                textView.setText("After Taking Picture press Back button");
            }
            else if(CommonClass.locationSave==3)
            {
                textView.setText("Please Take A photo with your face");
            }
            textView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.right_in));
            SharedPreferences.Editor editor = getSharedPreferences("GlovePharma", MODE_PRIVATE).edit();
            editor.putInt("NotificationID",1);
            editor.apply();
        }

    }

    private void reqPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {

                //permission denied, request it
                String[] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};

                ActivityCompat.requestPermissions(this, permission, PERMISSION_STORAGE_CODE);

            } else {
                //permission already granted
                initCamera();
            }
        } else {

            //system os is less than marshmallow
            initCamera();
        }
    }
/*    private void reqPermission() {
        Dexter.withContext(CameraActivity.this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    Log.d(TAG, "onPermissionsChecked: all permission granted");
                    initCamera();
                } else {
                    reqPermission();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }

        }).check();
    }*/
    private void initCamera() {
        cameraPreviewLayout = findViewById(R.id.camera_preview);
        capturedImageHolder = findViewById(R.id.captured_image);
        camera = getCameraInstance();
        if (camera != null) {
            CameraPreview cameraPreview = new CameraPreview(CameraActivity.this, camera);
            cameraPreviewLayout.addView(cameraPreview);

            FloatingActionButton captureButton = findViewById(R.id.camera);
            captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (camera != null) {
                        camera.takePicture(null, null, mPicture);
                    } else {
                      //  Log.d(TAG, "onClick: camera getting null");
                    }
                }
            });

        } else {
          //  Log.d(TAG, "initCamera: camera getting null");
        }
    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            if(CommonClass.locationSave==3)
                c = Camera.open(1);
            else {
                c = Camera.open();
            }
        } catch (Exception e) {
           // Log.d("TAG", "getCameraInstance: " + e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.startPreview();
            //Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            try {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            } catch (OutOfMemoryError oome) {
                Toast.makeText(CameraActivity.this, "Memory Is Full", Toast.LENGTH_SHORT).show();
            }
            if (bitmap == null) {
                Toast.makeText(CameraActivity.this, "Captured image is empty", Toast.LENGTH_LONG).show();
                return;
            }



            capturedImgByteList.add(data);
            capturedImgBitmapList.add(bitmap);

            if(CommonClass.locationSave==3)
            {
                CommonClass.locationSave=17;
                CommonClass.selectedImage=data;
                onBackPressed();
            }
            if (capturedImgBitmapList.size() > 0) {
                //
                capturedImageHolder.removeAllViews();
                //
                for (Bitmap bit : capturedImgBitmapList) {
                    ImageView imageView = new ImageView(CameraActivity.this);
                    imageView.setImageBitmap(bit);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dpToPx(150), dpToPx(200));
                    lp.setMargins(dpToPx(2), 0, dpToPx(2), 0);
                    imageView.setLayoutParams(lp);
                    imageView.setRotation(90);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    capturedImageHolder.addView(imageView);
                }
            }
           // Log.d(TAG, "onPictureTaken: capturedImgBitmap size = " + capturedImgBitmapList.size());
        }
    };

    public int dpToPx(int dp) {
        return (int) (dp * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }

    @Override
    public void onBackPressed() {
        CommonClass.capturedImgByteList = capturedImgByteList;
        CommonClass.capturedImgBitmapList = capturedImgBitmapList;
        setResult(RESULT_OK);
        finish();
        super.onBackPressed();
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1000: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] ==
                        PackageManager.PERMISSION_GRANTED) {
                    ToastCustom.ToastMsg(this, "Permission Granted");

                    initCamera();

                } else {
                    ToastCustom.ToastMsg(this, "Permission Denied");
                    reqPermission();
                }
            }
            return;

        }
    }
}