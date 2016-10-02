package com.ateam.funshoppers.Main_navigation;


import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ateam.funshoppers.R;

/**
 * Created by Ratan on 7/29/2015.
 */
public class Home_page extends Fragment {
    GPSTracker gps;

    private WebView wv1;
    LocalDatabase localDatabase;

    String urll = "http://suvojitkar365.esy.es/apptite/index.php?lat=";
    String url;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_page, container, false);
        localDatabase = new LocalDatabase(getActivity());


        gps = new GPSTracker(getActivity());
        // check if GPS enabled
        if(gps.canGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            // \n is for new line
            Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

            Contact contact = localDatabase.getLoggedInUser();
             url = urll + latitude+"&long="+longitude+"&phonenumber="+contact.username;
            Log.d("Mehra",url);

        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
        wv1=(WebView)v.findViewById(R.id.webView);
        wv1.setWebViewClient(new MyBrowser());
        WebSettings webSettings = wv1.getSettings();
        wv1.getSettings().setJavaScriptEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        wv1.getSettings().setPluginState(WebSettings.PluginState.ON);
        webSettings.setDomStorageEnabled(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.getSettings().setDomStorageEnabled(true);
        wv1.canGoBack();



        wv1.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    WebView webView = (WebView) v;

                    switch(keyCode)
                    {
                        case KeyEvent.KEYCODE_BACK:
                            if(webView.canGoBack())
                            {
                                webView.goBack();
                                return true;
                            }
                            break;
                    }
                }

                return false;
            }
        });


        //caching
        wv1.getSettings().setAppCacheMaxSize( 20 * 1024 * 1024 * 1024 ); // 5MB
        wv1.getSettings().setAppCachePath( getActivity().getApplicationContext().getCacheDir().getAbsolutePath() );
        wv1.getSettings().setAllowFileAccess( true );
        wv1.getSettings().setAppCacheEnabled( true );
        wv1.getSettings().setJavaScriptEnabled( true );
        wv1.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT ); // load online by default

        if ( !isNetworkAvailable() ) { // loading offline
            wv1.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }

        wv1.loadUrl(url);
        return v;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService( getActivity().CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }



}