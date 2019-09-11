package com.example.companieslocator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.model.LatLng;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    private FloatingActionButton fab_main, fab_add, fab_remove, fab_exit;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView textview_remove, textview_add, textview_exit;
    RecyclerView recyclerView_main;

    Boolean isOpen = false;

    int page = 0;
    int arrayLength = 0;
    boolean called = false;

    RecyclerViewAdapter adapter;

    public ArrayList<String> companyCustomNames = new ArrayList<>();
    public ArrayList<String> companyCustomCityAndAddress = new ArrayList<>();
    public ArrayList<Bitmap> companyCustomImages = new ArrayList<>();
    public ArrayList<LatLng> companyCustomLatLng = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        recyclerView_main = (RecyclerView) view.findViewById(R.id.recyclerView_main);
        fab_main = (FloatingActionButton) view.findViewById(R.id.fab_main);
        fab_add = (FloatingActionButton) view.findViewById(R.id.fab_add);
        fab_remove = (FloatingActionButton) view.findViewById(R.id.fab_remove);
        fab_exit = (FloatingActionButton) view.findViewById(R.id.fab_exit);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_anticlock);
        textview_add = (TextView) view.findViewById(R.id.textview_add);
        textview_remove = (TextView) view.findViewById(R.id.textview_remove);
        textview_exit = (TextView) view.findViewById(R.id.textview_exit);
        final SwipyRefreshLayout swipeRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.swipeLoader);

        if (!called) {
            try {
                setUpDefaultArrayList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            called = true;
        }

        try {
            initRecyclerView();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen) {
                    //textview_add.setVisibility(View.INVISIBLE);
                    //textview_remove.setVisibility(View.INVISIBLE);
                    //textview_exit.setVisibility(View.INVISIBLE);
                    fab_exit.startAnimation(fab_close);
                    fab_remove.startAnimation(fab_close);
                    fab_add.startAnimation(fab_close);
                    fab_main.startAnimation(fab_anticlock);
                    fab_exit.setClickable(false);
                    fab_remove.setClickable(false);
                    fab_add.setClickable(false);
                    isOpen = false;
                } else {
                    //textview_add.setVisibility(View.VISIBLE);
                    //textview_remove.setVisibility(View.VISIBLE);
                    //textview_exit.setVisibility(View.VISIBLE);
                    fab_exit.startAnimation(fab_open);
                    fab_remove.startAnimation(fab_open);
                    fab_add.startAnimation(fab_open);
                    fab_main.startAnimation(fab_clock);
                    fab_exit.setClickable(true);
                    fab_remove.setClickable(true);
                    fab_add.setClickable(true);
                    isOpen = true;
                }
            }
        });

        fab_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.exit_application_message).setTitle(R.string.exit_application_title)
                        .setPositiveButton(R.string.generic_accept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                                System.exit(0);
                            }
                        }).setNegativeButton(R.string.generic_decline, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        fab_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.delete_all_message).setTitle(R.string.delete_all_title)
                        .setPositiveButton(R.string.generic_accept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeAllCompanies();
                                page = 0;
                                try {
                                    initRecyclerView();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton(R.string.generic_decline, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).setViewPager(1);
                //textview_add.setVisibility(View.INVISIBLE);
                //textview_remove.setVisibility(View.INVISIBLE);
                //textview_exit.setVisibility(View.INVISIBLE);
                fab_exit.startAnimation(fab_close);
                fab_remove.startAnimation(fab_close);
                fab_add.startAnimation(fab_close);
                fab_main.startAnimation(fab_anticlock);
                fab_exit.setClickable(false);
                fab_remove.setClickable(false);
                fab_add.setClickable(false);
                isOpen = false;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    page++;
                    if (arrayLength % 5 == 0) {
                        if (page >= arrayLength / 5) {
                            page = 0;
                        }
                    }
                    if (arrayLength <= 5) {
                        page = 0;
                    }
                    if (arrayLength % 5 != 0) {
                        if (page >= (arrayLength / 5) + 1) {
                            page = 0;
                        }
                    }
                    try {
                        initRecyclerView();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    page--;
                    if (page < 0) {
                        if (arrayLength % 5 == 0) {
                            page = (arrayLength / 5) - 1;
                        }
                        if (arrayLength <= 5) {
                            page = 0;
                        }
                        if (arrayLength % 5 != 0) {
                            page = (arrayLength / 5);
                        }
                    }
                    try {
                        initRecyclerView();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    public JSONArray getCompaniesJSON() {
        String companiesAsString = getString(R.string.companiesAsString);
        try {
            JSONObject jsonCompanies = new JSONObject(companiesAsString);
            JSONArray companies = jsonCompanies.getJSONArray("companies");
            return companies;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("No json isn't loaded, Failed to read file");
        return null;
    }

    public void setUpDefaultArrayList() throws JSONException {
        for (int index = 0; index < this.getCompaniesJSON().length(); index++) {
            this.companyCustomNames.add(this.getCompaniesJSON().getJSONObject(index).getString("name"));
            this.companyCustomCityAndAddress.add(this.getCompaniesJSON().getJSONObject(index).getString("city") + ", " + this.getCompaniesJSON().getJSONObject(index).getString("address"));
            this.companyCustomLatLng.add(new LatLng((Double.parseDouble(this.getCompaniesJSON().getJSONObject(index).getString("latitude"))), (Double.parseDouble(this.getCompaniesJSON().getJSONObject(index).getString("longitude")))));
            String downloadURL = "http://maps.google.com/maps/api/staticmap?center=" + this.getCompaniesJSON().getJSONObject(index).getString("latitude") + "," + this.getCompaniesJSON().getJSONObject(index).getString("longitude") + "&zoom=15&size=100x100&sensor=false&key=AIzaSyDwvCTpGFc0w9dfDYUTgfo0ZTGPdaqreLk&markers=color:blue%7Clabel:" + this.getCompaniesJSON().getJSONObject(index).getString("name").charAt(0) + "%7C" + this.getCompaniesJSON().getJSONObject(index).getString("latitude") + "," + this.getCompaniesJSON().getJSONObject(index).getString("longitude");
            System.out.println("Attempting to download: " + downloadURL);
            this.companyCustomImages.add(getBitmapFromURL(downloadURL));
        }
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

    public void addCustomCompany(String name, String cityAndAddress, Bitmap bitmap) throws JSONException {
        companyCustomNames.add(name);
        companyCustomCityAndAddress.add(cityAndAddress);
        companyCustomImages.add(bitmap);
        companyCustomLatLng.add(new LatLng(0.000, 0.000));
        initRecyclerView();
    }

    public void initRecyclerView() throws JSONException {
        adapter = new RecyclerViewAdapter(getContext(), this);
        System.out.println("adapter = " + adapter);
        System.out.println("recyclerView_main = " + recyclerView_main);
        recyclerView_main.setAdapter(adapter);
        recyclerView_main.setLayoutManager(new LinearLayoutManager(getContext()));
        arrayLength = companyCustomNames.size();
    }

    public void removeCompany(int id) {
        companyCustomNames.remove(id);
        companyCustomImages.remove(id);
        companyCustomCityAndAddress.remove(id);
        companyCustomLatLng.remove(id);
    }

    public void removeAllCompanies() {
        companyCustomNames = new ArrayList<>();
        companyCustomImages = new ArrayList<>();
        companyCustomCityAndAddress = new ArrayList<>();
        companyCustomLatLng = new ArrayList<>();
    }

    public void initDisplayFragment(int id) {
        ((MainActivity)getActivity()).setViewPager(2);
        LatLng passed;
        if (id < companyCustomLatLng.size()) {
            passed = companyCustomLatLng.get(id);
        } else {
            passed = new LatLng(0.000,0.000);
        }
        ((MainActivity)getActivity()).displayCompany(companyCustomNames.get(id), companyCustomCityAndAddress.get(id), companyCustomImages.get(id), passed);
    }
}
