package slam.com.slamzoomimagelistview.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import slam.com.slamzoomimagelistview.R;

public class WebViewActivity extends AppCompatActivity {
    static private final String TAG = WebViewActivity.class.getSimpleName();

    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Intent intent = getIntent();
        String url = intent.getStringExtra("URL");

        bar = (ProgressBar) this.findViewById(R.id.progressBar);

        final EditText editUrl = this.findViewById(R.id.et_url);
        editUrl.setText(url);

         Button btnGo = this.findViewById(R.id.btn_go);
         btnGo.setOnClickListener(new View.OnClickListener() {
             public void onClick(View view) {
                 String url = editUrl.getText().toString();
                 showContentsInsideWebviewActivity(url);
             }
         });

        // new ProgressTask().execute();

//        showContentsInExternalWebBrowser(_url);
        showContentsInsideWebviewActivity(url);
    }

    protected void showContentsInsideWebviewActivity(String url) {
        if (url.length() < 8) {
            url = "http://" + url;
        }
        else {
            /*
             * Check to see if the url prefix is "http://" or "https://".
             * If not, then add the http protocol string to it.
             */
            String httpHeader = url.substring(0, 7);
            String httpsHeader = url.substring(0, 8);

            if (!httpHeader.equalsIgnoreCase("http://") && !httpsHeader.equalsIgnoreCase("https://")) {
                url = "http://" + url;
            }
        }

        WebView webView = (WebView) findViewById(R.id.webView);
        if (webView != null) {
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(url);
        }
    }

    protected void showContentsInExternalWebBrowser(String url) {

/* Does not work !
        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        this.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);

        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
*/
        // Remove the action bar (title bar)
         getSupportActionBar().hide();
         WebView webView = setupWebView();
         webView.loadUrl(url);
         setContentView(webView);
    }

    private WebView setupWebView() {
        WebView webView  = new WebView(this);

        // MUST to call setWebViewClient() for activity title bar to hide.
        // If not call, the back button will show a blank activity. whty???
        //
        webView.setWebViewClient(new WebViewClient());
        if (webView != null) {
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setDomStorageEnabled(true);

            webView.setWebViewClient(new WebViewClient() {
                // Deprecated---
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;   // Return 'false' to continue showing the response html.
                }

                /**
                 * @param view
                 * @param url
                 * @param favicon
                 */
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    String URL = url;
                }

                /**
                 * @param view
                 * @param url
                 */
                public void onPageFinished(WebView view, String url) {
                    String URL = url;
                }
            });
        }
        return webView;
    }

//    @Override
//    public void onBackPressed() {
//        finish();
//        super.onBackPressed();
//    }

    private class ProgressTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute(){
            bar.setVisibility(View.VISIBLE);
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            bar.setVisibility(View.GONE);
        }
    }
}
