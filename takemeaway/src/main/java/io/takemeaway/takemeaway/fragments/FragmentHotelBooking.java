package io.takemeaway.takemeaway.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import androidx.fragment.app.Fragment;
import io.takemeaway.takemeaway.R;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class FragmentHotelBooking extends Fragment implements View.OnClickListener {

    private static final String TAG = FragmentHotelBooking.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View view = inflater.inflate(R.layout.fragment_flight_hotel_booking, parent, false);

        Bundle bundle = getArguments();
        String uri = bundle.getString("hotelLink");

        WebView webView = view.findViewById(R.id.webViewHolder);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.loadUrl(uri);

        return view;


    }

    @Override
    public void onClick(View view) {

    }
}