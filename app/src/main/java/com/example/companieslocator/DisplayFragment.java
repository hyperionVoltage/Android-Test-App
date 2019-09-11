package com.example.companieslocator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DisplayFragment extends Fragment {
    private static final String TAG = "DisplayFragment";

    ImageButton button_toolbar_back;
    TextView textView_display_name, textView_display_city_and_address;
    ImageView imageView_display;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_display, container, false);

        button_toolbar_back = (ImageButton) view.findViewById(R.id.button_toolbar_display_back);

        button_toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setViewPager(0);
            }
        });

        textView_display_name = (TextView) view.findViewById(R.id.textView_display_name);
        textView_display_city_and_address = (TextView) view.findViewById(R.id.textView_display_city_and_address);

        imageView_display = (ImageView) view.findViewById(R.id.imageView_display);
        return view;
    }
}
