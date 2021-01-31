package com.myapplications.cctask_app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.myapplications.cctask_app.ConfigFaribase.ConfiguracaoFaribase;
import com.myapplications.cctask_app.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PdfActivity extends AppCompatActivity {

    private PDFView pdfView;
    private static final int STATUS = 200;
    private File file;
    private StorageReference storage;
    private WebView webView;
    private Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        storage = ConfiguracaoFaribase.getStorage().child("PDFs");
        webView = findViewById(R.id.web_view);
        pdfView = findViewById(R.id.pdf);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("getdata")) {
                uri = Uri.parse(bundle.getString("getdata"));
                showPdfFromUri(uri);
            } else {
                File file = (File) bundle.getSerializable("getArquivo");
                showPdfFrom(file);
            }
        }
    }

    public void openDiretorio(Uri uri) {
        String pdfUrl = String.valueOf(uri); //your pdf url
        String url = "https://docs.google.com/viewerng/viewer?url=" + pdfUrl;

        WebSettings webSettings = webView.getSettings();
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setBuiltInZoomControls(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(pdfUrl);
    }

    private void showPdfFromUri(Uri uri) {
        pdfView.fromUri(uri)
                .defaultPage(0)
                .spacing(10)
                .enableDoubletap(true)
                .load();
        pdfView.setMinZoom(60);
        pdfView.setMidZoom(80);
        pdfView.setMaxZoom(100);
    }

    private void showPdfFrom(File teste) {


        pdfView.fromFile(teste)
                .defaultPage(0)
                .spacing(10)
                .enableDoubletap(true)
                .load();
        pdfView.setMinZoom(60);
        pdfView.setMidZoom(80);
        pdfView.setMaxZoom(100);
    }

    class PDFStream extends AsyncTask<String, Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;

            try {

                URL urlx = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) urlx.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
        }
    }

    private void retorna() {

        final long ONE_MEGABYTE = 1024 * 1024;
        storage.child("ola.pdf").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                pdfView.fromBytes(bytes).load();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PdfActivity.this,
                        "download unsuccessful", Toast.LENGTH_LONG).show();
            }
        });
    }
}
