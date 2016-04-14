package com.ypf.okhttpclientutil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/4/14.
 */
public class OkHttpUtil {
    private static final int REQUEST_FAIL = 1001;
    private static final int REQUEST_SUCCESS = 1002;
    private static final String RESULT = "RESULT";
    private static OkHttpClient client = new OkHttpClient();
    private static final String TAG = "TAG";
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_SUCCESS:
                    String result = msg.getData().getCharSequence(RESULT).toString();
                    Log.d(TAG, "REQUEST_SUCCESS result = " + result);
                    break;
                case REQUEST_FAIL:
                    Log.d(TAG, "REQUEST_FAIL result = null");
                    break;
            }
        }
    };

    /**
     * get 请求
     *
     * @param url    请求的URL
     * @param isSync 是否是同步请求
     * @return 返回结果字符串
     */
    public static String get(String url, boolean isSync) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            if (isSync) {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    response.body().close();
                }
            } else {
                Call call = client.newCall(request);
                call.enqueue(getCallBack());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * post 请求
     *
     * @param url  请求url
     * @param json json字符串形式的参数
     * @return 返回结果字符串
     */
    public static String post(String url, String json, boolean isSync) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            if (isSync) {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    response.body().close();
                }
            } else {
                Call call = client.newCall(request);
                call.enqueue(getCallBack());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * post 请求
     *
     * @param url  请求url
     * @param para post参数，表单键值对
     * @return 返回结果字符串
     */
    public static String postOfFrom(String url, Map<String, String> para, boolean isSync) {
        if (para == null || para.size() == 0)
            return null;
        FormBody.Builder builder = new FormBody.Builder();

        for (Map.Entry<String, String> entry : para.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }

        RequestBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        try {
            if (isSync) {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    response.body().close();
                }
            } else {
                Call call = client.newCall(request);
                call.enqueue(getCallBack());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Callback getCallBack() {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = handler.obtainMessage(REQUEST_FAIL);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = handler.obtainMessage(REQUEST_SUCCESS);
                Bundle bundle = new Bundle();
                bundle.putCharSequence(RESULT, response.body().string());
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        };
    }
}

