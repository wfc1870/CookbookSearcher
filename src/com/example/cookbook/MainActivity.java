package com.example.cookbook;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.cookbook.GetCookbookData.Cookbook;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {

	private WebView webview;
	private final String url = "file:///android_asset/index.html"; 
	ArrayList<Cookbook> listCookbook =  new ArrayList<Cookbook>();
	//创建Handler对象
	Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        Bundle data = msg.getData();
	        String val = data.getString("value");
	        Log.i("mylog","请求结果-->" + val);
	    }
	};
	//新建一个线程对象
	Runnable runnable = new Runnable() {
	    @Override
	    public void run() {
//	    	GetCookbookData getCookbookData = new GetCookbookData();
//	    	listCookbook = getCookbookData.run("红烧肉");
	        Message msg = new Message();
	        Bundle data = new Bundle();
	        data.putString("value", "请求结果");
	        msg.setData(data);
	        handler.sendMessage(msg);
	    }
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		webview = (WebView) findViewById(R.id.webView);
		
		//在Runnable中做HTTP请求，以防阻塞UI线程抛NetworkOnMainThreadException
//		new Thread(runnable).start();
		
		//设置webview的参数和加载本地页面
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);	//可使滚动条不占位
		webview.getSettings().setBuiltInZoomControls(false);	//隐藏左下角缩放按钮
		webview.getSettings().setSupportZoom(false);	//不允许html缩放
		webview.getSettings().setJavaScriptEnabled(true);	//必须！使webview中的html支持javascript，能够与安卓进行交互
		webview.getSettings().setUseWideViewPort(true);	//使自适应分辨率
		webview.getSettings().setLoadWithOverviewMode(true);	//使自适应分辨率
		webview.setWebViewClient(new webViewClient()); ////为WebView设置WebViewClient处理某些操作	
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);// 禁止由于内容过宽，导致横屏滚动。
		webview.addJavascriptInterface(this, "android");	//注意！使用这条语句，需在本类或onCreate方法添加注解@SuppressLint("JavascriptInterface")，和导入android.annotation.SuppressLint包，不然会报错。并且要用@JavascriptInterface注解的公有方法才能在webview中被调用
		webview.loadUrl(url);	//加载当前项目的assets目录下的welcome.html文件到webview
		
	}
	
	/**
	 * 被javaScript调用的方法，主要将搜索关键词传到下一个Activity
	 * @param some
	 */
	@JavascriptInterface
	public void callDetailActivity(String keyword) {
		//输入合法性检查
		if(keyword.equals("")) {
			Toast.makeText(getApplicationContext(), "您未输入关键词！", Toast.LENGTH_SHORT).show();
		} else {
			keyword = keyword.trim();//去掉字符串首尾的空格  
			Intent intent = new Intent(MainActivity.this, DetailActivity.class);
			intent.putExtra("keyword", keyword);
			startActivity(intent);
		}
	}
	
	/**
	 * 关键就是为WebView设置WebViewClient，然后重写shouldOverrideUrlLoading方法即可。其中WebViewClient为WebView的一个辅助类，主要处理各种通知、请求事件。
	 * @author Dr.Chan
	 *
	 */
	 class webViewClient extends WebViewClient{ 
		 	/**
		 	 * 重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。 
		 	 */
		 	@Override 
		    public boolean shouldOverrideUrlLoading(WebView view, String url) { 
		        view.loadUrl(url); 
		        //如果不需要其他对点击链接事件的处理返回true，否则返回false 
		        return true; 
		    }
		 	
		 	/**
		 	 * 页面载入完成后调用
		 	 */
		 	@Override
		 	public void onPageFinished(WebView view, String url) {
		 		super.onPageFinished(view, url);
//		 		webview.loadUrl("javascript:changeH1Value('" + getRomTotalSize() + "')");  
		 	}
	 }

}
