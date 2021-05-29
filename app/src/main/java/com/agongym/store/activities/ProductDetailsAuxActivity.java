package com.agongym.store.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.agongym.store.database.DataContract;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.agongym.R;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsAuxActivity extends AppCompatActivity {

    String productId;
    Cursor cursor;

    Button plusButt;
    Button minusButt;
    Button addButt;
    TextView quantityTV;
    TextView descriptionTV;

    String variantId;
    String selection;
    int i;
    int columnIndex1;
    int columnIndex2;
    int columnIndex3;
    String cartSize;
    int cartQuantity=1;
    boolean variantState=true;
    String origin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_aux);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("ΛGONGYM");



        Log.d("ID que llega", getIntent().getExtras().getString("productId"));
        productId=getIntent().getExtras().getString("productId");


        String[] selectionArgs = {getIntent().getExtras().getString("productId")};



        //Query para descargar imagenes
        selection = DataContract.ImageInternalClass.PRODUCT_ID + " = ?";

        cursor = getContentResolver().query(DataContract.ImageInternalClass.buildImageUri(),DataContract.ImageInternalClass.ALL_FIELDS,selection,selectionArgs, null);
        String [] imagesSrc = new String[cursor.getCount()];
        Log.e("Numero de imagenes con este ID",""+cursor.getCount());

        i=0;
        columnIndex1 = cursor.getColumnIndexOrThrow(DataContract.ImageInternalClass.ORIGINAL_SRC);

        cursor.moveToFirst();

        //Recorremos el cursor hasta que no haya más registros
        do {

            imagesSrc[i] = cursor.getString(columnIndex1);
            Log.e("PRODUCTO AÑADIDO NUMERO", ""+i);
            i++;

        } while(cursor.moveToNext());


        for(int j=0;j<imagesSrc.length;j++){
            if(imagesSrc[j]==null){
                break;
            }
            Log.e("Product SRC", imagesSrc[j]);
        }
        //

        //IMAGES SLIDER
        ImageSlider imageSlider = findViewById(R.id.slider);
        List<SlideModel> slideModels = new ArrayList<>();

        for(int z=0;z<imagesSrc.length;z++){

            if(imagesSrc[z]!=null){
                slideModels.add(new SlideModel(imagesSrc[z]));
            }

        }

        imageSlider.setImageList(slideModels,true);
        //



        //Query para descargar descripción
        selection = DataContract.ProductInternalClass.ID + " = ?";

        cursor = getContentResolver().query(DataContract.ProductInternalClass.buildProductUri(),DataContract.ProductInternalClass.ALL_FIELDS,selection,selectionArgs, null);
        String  description = "";
        Log.e("Numero de descripciones con este ID",""+cursor.getCount());

        cursor.moveToFirst();
        columnIndex1 = cursor.getColumnIndexOrThrow(DataContract.ProductInternalClass.DESCRIPTION);
        if(cursor.getCount()!=0){
            description=cursor.getString(columnIndex1);
        }

        //Description TV
        descriptionTV = (TextView) findViewById(R.id.descriptionAuxTextView);
        descriptionTV.setText(description);
        //


    }

}