package com.example.linyuming.mycamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.File;
import java.util.Vector;

public class AblumActivity extends AppCompatActivity {
    private ViewFlipper vf;
    private Bitmap[] mapList;
    private long startTime = 0;
    private SensorManager sm;
    private SensorEventListener sel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ablum);
        vf= (ViewFlipper) findViewById(R.id.vf_ablum);
        loadAlum();
        initData();
        setFlipper();
    }
    private void setFlipper() {
        sm= (SensorManager) this.getSystemService(SENSOR_SERVICE);
        Sensor se=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sel=new SensorEventListener() { //添加传感器监听
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x=sensorEvent.values[SensorManager.DATA_X];
                if(x>10&&System.currentTimeMillis()>startTime+1000){
                    startTime=System.currentTimeMillis();
                    vf.setInAnimation(AnimationUtils.loadAnimation(AblumActivity.this,R.anim.push_left_in));
                    vf.setInAnimation(AnimationUtils.loadAnimation(AblumActivity.this,R.anim.push_left_out));
                    vf.showPrevious();
                }else if(x<-10&&System.currentTimeMillis()>startTime+1000){
                    startTime=System.currentTimeMillis();
                    vf.setInAnimation(AnimationUtils.loadAnimation(AblumActivity.this,R.anim.push_right_in));
                    vf.setInAnimation(AnimationUtils.loadAnimation(AblumActivity.this,R.anim.push_right_out));
                    vf.showNext();

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sm.registerListener(sel,se,SensorManager.SENSOR_DELAY_GAME);

    }

    private void initData() {
        if (mapList.length == 0 || mapList == null) {
            Toast.makeText(this, "相册无照片", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else {
            for (int i = 0; i < mapList.length; i++) {
                vf.addView(addImage(mapList[i]), i, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }//往viewFillper 动态添加view；     }
        }
    }
    public String[] loadAlum()
    {
        String pathName = Environment.getExternalStorageDirectory().getPath() + "/mycamera";
        File file = new File(pathName);
        Vector<Bitmap> fileName = new Vector<Bitmap>();
        if (file.exists() && file.isDirectory()) {
            String[] str = file.list();
            for (String name : str) {
                if (new File(pathName + File.separator + name).isFile()) {
                    fileName.addElement(loadImage(pathName + File.separator + name));
                }
            }
            mapList=fileName.toArray(new Bitmap[]{});
        }
        return null;
    }

    public Bitmap loadImage(String path) {
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        Bitmap bit=BitmapFactory.decodeFile(path,options);
        DisplayMetrics dp= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);
        int sreenWidth=dp.widthPixels;
        options.inSampleSize=options.outWidth/sreenWidth;
        options.inJustDecodeBounds=false;
        bit=BitmapFactory.decodeFile(path,options);
        return  bit;

    }
    private  View addImage(Bitmap bitmap){
        ImageView img=new ImageView(this);
        img.setImageBitmap(bitmap);
        return img;
    }
}