package com.juzi.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class ClipImageActivity extends Activity {

    private ImageView mIvBack;
    private TextView mStockName;
    private ClipViewLayout mClipViewLayout1;
    private ClipViewLayout mClipViewLayout2;
    private RelativeLayout mBottom;
    private TextView mBtnCancel;
    private TextView mBtOk;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_image);
        type = getIntent().getIntExtra("type", 1);
        initView();
    }

    private void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mStockName = (TextView) findViewById(R.id.stock_name);
        mClipViewLayout1 = (ClipViewLayout) findViewById(R.id.clipViewLayout1);
        mClipViewLayout2 = (ClipViewLayout) findViewById(R.id.clipViewLayout2);
        mBottom = (RelativeLayout) findViewById(R.id.bottom);
        mBtnCancel = (TextView) findViewById(R.id.btn_cancel);
        mBtOk = (TextView) findViewById(R.id.bt_ok);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateUriAndReturn();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (type == 1) {
            mClipViewLayout1.setVisibility(View.VISIBLE);
            mClipViewLayout2.setVisibility(View.GONE);
            //设置图片资源
            mClipViewLayout1.setImageSrc(getIntent().getData());
        } else {
            mClipViewLayout2.setVisibility(View.VISIBLE);
            mClipViewLayout1.setVisibility(View.GONE);
            //设置图片资源
            mClipViewLayout2.setImageSrc(getIntent().getData());
        }
    }

    /**
     * 生成Uri并且通过setResult返回给打开的activity
     */
    private void generateUriAndReturn() {
        //调用返回剪切图
        Bitmap zoomedCropBitmap;
        if (type == 1) {
            zoomedCropBitmap = mClipViewLayout1.clip();
        } else {
            zoomedCropBitmap = mClipViewLayout2.clip();
        }
        if (zoomedCropBitmap == null) {
            Log.e("android", "zoomedCropBitmap == null");
            return;
        }
        Uri mSaveUri = Uri.fromFile(new File(getCacheDir(), "cropped_" + System.currentTimeMillis() + ".jpg"));
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(mSaveUri);
                if (outputStream != null) {
                    zoomedCropBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                }
            } catch (IOException ex) {
                Log.e("android", "Cannot open file: " + mSaveUri, ex);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Intent intent = new Intent();
            intent.setData(mSaveUri);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
