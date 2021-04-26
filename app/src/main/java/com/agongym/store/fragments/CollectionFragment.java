package com.agongym.store.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.agongym.store.activities.ProductDetailsActivity;
import com.agongym.store.adapters.ProductsAdapter;
import com.agongym.store.database.DataContract;
import com.example.agongym.R;


public class CollectionFragment extends Fragment {

    private Cursor cursor;
    private ProductsAdapter pa =null;
    Intent intent;
    int columnIndex;
    String productId;

    public CollectionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_colletion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridView productGV = getActivity().findViewById(R.id.productsgridview);


        cursor =  getActivity().getContentResolver().query(DataContract.ProductInternalClass.buildProductUri(), DataContract.ProductInternalClass.ALL_FIELDS,null,
                null,null);



        pa = new ProductsAdapter(this.getContext(), cursor);

        productGV.setAdapter(pa);

        productGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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