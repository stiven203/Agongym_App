package com.agongym.store.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.agongym.R;

public class MainFragment extends Fragment {

    ImageView men_main_butt;
    ImageView women_main_butt;
    ImageView acc_main_butt;

    //TEST
    //public static final int DISPLAY_SHOW_CUSTOM = 0x10;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ΛGONGYM");


        //Poner Custom toolbar
        //((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(R.layout.toolbar_title);
        //


        men_main_butt = (ImageView) root.findViewById(R.id.main_image_men);
        women_main_butt = (ImageView) root.findViewById(R.id.main_image_women);
        acc_main_butt = (ImageView) root.findViewById(R.id.main_image_acc);

        men_main_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_man);

            }
        });

        women_main_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_women);
            }
        });

        acc_main_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_accessories);
            }
        });






        return root;
    }





}
