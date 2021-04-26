package com.agongym.store.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.agongym.store.activities.ProductDetailsActivity;
import com.agongym.store.adapters.ProductsAdapter;
import com.agongym.store.database.DataContract;
import com.example.agongym.R;

public class AccessoriesFragment extends Fragment {


    private Cursor cursor;
    private ProductsAdapter pa =null;
    Intent intent;
    int columnIndex;
    String productId;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_accessories, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridView accessoriesGV = getActivity().findViewById(R.id.accessoriesgridview);

        String[] selectionArgs = {"ACCESSORIES"};
        String selection = DataContract.ProductInternalClass.COLLECTION + " = ?";

        cursor =  getActivity().getContentResolver().query(DataContract.ProductInternalClass.buildProductUri(), DataContract.ProductInternalClass.ALL_FIELDS,selection,
                selectionArgs,null);



        pa = new ProductsAdapter(this.getContext(), cursor);

        accessoriesGV.setAdapter(pa);

        accessoriesGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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