package com.agongym.store.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.agongym.store.activities.HomeActivity;
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

    SearchView mSearchView;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View root = inflater.inflate(R.layout.fragment_men, container, false);

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


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_action_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem menuItem = menu.findItem(R.id.search_product);

        mSearchView=(SearchView) menuItem.getActionView();
        Log.e("INFO"," LLEGA AQUI");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getActivity().getApplicationContext(),"Se ha escrito: "+query, Toast.LENGTH_LONG).show();
                Log.e("INFO","SALTA EL METODO");

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(getActivity().getApplicationContext(),"Se ha escrito: "+newText, Toast.LENGTH_LONG).show();
                Log.e("INFO","SALTA EL METODO");
                return false;
            }
        });




    }
}