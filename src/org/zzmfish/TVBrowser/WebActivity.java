package org.zzmfish.TVBrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ProgressBar;

public class WebActivity extends Activity
{
    MyWebView mWebView;
    ProgressBar mProgressBar;
    static WebActivity mInstance = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	Log.d("zhouzm", "WebActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mProgressBar = (ProgressBar) findViewById(R.id.webProgress);
        mInstance = this;
        onNewIntent(getIntent());
    }
    
	@Override
	protected void onDestroy() {
		Log.d("zhouzm", "WebActivity.onDestroy");
		// TODO Auto-generated method stub
		super.onDestroy();
		mInstance = null;
	}



	@Override
	protected void onNewIntent(Intent intent) {
		Log.d("zhouzm", "WebActivity.onNewIntent");
		Bundle extras = intent.getExtras();
        if (extras != null) {
        	String url = extras.getString("url");
        	loadUrl(url);
        	
        }
	}
	
	void loadUrl(String url) {
    	if (url == null)
        	url = "about:blank";
    	else if (url.indexOf("://") == -1)
    		url = "http://" + url;
    	if (mWebView == null) {
    		mWebView = (MyWebView)findViewById(R.id.webview);
            mWebView.init();
            mWebView.setWebChromeClient(new WebChromeClient() {
            	public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            		Log.d("MyWebView", message);
            	}
            	
    			@Override
    			public void onProgressChanged(WebView view, int newProgress) {
    				if (mProgressBar != null) {
    					mProgressBar.setProgress(newProgress);
    					if (newProgress == 100)
    						mProgressBar.setVisibility(View.GONE);
    					else
    						mProgressBar.setVisibility(View.VISIBLE);
    				}
    			}
            });
    	}
    	mWebView.loadUrl(url);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_home:
			Intent intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			return true;
		case R.id.menu_add_bookmark:
			Log.d("zhouzm", "add_to_home");
		    DialogFragment newFragment = new EditBookmarkDialog(mWebView.getTitle(), mWebView.getUrl());
		    newFragment.show(getFragmentManager(), "missiles");
			return true;
		case R.id.menu_zoom_in:
			mWebView.decreateZoomDensity();
			return true;
		case R.id.menu_zoom_out:
			mWebView.increateZoomDensity();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
		
	}
}

class EditBookmarkDialog extends DialogFragment {
	String mName;
	String mUrl;
    public EditBookmarkDialog(String name, String url) {
		super();
		mName = name;
		mUrl = url;
	}

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_bookmark, null);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
        		@Override
        		public void onClick(DialogInterface dialog, int id) {
        			Dialog myDialog = EditBookmarkDialog.this.getDialog();
        			String name = ((EditText) myDialog.findViewById(R.id.edit_bookmark_name)).getText().toString();
        			String url = ((EditText) myDialog.findViewById(R.id.edit_bookmark_url)).getText().toString();
        			//Log.d("zhouzm", "name=" + name + ", url=" + url);
        			Bookmarks.getInstance().add(name, url);
        			Bookmarks.getInstance().save(EditBookmarkDialog.this.getActivity().getApplicationContext());
        		}
        	});
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
        		@Override
        		public void onClick(DialogInterface dialog, int id) {
        		}
        	});
        EditText textName = (EditText) view.findViewById(R.id.edit_bookmark_name);
        EditText textUrl = (EditText) view.findViewById(R.id.edit_bookmark_url);
        textName.setText(mName);
        textUrl.setText(mUrl);
        Dialog dialog = builder.create();
        
        return dialog;
    }
}

