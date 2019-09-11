package com.example.companieslocator;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toolbar;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddFragment extends Fragment {
    private static final String TAG = "AddFragment";

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;

    EditText editText_name, editText_city, editText_address;
    Button button_add, button_camera, button_gallery;
    ImageButton button_toolbar_back;
    ImageView imageView_camera;
    Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add, container, false);

        editText_name = view.findViewById(R.id.editText_name);
        editText_address = view.findViewById(R.id.editText_address);
        editText_city = view.findViewById(R.id.editText_city);

        button_add = view.findViewById(R.id.button_add);
        button_camera = view.findViewById(R.id.button_camera);

        imageView_camera = view.findViewById(R.id.imageView_camera);

        button_toolbar_back = view.findViewById(R.id.button_toolbar_back);
        button_toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMainScreen();
            }
        });

        button_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_name.getText().toString().length() == 0) {
                    editText_name.requestFocus();
                }
                if (editText_address.getText().toString().length() == 0) {
                    editText_address.requestFocus();
                }
                if (editText_city.getText().toString().length() == 0) {
                    editText_city.requestFocus();
                }
                if (editText_name.getText().toString().length() > 0 && editText_city.getText().toString().length() > 0 && editText_address.getText().toString().length() > 0) {
                    if (bitmap == null) {
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.common_google_signin_btn_icon_dark_focused);
                    }
                    try {
                        ((MainActivity)getActivity()).mainFragment.addCustomCompany(editText_name.getText().toString(), editText_city.getText().toString() + ", " + editText_address.getText().toString(), bitmap);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    returnToMainScreen();
                }
            }
        });

        button_gallery = view.findViewById(R.id.button_gallery);
        button_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1000);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap

                bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                imageView_camera.setImageBitmap(bitmap);
                button_camera.setClickable(false);
                button_camera.setVisibility(View.GONE);
                button_gallery.setClickable(false);
                button_gallery.setVisibility(View.GONE);
                }
            } else {
                if (requestCode == 1000) {
                    if (resultCode == Activity.RESULT_OK) {
                        Uri returnUri = data.getData();
                        Bitmap bitmapImage = null;
                        try {
                            bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                            bitmap = bitmapImage;
                            imageView_camera.setImageBitmap(bitmap);
                            button_camera.setClickable(false);
                            button_camera.setVisibility(View.GONE);
                            button_gallery.setClickable(false);
                            button_gallery.setVisibility(View.GONE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            }
        }
    }

    public void returnToMainScreen() {
        ((MainActivity)getActivity()).setViewPager(0);
        button_camera.setClickable(true);
        imageView_camera.setImageBitmap(null);
        button_gallery.setClickable(true);
        button_gallery.setVisibility(View.VISIBLE);
        button_camera.setVisibility(View.VISIBLE);
        editText_name.setText("");
        editText_city.setText("");
        editText_address.setText("");
        bitmap = null;
    }
}
