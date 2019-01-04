package com.iflytek.mscv5plusdemo.bridgewebview;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by youliang.ji on 2016/12/23.
 */

public class JavaSctiptMethods {
    private WebView webView;
    private Activity mActivity;

    public JavaSctiptMethods(Activity mContext, WebView webView) {
        this.mActivity = mContext;
        this.webView = webView;
    }

    /**
     * 统一分发js调用android分发
     */
    public void send(String[] jsons) {
        final String str = jsons[0];
        showLog(str);
        try {
            JSONObject json = new JSONObject(str);
            String action = json.optString("action");//js传递过来的动作，比如callPhone代表拨号，share2QQ代表分享到QQ，其实就是H5和android通信协议（自定义的）
            if (!TextUtils.isEmpty(action)) {
                if (action.equals("getResult")) {
                    getResult(str);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void getResult(String str) {
        try {
            //解析js callback方法
            JSONObject mJson = new JSONObject(str);
            String callback = mJson.optString("callback");//解析js回调方法

            JSONObject json = new JSONObject();
            //传值到js
            json.put("result", BridgeWebViewActivity.result);
            json.put("voiceMessage", BridgeWebViewActivity.voiceMessage);

            //调用js方法必须在主线程
//            webView.loadUrl("javascript:"+callback+"(" + json.toString() + ")");
            invokeJavaScript(callback, json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 统一管理所有android调用js方法
     *
     * @param callback js回调方法名
     * @param json     传递json数据
     */
    private void invokeJavaScript(final String callback, final String json) {
        showToast("回调js方法：" + callback + ", 参数：" + json);

        if (TextUtils.isEmpty(callback)) return;
        //调用js方法必须在主线程
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:" + callback + "(" + json + ")");
            }
        });
    }

    public void showToast(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String msg = jsonObject.optString("msg");
            Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void showLog(String msg) {
        Log.i("result", "" + msg);
    }

}
