package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.example.mbw.R;


import android.content.Context;

import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PrintActivity extends AppCompatActivity {

/*    Button printBT;

    private WebView mWebView;*/


    private Button scanBtn;
    private TextView formatTxt, contentTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);




        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient());

        myWebView.addJavascriptInterface(new JavaScriptInterface(this), "fonix");
        WebSettings webSettings = myWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("http://192.168.1.126/webTest/");



    }



    public String scanContent;
    public String scanFormat;

    public String getContent(){ return scanContent; }

    public class JavaScriptInterface {
        Context mContext;

        /* Instantiate the interface and set the context */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        /* Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public String getFromAndroid() {
            return "This is from android.";
        }

        @JavascriptInterface
        public void scanJS() {

        }

        @JavascriptInterface
        public String scanResult() {
            return getContent();
        }
    }
}

       /* mWebView = (WebView) findViewById(R.id.webview);
        printBT = findViewById(R.id.printBT);
        printBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print();
            }
        });

        print();
        }
        */




/*
    private void print() {

        // Create a WebView and hold on to it as the printing will start when
        // load completes and we do not want the WbeView to be garbage collected.
        // Important: Only after the page is loaded we will do the print.
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                doPrint();
            }
        });

        String htmlDocument = "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table, th, td {\n" +
                "  border: 1px solid black;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h2>Table With Border</h2>\n" +
                "\n" +
                "<p>Use the CSS border property to add a border to the table.</p>\n" +
                "\n" +
                "<table style=\"width:100%\">\n" +
                "  <tr>\n" +
                "    <th>Firstname</th>\n" +
                "    <th>Lastname</th> \n" +
                "    <th>Age</th>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>Jill</td>\n" +
                "    <td>Smith</td>\n" +
                "    <td>50</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>Eve</td>\n" +
                "    <td>Jackson</td>\n" +
                "    <td>94</td>\n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td>John</td>\n" +
                "    <td>Doe</td>\n" +
                "    <td>80</td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "\n" +
                "developed by multibrand</body>\n" +
                "</html>";

        // Load an HTML page.

        mWebView  .loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);
      //  mWebView.loadUrl("file:///android_res/raw/motogp_stats.html");
    }

    private void doPrint() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            // Get the print manager.
            PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

            // Create a wrapper PrintDocumentAdapter to clean up when done.
            PrintDocumentAdapter adapter = new PrintDocumentAdapter() {
                private final PrintDocumentAdapter mWrappedInstance = mWebView.createPrintDocumentAdapter();

                @Override
                public void onStart() {
                    mWrappedInstance.onStart();
                }

                @Override
                public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                                     CancellationSignal cancellationSignal, LayoutResultCallback callback,
                                     Bundle extras) {
                    mWrappedInstance.onLayout(oldAttributes, newAttributes, cancellationSignal,
                            callback, extras);
                }

                @Override
                public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                                    CancellationSignal cancellationSignal, PrintDocumentAdapter.WriteResultCallback callback) {
                    mWrappedInstance.onWrite(pages, destination, cancellationSignal, callback);
                }

                @Override
                public void onFinish() {
                    mWrappedInstance.onFinish();
                    // Intercept the finish call to know when printing is done
                    // and destroy the WebView as it is expensive to keep around.
                    mWebView.destroy();
                    mWebView = null;
                }
            };

            // Pass in the ViewView's document adapter.
            printManager.print("MotoGP stats", adapter, null);
        }
    }
}

*/




