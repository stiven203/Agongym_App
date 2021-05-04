package com.agongym.store.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
//import androidx.appcompat.widget.SearchView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.agongym.store.activities.ProductDetailsActivity;
import com.agongym.store.adapters.ProductsAdapter;
import com.agongym.store.database.DataContract;
import com.example.agongym.R;

public class MenFragment extends Fragment{


    private Cursor cursor;
    GridView menGV;
    private ProductsAdapter pa =null;
    Intent intent;
    int columnIndex;
    String productId;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //manViewModel = new ViewModelProvider(this).get(ManViewModel.class);

        View root = inflater.inflate(R.layout.men_fragment, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ΛGONGYM");



        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        menGV = getActivity().findViewById(R.id.mengridview);

        String[] selectionArgs = {"MEN"};
        String selection = DataContract.ProductInternalClass.COLLECTION + " = ?";

        cursor =  getActivity().getContentResolver().query(DataContract.ProductInternalClass.buildProductUri(), DataContract.ProductInternalClass.ALL_FIELDS,selection,
                selectionArgs,null);



        pa = new ProductsAdapter(this.getContext(), cursor);



        menGV.setAdapter(pa);

        /////////////////////////////
        //test search view
        //mSearchView = (SearchView) getActivity().findViewById(R.id.search_product);
        menGV.setTextFilterEnabled(true);
        //setUpSearchView();
        //

        menGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                cursor.moveToPosition(position);
                columnIndex = cursor.getColumnIndexOrThrow(DataContract.ProductInternalClass.ID);
                productId = cursor.getString(columnIndex);

                intent = new Intent(getActivity().getApplicationContext(), ProductDetailsActivity.class);
                intent.putExtra("productId",productId);
                startActivity(intent);

            }
        });

    }



}