package com.kaidongyuan.gxsd_android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;

import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.StandardFeature;
import io.dcloud.common.util.JSUtil;

import static android.content.Context.MODE_MULTI_PROCESS;

public class PGPlugintest extends StandardFeature {

    @SuppressLint("JavascriptInterface")
    public void PluginPrintIosFunction(IWebview pWebview, JSONArray array)  {

        String user_info = array.optString(1);
        String uss = array.optString(2);
        SharedPreferences p = getDPluginContext().getSharedPreferences("w_UserInfo", Context.MODE_MULTI_PROCESS);
        p.edit().putString("user_info", user_info).commit();
        Log.i("LM","获取用户信息");
    }

    @SuppressLint("JavascriptInterface")
    public void PluginGetUserFunction(IWebview pWebview, JSONArray array) {

        SharedPreferences p = getDPluginContext().getSharedPreferences("w_UserInfo", MODE_MULTI_PROCESS);
        String user_info = p.getString("user_info", "");
        JSONArray arr = new JSONArray();
        arr.put(user_info);
        String CallBackID = array.optString(0);
        JSUtil.execCallback(pWebview, CallBackID, arr, JSUtil.OK, false);
        Log.i("LM", "发送用户信息给vue");
    }
}
