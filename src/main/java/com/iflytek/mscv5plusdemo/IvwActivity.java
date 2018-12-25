package com.iflytek.mscv5plusdemo;

import com.iflytek.cloud.VoiceWakeuper;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class IvwActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ivw_activity);

        ((Button) findViewById(R.id.btn_wake)).setOnClickListener(IvwActivity.this);
        ((Button) findViewById(R.id.btn_oneshot)).setOnClickListener(IvwActivity.this);

    }

    @Override
    public void onClick(View v) {
        if (null == VoiceWakeuper.createWakeuper(this, null)) {
            // 创建单例失败iv，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            Toast.makeText(this
                    , "创建对象失败，请确认 libmsc.so 放置正确，\n 且有调用 createUtility 进行初始化"
                    , Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_wake:
                intent = new Intent(IvwActivity.this, WakeDemo.class);
                startActivity(intent);
                break;

            case R.id.btn_oneshot:
                intent = new Intent(IvwActivity.this, OneShotDemo.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 唤醒手机屏幕并解锁
     */
    public void wakeUpAndUnlock() {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) IvwActivity.this.getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire(10000); // 点亮屏幕
            wl.release(); // 释放
        }
        // 屏幕解锁
        KeyguardManager keyguardManager = (KeyguardManager) IvwActivity.this.getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
        // 屏幕锁定
        keyguardLock.reenableKeyguard();
        keyguardLock.disableKeyguard(); // 解锁
    }
}
