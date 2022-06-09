package com.project.lb3labs.ecashier;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class BrowserActivity extends AppCompatActivity {
    private WebView webView;
    private Intent intent;
    private String username, link;
    private int print, last_id;
    boolean loadingFinished = true;
    boolean redirect = false;
    private ProgressDialog pDialog;
    //private FloatingActionButton print_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        intent = getIntent();
        print = intent.getIntExtra("print",0);
        link = intent.getStringExtra("link");

        // toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Floating Acttion Button
        //print_button = findViewById(R.id.fab_print);

        webView = findViewById(R.id.activity_webview);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        // Tiga baris di bawah ini agar laman yang dimuat dapat
        // melakukan zoom.
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        // Baris di bawah untuk menambahkan scrollbar di dalam WebView-nya
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        if(print == 0){
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(link);
        } else {
            last_id = intent.getIntExtra("last_id",0);
            username = intent.getStringExtra("username");
            link = link+"?last_id="+last_id+"&username="+username;

            pDialog = new ProgressDialog(this);
            pDialog.setCancelable(false);
            pDialog.setMessage("Printing ...");
            showDialog();

            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                    if (!loadingFinished) {
                        redirect = true;
                    }

                    loadingFinished = false;
                    view.loadUrl(urlNewString);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                    loadingFinished = false;
                    //SHOW LOADING IF IT ISNT ALREADY VISIBLE
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if(!redirect){
                        loadingFinished = true;
                    }

                    if(loadingFinished && !redirect){
                        //HIDE LOADING IT HAS FINISHED
                        createWebPagePrint(webView);
                        hideDialog();
                    } else{
                        redirect = false;
                    }

                }
            });
            webView.loadUrl(link);

            /*
            print_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createWebPagePrint(webView);
                }
            });
            */
        }
    }

    public void createWebPagePrint(WebView webView) {
		/*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return;*/
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();
        String jobName = getString(R.string.app_name) + " Document";
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A5);
        assert printManager != null;
        PrintJob printJob = printManager.print(jobName, printAdapter, builder.build());

        if(printJob.isCompleted()){
            Log.d("Ngeprint", "slur");
            Toast.makeText(getApplicationContext(), R.string.print_complete, Toast.LENGTH_LONG).show();
        }
        else if(printJob.isFailed()){
            Toast.makeText(getApplicationContext(), R.string.print_failed, Toast.LENGTH_LONG).show();
        }
        // Save the job object for later status checking
    }

    // SHOWING ProgressDialog
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
