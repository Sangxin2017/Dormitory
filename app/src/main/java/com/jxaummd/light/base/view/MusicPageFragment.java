package com.jxaummd.light.base.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;

import com.jxaummd.light.R;
import com.jxaummd.light.base.toolview.ProgressWebView;

/**
 * Created by sangx on 2018/2/21.
 */

public class MusicPageFragment extends Fragment {
    private ProgressWebView progressWebView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_layout,container,false);
        progressWebView=view.findViewById(R.id.music_webview);
        progressWebView.getSettings().setJavaScriptEnabled(true);
        progressWebView.setWebViewClient(new WebViewClient());
        progressWebView.loadUrl("http://m.kugou.com/");
        return view;
    }
}
