package cat.dam.andy.connexioweb;

import androidx.appcompat.app.AppCompatActivity;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    // member variables and objects
    private RelativeLayout layout;
    private EditText et_url;
    private Button btn_search;
    private ConnectionDetect connectionDetect;
    private WebView webView;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initListeners();
        initWebView();
    }

    private void initViews() {
        layout = (RelativeLayout) findViewById(R.id.rl_main);
        et_url = (EditText) findViewById(R.id.et_url);
        btn_search = (Button) findViewById(R.id.btn_search);
        webView =(WebView)findViewById(R.id.wv_page);
        connectionDetect = (ConnectionDetect) new ConnectionDetect(getApplicationContext());
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);
    }

    private void initListeners() {
        this.btn_search.setOnClickListener(v -> {
            getWeb(et_url.getText().toString());
        });
    }

    private void initWebView() {
        final WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); //activem Javascript
        webSettings.setBuiltInZoomControls(true); //per que puguem fer zoom amb gest
        webSettings.setLoadWithOverviewMode(true); //carrega la web en una vista completa
        webSettings.setUseWideViewPort(true);//mostra la vista com un navegador i escala per veure tot
        //webView.setWebChromeClient(new WebChromeClient());
        // ^^^ Utilitzarem .setWebChromeClient en lloc de WebViewClient si necessitem més control
        //per exemple percentatge de càrrega, diàleg Javascript, obtenció de la icona favicon...
        initWebClient();
    }
    
    private void initWebClient(){
        webView.setWebViewClient(new WebViewClient(){
            @Override
            //Forcem que quan es cliqui un enllaç dins la pàgina s'obri a la mateixa app
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                progressBar.setVisibility(View.VISIBLE);
                getWeb(request.getUrl().toString());
                return true;
            }
            @Override
            //controlem que no hi hagi error SSL perque no quedi una pàgina en blanc
            public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                           SslError error) {
                handler.proceed();
            }
            @Override
            //quan estigui completament carregada la pàgina treiem el diàleg de càrrega
            public void onPageFinished(WebView view, final String url) {
                progressBar.setVisibility(View.GONE);
//                To get user interaction back you just need to add the following code
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }


    private void getWeb(String url) {
        if (connectionDetect.haveConnection()) {
            //        To disable the user interaction you just need to add the following code
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (url.isEmpty()) {
                url="http://www.google.cat";
                et_url.setText(url);
            }
            else if (!url.startsWith("http://") &&
                    !url.startsWith("https://")) {
                url="http://" + url;
                et_url.setText(url);
            }
            progressBar.setVisibility(View.VISIBLE);
            webView.loadUrl(url);
        }
        else  {
            webView.loadUrl("about:blank");
            createToast(getResources().getString(R.string.dont_have_connection));
        }
    }

    private void createToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        // Prement botó de retrocés intentarà navegador vagi enrere en lloc de tancar
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
