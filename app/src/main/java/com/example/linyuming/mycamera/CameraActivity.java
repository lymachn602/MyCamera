package com.example.linyuming.mycamera;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener,SurfaceHolder.Callback{
    private ImageView takePhoto,preview;
    private SurfaceView tp_sv;
    private Camera camera;
    private boolean mPreviewRuning;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initView();
        preview.setVisibility(View.GONE);
    }

    private void initView() {
        takePhoto= (ImageView) findViewById(R.id.takePhoto);
        preview= (ImageView) findViewById(R.id.img_sv_photo);
        takePhoto.setOnClickListener(this);
        tp_sv= (SurfaceView) findViewById(R.id.sv_photo);
        SurfaceHolder holder=tp_sv.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void onClick(View view) {



        if(mPreviewRuning){
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    camera.takePicture(new Camera.ShutterCallback() {
                        @Override
                        public void onShutter() {

                        }
                    }, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] bytes, Camera camera) {
                            if(bytes!=null){
                                saveAndShow(bytes);
                            }
                        }
                    });

                }
            });
            mPreviewRuning=false;
        }
    }

    private void saveAndShow(byte[] bytes) {
        String path= Environment.getExternalStorageDirectory().getPath()+"/mycamera";
        String name=System.currentTimeMillis()+".jpeg";
        File file=new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        file=new File(path+File.separator+name);
        if(!file.exists()){
            try {
                file.createNewFile();
                FileOutputStream fos=new FileOutputStream(file);
                fos.write(bytes);
                fos.close();
                Uri uri= Uri.fromFile(file);
                preview.setImageURI(uri);
                preview.setVisibility(View.VISIBLE);
                tp_sv.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setPhotoParams(surfaceHolder);
    }

    private void setPhotoParams(SurfaceHolder surfaceHolder) {
        if(camera!=null){
            return;
        }
        try {
            camera=Camera.open();
            Camera.Parameters params=camera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            params.getSupportedPictureFormats().size();
            params.setPreviewFpsRange(2,30);

            params.setJpegQuality(85);
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            mPreviewRuning=true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(camera!=null){
            camera.stopPreview();
            camera.release();
            camera=null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0,"重拍");
        menu.add(0,1,0,"打开相册");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 0:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case 1:
                Intent intent=new Intent(this,AblumActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}