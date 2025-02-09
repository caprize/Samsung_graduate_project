package com.example.samsung_project;



import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.samsung_project.Navigation.NavigationHost;
import com.example.samsung_project.Navigation.NavigationIconClickListener;
import com.example.samsung_project.image.ImagePostReq;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


import java.io.IOException;

import okhttp3.MediaType;

import static android.app.Activity.RESULT_OK;

public class FoodGridFragment extends Fragment {
    static final int GALLERY_REQUEST = 1;
    ProgressBar progressBar;
    View view;
    static Bitmap bitmap=null;
    public String res = null;
    public static final MediaType String = MediaType.get("application/string; charset=utf-8");
    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                getContext(),
                view.findViewById(R.id.product_grid),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.shr_branded_menu), // Menu open icon
                getContext().getResources().getDrawable(R.drawable.shr_close_menu))); // Menu close icon
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        view = inflater.inflate(R.layout.food_grid_fragment, container, false);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Справка")
                .setMessage("Нажмите кнопку Image для того, чтобы прикрепить вашу фотографию")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Закрываем окно
                        dialog.cancel();
                    }
                });
        builder.show();

        // Set up the toolbar
        setUpToolbar(view);
        View view1 = view;


        MaterialButton imageButton = view.findViewById(R.id.image_button);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar = (ProgressBar) view1.findViewById(R.id.progress_bar);
                progressBar.setVisibility(ProgressBar.VISIBLE);
                Intent in = new   Intent(Intent.ACTION_GET_CONTENT,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                in.setType("image/*");
                startActivityForResult(in, GALLERY_REQUEST);

            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Context context = getContext();
        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);
                        onDestroy();
                        getChoices();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
    public void getChoices(){

        String url = "10.0.2.2";

        ImagePostReq image = new ImagePostReq();
        byte[] req = image.PrepImage(bitmap);
        String[] response = {null};
        Runnable task = () -> {
            try {
                response[0] = image.post1(url,req);
                res = response[0];
                ((NavigationHost) getActivity()).navigateTo(new FoodChoice(res), true);
                //progressBar.setVisibility(ProgressBar.INVISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }
}

