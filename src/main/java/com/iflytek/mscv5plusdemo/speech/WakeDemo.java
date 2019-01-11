package com.iflytek.mscv5plusdemo.speech;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;
import com.iflytek.mscv5plusdemo.R;
import com.iflytek.mscv5plusdemo.bean.info;
import com.iflytek.mscv5plusdemo.bridgewebview.BridgeWebViewActivity;
import com.iflytek.mscv5plusdemo.retrofit.RetrofitApi;
import com.iflytek.mscv5plusdemo.bean.DataList;
import com.iflytek.mscv5plusdemo.bean.SearchResult;
import com.iflytek.mscv5plusdemo.setting.IatSettings;
import com.iflytek.mscv5plusdemo.util.JsonParser;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WakeDemo extends Activity implements OnClickListener {
    //z-1.0.1
    private String TAG = "ivw";
    private Toast mToast;
    private TextView textView;
    // 语音唤醒对象
    private VoiceWakeuper mIvw;
    // 唤醒结果内容
    private String resultString;

    // 设置门限值 ： 门限值越低越容易被唤醒
    private TextView tvThresh;
    private SeekBar seekbarThresh;
    private final static int MAX = 3000;
    private final static int MIN = 0;
    private int curThresh = 1450;
    private String threshStr = "门限值：";
    private String keep_alive = "1";
    private String ivwNetMode = "0";
    //private IvwActivity activity;
    private Context mContext;
    private ResolveInfo homeInfo;

    int ret = 0;// 函数调用返回值

    // 语音听写对象
    private SpeechRecognizer mIat;

    // 语音听写UI
    private RecognizerDialog mIatDialog;

    private SharedPreferences mSharedPreferences;

    // 默认识别模式为云端
    private String mEngineType = "cloud";

    private boolean mTranslateEnable = false;

    //语音识别的结果
    private String text;
    private ArrayList<info> mlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wake_activity);
        mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME, Activity.MODE_PRIVATE);
        mContext = getApplicationContext();


        PackageManager pm = getPackageManager();
        homeInfo = pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);

        initUi();
        // 初始化唤醒对象
        mIvw = VoiceWakeuper.createWakeuper(this, null);
    }


    @SuppressLint("ShowToast")
    private void initUi() {
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        textView = (TextView) findViewById(R.id.txt_show_msg);
        tvThresh = (TextView) findViewById(R.id.txt_thresh);
        seekbarThresh = (SeekBar) findViewById(R.id.seekBar_thresh);
        seekbarThresh.setMax(MAX - MIN);
        seekbarThresh.setProgress(curThresh);
        tvThresh.setText(threshStr + curThresh);
        seekbarThresh.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                curThresh = seekbarThresh.getProgress() + MIN;
                tvThresh.setText(threshStr + curThresh);
            }
        });

        RadioGroup group = (RadioGroup) findViewById(R.id.ivw_net_mode);
        group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                /**
                 * 闭环优化网络模式有三种：
                 * 模式0：关闭闭环优化功能
                 *
                 * 模式1：开启闭环优化功能，允许上传优化数据。需开发者自行管理优化资源。
                 * sdk提供相应的查询和下载接口，请开发者参考API文档，具体使用请参考本示例
                 * queryResource及downloadResource方法；
                 *
                 * 模式2：开启闭环优化功能，允许上传优化数据及启动唤醒时进行资源查询下载；
                 * 本示例为方便开发者使用仅展示模式0和模式2；
                 */
                switch (arg1) {
                    case R.id.mode_close:
                        ivwNetMode = "0";
                        break;
                    case R.id.mode_open:
                        ivwNetMode = "1";
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                //非空判断，防止因空指针使程序崩溃
                mIvw = VoiceWakeuper.getWakeuper();
                if (mIvw != null) {
                    setRadioEnable(false);
                    resultString = "";
                    textView.setText(resultString);

                    // 清空参数
                    mIvw.setParameter(SpeechConstant.PARAMS, null);
                    // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
                    mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + curThresh);
                    // 设置唤醒模式
                    mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
                    // 设置持续进行唤醒
                    mIvw.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
                    // 设置闭环优化网络模式
                    mIvw.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
                    // 设置唤醒资源路径
                    mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getResource());
                    // 设置唤醒录音保存路径，保存最近一分钟的音频
                    mIvw.setParameter(SpeechConstant.IVW_AUDIO_PATH, Environment.getExternalStorageDirectory().getPath() + "/msc/ivw.wav");
                    mIvw.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
                    //mIvw.setParameter(SpeechConstant.VOICE_NAME, "xiaorong");//设置发音人 四川话
                    // 如有需要，设置 NOTIFY_RECORD_DATA 以实时通过 onEvent 返回录音音频流字节
                    //mIvw.setParameter( SpeechConstant.NOTIFY_RECORD_DATA, "1" );

                    // 启动唤醒
                    mIvw.startListening(mWakeuperListener);
                } else {
                    showTip("唤醒未初始化");
                }
                break;
            case R.id.btn_stop:
                mIvw.stopListening();
                setRadioEnable(true);
                break;
            default:
                break;
        }
    }

    /**
     * 查询闭环优化唤醒资源
     * 请在闭环优化网络模式1或者模式2使用
     */
    public void queryResource() {
        int ret = mIvw.queryResource(getResource(), requestListener);
        showTip("updateResource ret:" + ret);
    }


    // 查询资源请求回调监听
    private RequestListener requestListener = new RequestListener() {
        @Override
        public void onEvent(int eventType, Bundle params) {
            // 以下代码用于获取查询会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                Log.d(TAG, "sid:" + params.getString(SpeechEvent.KEY_EVENT_SESSION_ID));
            }
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error != null) {
                Log.d(TAG, "error:" + error.getErrorCode());
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            try {
                String resultInfo = new String(buffer, "utf-8");
                Log.d(TAG, "resultInfo:" + resultInfo);

                JSONTokener tokener = new JSONTokener(resultInfo);
                JSONObject object = new JSONObject(tokener);

                int ret = object.getInt("ret");
                if (ret == 0) {
                    String uri = object.getString("dlurl");
                    String md5 = object.getString("md5");
                    Log.d(TAG, "uri:" + uri);
                    Log.d(TAG, "md5:" + md5);
                    showTip("请求成功");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            Log.d(TAG, "onResult");
            if (!"1".equalsIgnoreCase(keep_alive)) {
                setRadioEnable(true);
            }
            try {
                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("【RAW】 " + text);
                buffer.append("\n");
                buffer.append("【操作类型】" + object.optString("sst"));
                buffer.append("\n");
                buffer.append("【唤醒词id】" + object.optString("id"));
                buffer.append("\n");
                buffer.append("【唤醒词】" + object.optString("keyword"));
                buffer.append("\n");
                buffer.append("【得分】" + object.optString("score"));
                buffer.append("\n");
                buffer.append("【前端点】" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("【尾端点】" + object.optString("eos"));
                resultString = buffer.toString();
                //判断唤醒是否在监听，监听的话就停止监听
                if (mIvw.isListening()) {
                    mIvw.stopListening();
                    startIat(mContext);
                }

            } catch (JSONException e) {
                resultString = "结果解析出错";
                e.printStackTrace();
            }
            textView.setText(resultString);
            textView.append("\n \n 唤醒成功……\n \n");
            textView.append("\n 开始语音听写识别……");
            //Ωactivity.wakeUpAndUnlock();
        }

        @Override
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
            setRadioEnable(true);
        }

        @Override
        public void onBeginOfSpeech() {
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
            switch (eventType) {
                // EVENT_RECORD_DATA 事件仅在 NOTIFY_RECORD_DATA 参数值为 真 时返回
                case SpeechEvent.EVENT_RECORD_DATA:
                    final byte[] audio = obj.getByteArray(SpeechEvent.KEY_EVENT_RECORD_DATA);
                    Log.i(TAG, "ivw audio length: " + audio.length);
                    break;
            }
        }

        @Override
        public void onVolumeChanged(int volume) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy WakeDemo");
        // 销毁合成对象
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.destroy();
        }
    }

    private String getResource() {
        final String resPath = ResourceUtil.generateResourcePath(WakeDemo.this, RESOURCE_TYPE.assets, "ivw/" + getString(R.string.app_id) + ".jet");
        Log.d(TAG, "resPath: " + resPath);
        return resPath;
    }

    private void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

    private void setRadioEnable(final boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.ivw_net_mode).setEnabled(enabled);
                findViewById(R.id.btn_start).setEnabled(enabled);
                findViewById(R.id.seekBar_thresh).setEnabled(enabled);
            }
        });
    }

    /**
     * 启动语音听写
     */
    private void startIat(Context context) {
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(context, mInitListener);

        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(this, mInitListener);
        // 设置参数
        setParam();
        boolean isShowDialog = mSharedPreferences.getBoolean(getString(R.string.pref_key_iat_show), true);
        if (isShowDialog) {
            // 显示听写对话框
            mIatDialog.setListener(mRecognizerDialogListener);
            mIatDialog.show();
//            showTip(getString(R.string.text_begin));
            textView.append(getString(R.string.text_begin));
        } else {
            // 不显示听写对话框
            ret = mIat.startListening(mRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                showTip("听写失败,错误码：" + ret);
            } else {
                showTip(getString(R.string.text_begin));
            }
        }
    }


    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            if (mTranslateEnable && error.getErrorCode() == 14002) {
                showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
            } else {
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            if (mTranslateEnable) {
                printTransResult(results);
            } else {
                String text = JsonParser.parseIatResult(results.getResultString());
//                textView.append(text);
//                textView.setSelection(textView.length());
            }

            if (isLast) {
                //TODO 最后的结果
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };


    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, "recognizer result：" + results.getResultString());

            if (mTranslateEnable) {
                textView.append("\n 获得语音翻译结果：\n");
                printTransResult(results);
            } else {
                text = JsonParser.parseIatResult(results.getResultString());
                textView.append("\n 获得语音翻译结果：\n");
                textView.append("\n" + text);
//                mResultText.setSelection(mResultText.length());
            }
            //处理语音识别的结果
            if (!text.equals("。") && !text.equals("")) {
                //发送语音识别的结果到后台
                sendMessage(text);

            }
            //判断语音识别是否处于监听状态，处于监听状态则关闭
            if (mIat.isListening()) {
                mIat.stopListening();
            }
            //判断语音唤醒是否处于监听状态，不处于则开始监听
            if (!mIvw.isListening()) {
                mIvw.startListening(mWakeuperListener);
            }
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            if (mTranslateEnable && error.getErrorCode() == 14002) {
                showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
            } else {
                showTip(error.getPlainDescription(true));
            }
        }

    };

    public void gettelmessage() {
        ContentResolver contentResolver = this.getContentResolver();
        //获取总表的游标
        Cursor cursorContacts = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (cursorContacts.moveToNext()) {
            //总表ID
            int cursorid = cursorContacts.getInt(cursorContacts.getColumnIndex(ContactsContract.Contacts._ID));
            //找中间表,以总表ID为条件查询中间表
            Cursor cursorraw = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, null,
                    ContactsContract.RawContacts._ID + " = ?", new String[]{cursorid + ""}, null);
            //查询最后的表
            while (cursorraw.moveToNext()) {
                //中间表ID
                int rawid = cursorraw.getInt(cursorraw.getColumnIndex(ContactsContract.RawContacts._ID));
                info info = new info();
                //名字
                getname(contentResolver, rawid, info);
                //电话号码
                getnumber(contentResolver, rawid, info);
                mlist.add(info);
                Log.e("info---------------", info.toString());

            }
            cursorraw.close();

        }
        cursorContacts.close();
    }

    private void getnumber(ContentResolver contentResolver, int rawid, info info) {
        //查询电话
        Cursor querydatab = contentResolver.query(ContactsContract.Data.
                        CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " =? ",
                new String[]{rawid + "", ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}, null);
        while (querydatab.moveToNext()) {
            String number = querydatab.getString(querydatab.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            info.setNumber(number);
        }
        querydatab.close();
    }

    private void getname(ContentResolver contentResolver, int rawid, info info) {
        //查询内容表
        Cursor querydataa = contentResolver.query(ContactsContract.Data.
                        CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " =? ",
                new String[]{rawid + "", ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE}, null);
        while (querydataa.moveToNext()) {
            String name = querydataa.getString(querydataa.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
            info.setName(name);
        }
        querydataa.close();
    }

    //发送语音识别的文字
    private void sendMessage(final String text) {
        Logger.i(text);
        //创建retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                //使用自定义的mGsonConverterFactory
                .addConverterFactory(GsonConverterFactory.create())
                //.baseUrl("https://api.douban.com/v2/")
                //.baseUrl("http://172.27.12.226:8080")
                //.baseUrl("http://192.168.1.108:8080")
                .baseUrl("http://ics-backend.unicom.dev.wochanye.com")
                .build();
        // 实例化我们的mApi对象
        RetrofitApi mApi = retrofit.create(RetrofitApi.class);
        Call<SearchResult> baseRespCall = mApi.createTask(text, 1, 50);
        baseRespCall.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {

                SearchResult searchResult = response.body();
                SearchResult.Data data = searchResult.getData();
                List<DataList> list = data.getList();
                String result = searchResult.toString();
                //拨号
                if (result.contains("打电话")) {
                    gettelmessage();
                    /**
                     * 关键字“给”作为关键字
                     * */
                    int indexof = result.lastIndexOf("给");
                    String user = result.substring(indexof + 1);
                    for (int i = 0; i < mlist.size(); i++) {
                        info inf = mlist.get(i);
                        if (user.equals(inf.getName())) {
                            Intent inten = new Intent(Intent.ACTION_CALL);
                            inten.setData(Uri.parse("tel:" + inf.getNumber().trim()));
                            if (ActivityCompat.checkSelfPermission(WakeDemo.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                //return;
                                Toast.makeText(WakeDemo.this, "没有拨号权限" + result, Toast.LENGTH_SHORT).show();
                            }
                            WakeDemo.this.startActivity(inten);
                            return;
                        }
                    }
                    Toast.makeText(WakeDemo.this, "通讯录没有找到这个人" + result, Toast.LENGTH_SHORT).show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("result", result);
                    bundle.putString("voiceMessage", text);
                    Intent intent = new Intent(WakeDemo.this, BridgeWebViewActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                Logger.i("success");
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                Logger.i("failure");
            }
        });
    }

    private void printTransResult(RecognizerResult results) {
        String trans = JsonParser.parseTransResult(results.getResultString(), "dst");
        String oris = JsonParser.parseTransResult(results.getResultString(), "src");

        if (TextUtils.isEmpty(trans) || TextUtils.isEmpty(oris)) {
            showTip("解析结果失败，请确认是否已开通翻译功能。");
        } else {
            textView.append("原始语言:\n" + oris + "\n目标语言:\n" + trans);
        }

    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };


    /**
     * 参数设置
     *
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        String lag = mSharedPreferences.getString("iat_language_preference", "mandarin");
        // 设置引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        this.mTranslateEnable = mSharedPreferences.getBoolean(this.getString(R.string.pref_key_translate), false);
        if (mEngineType.equals(SpeechConstant.TYPE_LOCAL)) {
            // 设置本地识别资源
            mIat.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
        }
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD) && mTranslateEnable) {
            Log.i(TAG, "translate enable");
            mIat.setParameter(SpeechConstant.ASR_SCH, "1");
            mIat.setParameter(SpeechConstant.ADD_CAP, "translate");
            mIat.setParameter(SpeechConstant.TRS_SRC, "its");
        }
        //设置语言，目前离线听写仅支持中文
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
            mIat.setParameter(SpeechConstant.ACCENT, null);


            if (mEngineType.equals(SpeechConstant.TYPE_CLOUD) && mTranslateEnable) {
                mIat.setParameter(SpeechConstant.ORI_LANG, "en");
                mIat.setParameter(SpeechConstant.TRANS_LANG, "cn");
            }
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);

            if (mEngineType.equals(SpeechConstant.TYPE_CLOUD) && mTranslateEnable) {
                mIat.setParameter(SpeechConstant.ORI_LANG, "cn");
                mIat.setParameter(SpeechConstant.TRANS_LANG, "en");
            }
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        //识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "asr/common.jet"));
        tempBuffer.append(";");
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "asr/sms.jet"));
        //识别8k资源-使用8k的时候请解开注释
        return tempBuffer.toString();
    }

    @Override
    public void finish() {
        super.finish();//activity永远不会自动退出了，而是处于后台。
        moveTaskToBack(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //moveTaskToBack(true);
            goToIdle();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goToIdle() {

        ActivityInfo ai = homeInfo.activityInfo;
        Intent startIntent = new Intent(Intent.ACTION_MAIN);
        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        startIntent.setComponent(new ComponentName(ai.packageName,
                ai.name));
        startActivitySafely(startIntent);

    }

    void startActivitySafely(Intent intent) {

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "work wrongly", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, "notsecurity", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Launcher does not have the permission to launch "
                    + intent
                    + ".Make sure to create a MAIN intent-filter for the corresponding activity "
                    + "oruse the exported attribute for this activity.", e);
        }
    }
}