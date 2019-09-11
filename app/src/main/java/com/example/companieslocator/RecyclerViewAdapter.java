package com.example.companieslocator;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> companyNames = new ArrayList<>();
    private ArrayList<String> companyCityAndAddress = new ArrayList<>();
    private ArrayList<Bitmap> companyImages = new ArrayList<>();
    private ArrayList<LatLng> companyLatlng = new ArrayList<>();
    private Context context;

    int page = 0;
    MainFragment mainFragment;

    public RecyclerViewAdapter(Context context, MainFragment mainFragment) throws JSONException {
        this.page = mainFragment.page;
        for (int index = page * 5; index < (page * 5) + 5; index++) {
            if (index > mainFragment.companyCustomNames.size() - 1) {
                break;
            }
            this.companyNames.add(mainFragment.companyCustomNames.get(index));
            this.companyCityAndAddress.add(mainFragment.companyCustomCityAndAddress.get(index));
            this.companyImages.add(mainFragment.companyCustomImages.get(index));
            this.companyLatlng.add(mainFragment.companyCustomLatLng.get(index));
        }
        this.mainFragment = mainFragment;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_layout, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Log.d(TAG, "onBindViewHolder: called index - " + i);

        viewHolder.companyName.setText(companyNames.get(i));
        viewHolder.companyCityAndAddress.setText(companyCityAndAddress.get(i));
        Glide.with(context).asBitmap().load(companyImages.get(i)).into(viewHolder.companyImage);

        viewHolder.removeButton.setId(i);
        viewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedId = (v.getId() + (page * 5));
                mainFragment.removeCompany(clickedId);
                try {
                    mainFragment.initRecyclerView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        viewHolder.parentLayout.setId(i);
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedId = (v.getId() + (page * 5));
                mainFragment.initDisplayFragment(clickedId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return companyNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView companyImage;
        TextView companyName;
        TextView companyCityAndAddress;
        RelativeLayout parentLayout;
        Button removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companyImage = (ImageView) itemView.findViewById(R.id.image);
            companyName = (TextView) itemView.findViewById(R.id.textView_companyName);
            companyCityAndAddress = (TextView) itemView.findViewById(R.id.textView_companyCityAddress);
            parentLayout = (RelativeLayout) itemView.findViewById(R.id.parent_layout);
            removeButton = (Button) itemView.findViewById(R.id.button_removeButton);
        }
    }
}