package com.iflytek.mscv5plusdemo.bridgewebview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

import com.iflytek.mscv5plusdemo.R;

/**
 * Created by taoxingyu on 2018/12/28.
 */

public class BridgeWebViewActivity extends AppCompatActivity {

    private BridgeWebView mWeview;
    public static String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = getIntent().getExtras().getString("result");
        initView();
        setWebViewClient();
    }

    private void initView() {

        //获取布局里面的BridgeWebView
        mWeview = (BridgeWebView) findViewById(R.id.weview);

        //设置js和android通信桥梁方法
        mWeview.addBridgeInterface(new JavaSctiptMethods(BridgeWebViewActivity.this, mWeview));
        //mWeview.loadUrl("http://10.0.3.2:8080/BridgeWebView/index.html");//显示网页,在线模板
        mWeview.loadUrl("file:///android_asset/BridgeWebView/index.html");//本地模板

    }

    private void setWebViewClient() {
        mWeview.setWebViewClient(new WebViewClient());

        mWeview.setWebChromeClient(new WebChromeClient());
    }
}
