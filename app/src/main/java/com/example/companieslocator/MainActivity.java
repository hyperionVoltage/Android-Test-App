package com.example.companieslocator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private SectionStatePagerAdapter sectionStatePagerAdapter;
    private NonSwipeableViewPager viewPager;
    MainFragment mainFragment;
    DisplayFragment displayFragment;

    public static Bitmap nulled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nulled = BitmapFactory.decodeResource(getResources(), R.drawable.common_google_signin_btn_icon_dark_focused);

        mainFragment = new MainFragment();
        sectionStatePagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.container);
        setupViewPager(viewPager);
    }

    private void setupViewPager(NonSwipeableViewPager viewPager) {
        SectionStatePagerAdapter adapter = new SectionStatePagerAdapter(getSupportFragmentManager());
        displayFragment = new DisplayFragment();
        adapter.addFragment(mainFragment, "Main");
        adapter.addFragment(new AddFragment(), "Add");
        adapter.addFragment(displayFragment, "Display");
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentIndex) {
        viewPager.setCurrentItem(fragmentIndex);
    }

    public void addCustomCompany(String name, String cityAndAddress, Bitmap bitmap) throws JSONException {
        mainFragment.addCustomCompany(name, cityAndAddress, bitmap);
        setViewPager(2);
    }

    public void displayCompany(String name, String cityAndAddress, Bitmap bitmap, LatLng latLng) {
        displayFragment.textView_display_name.setText(name);
        displayFragment.textView_display_city_and_address.setText(cityAndAddress);
        if (bitmap != null) {
            if (bitmap.getWidth() == 100 && bitmap.getHeight() == 100) {
                if (latLng.latitude != 0.00 && latLng.longitude != 0.00) {
                    String downloadURL = "http://maps.google.com/maps/api/staticmap?center=" + latLng.latitude + "," + latLng.longitude + "&zoom=15&size=1920x1080&sensor=false&key=AIzaSyDwvCTpGFc0w9dfDYUTgfo0ZTGPdaqreLk&markers=color:blue%7Clabel:" + name.charAt(0) + "%7C" + latLng.latitude + "," + latLng.longitude;
                    System.out.println("Attempting to download - " + downloadURL);
                    displayFragment.imageView_display.setImageBitmap(getBitmapFromURL(downloadURL));
                }
            } else {
                displayFragment.imageView_display.setImageBitmap(bitmap);
            }
        } else {
            Bitmap empty = BitmapFactory.decodeResource(getResources(), R.drawable.common_google_signin_btn_icon_dark_focused);
            displayFragment.imageView_display.setImageBitmap(empty);
        }
    }

    public static Bitmap getNulledBitmap() {
        return nulled;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
