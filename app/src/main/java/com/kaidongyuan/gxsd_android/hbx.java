package com.kaidongyuan.gxsd_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import io.dcloud.EntryProxy;
import io.dcloud.common.DHInterface.ICore;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.IWebviewStateListener;
import io.dcloud.feature.internal.sdk.SDK;

public class hbx extends Activity {

    public EntryProxy mEntryProxy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(mEntryProxy == null){
            FrameLayout rootView = new FrameLayout(this);
            // 创建5+内核运行事件监听
            WebviewModeListener wm = new WebviewModeListener(this,rootView);
            // 初始化5+内核
            mEntryProxy = EntryProxy.init(this,wm);
            // 启动5+内核，并指定内核启动类型
            mEntryProxy.onCreate(savedInstanceState,SDK.IntegratedMode.WEBVIEW,null);
            setContentView(rootView);
        }
    }

    class WebviewModeListener implements ICore.ICoreStatusListener {
        IWebview webview = null;
        LinearLayout btns = null;
        Activity activity = null;
        ViewGroup mRootView = null;

        public WebviewModeListener(Activity activity, ViewGroup rootView) {
            this.activity = activity;
            mRootView = rootView;
            btns = new LinearLayout(activity);
            mRootView.setBackgroundColor(0xffffffff);
            mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    webview.onRootViewGlobalLayout(mRootView);
                }
            });
        }

        @Override
        public void onCoreInitEnd(ICore coreHandler) {
            //设置单页面集成的appid
            String appid = "test1";
            // 单页面集成时要加载页面的路径，可以是本地文件路径也可以是网络路径
            String url = "file:///android_asset/apps/H5A4057B2/www/index.html";
            webview = SDK.createWebview(activity, url, appid, new IWebviewStateListener() {
                @Override
                public Object onCallBack(int pType, Object pArgs) {
                    switch (pType) {
                        case IWebviewStateListener.ON_WEBVIEW_READY:
                            // 准备完毕之后添加webview到显示父View中，设置排版不显示状态，避免显示webview时，html内容排版错乱问题
                            ((IWebview) pArgs).obtainFrameView().obtainMainView().setVisibility(View.INVISIBLE);
                            SDK.attach(mRootView, ((IWebview) pArgs));
                            break;
                        case IWebviewStateListener.ON_PAGE_STARTED:

                            break;
                        case IWebviewStateListener.ON_PROGRESS_CHANGED:

                            break;
                        case IWebviewStateListener.ON_PAGE_FINISHED:
                            // 页面加载完毕，设置显示webview
                            webview.obtainFrameView().obtainMainView().setVisibility(View.VISIBLE);
                            break;
                    }
                    return null;
                }
            });

            final WebView webviewInstance = webview.obtainWebview();
            // 监听返回键
            webviewInstance.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (webviewInstance.canGoBack()) {
                            webviewInstance.goBack();
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        @Override
        public void onCoreReady(ICore coreHandler) {
            try {
                SDK.initSDK(coreHandler);
                SDK.requestAllFeature();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onCoreStop() {
            // TODO Auto-generated method stub
            return false;
        }
    }
}
