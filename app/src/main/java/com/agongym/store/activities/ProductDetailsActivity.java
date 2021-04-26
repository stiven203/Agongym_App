package com.agongym.store.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.agongym.store.database.DataContract;
import com.agongym.store.database.models.CartModel;
import com.agongym.store.database.models.ProductModel;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.agongym.R;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    Cursor cursor;
    Spinner spinner;
    Button plusButt;
    Button minusButt;
    Button addButt;
    TextView quantityTV;
    TextView descriptionTV;

    String productId;
    String variantId;
    String selection;
    int i;
    int columnIndex1;
    int columnIndex2;
    int columnIndex3;
    String cartSize;
    int cartQuantity=1;
    boolean variantState=true;

    ContentValues cartContentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Log.e("ID que llega", getIntent().getExtras().getString("productId"));
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

        //Query para descargar variantes
        selection = DataContract.VariantInternalClass.PRODUCT_ID + " = ?";

        cursor = getContentResolver().query(DataContract.VariantInternalClass.buildVariantUri(),DataContract.VariantInternalClass.ALL_FIELDS,selection,selectionArgs, null);
        String [] variantSizes = new String[cursor.getCount()];
        String [] variantStates = new String[cursor.getCount()];
        String [] variantIds = new String[cursor.getCount()];


        Log.e("Numero de variantes con este ID",""+cursor.getCount());

        i=0;
        columnIndex1 = cursor.getColumnIndexOrThrow(DataContract.VariantInternalClass.TITLE);
        columnIndex2 = cursor.getColumnIndexOrThrow(DataContract.VariantInternalClass.AVAILABLE_FOR_SALE);
        columnIndex3 = cursor.getColumnIndexOrThrow(DataContract.VariantInternalClass.ID);


        cursor.moveToFirst();

        //Recorremos el cursor hasta que no haya más registros
        do {

            variantSizes[i] = cursor.getString(columnIndex1);
            variantStates[i] = cursor.getString(columnIndex2);
            variantIds[i] = cursor.getString(columnIndex3);
            Log.e("VARIANTE AÑADIDA NUMERO", ""+i);
            i++;

        } while(cursor.moveToNext());


        for(int j=0;j<variantSizes.length;j++){
            if(variantSizes[j]!=null){
                Log.e("Variant State", variantStates[j]);
            }

        }
        //



        //SPINNER
        spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,variantSizes);
        spinner.setAdapter(spAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("Variant Size", variantSizes[position]);
                Log.e("Variant State", variantStates[position]);
                cartSize = variantSizes[position];
                if(variantStates[position].equals("false")){
                    quantityTV.setText("SOLD OUT");
                    Log.e("Variant State", "SOLD OUT!!!!");
                    variantState=false;

                }
                else{

                    Log.e("Variant State", "BUY IT!!!!");
                    variantState=true;
                    quantityTV.setText("1");
                    variantId=variantIds[position];

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //TextView and Buttons
        minusButt = (Button) findViewById(R.id.buttonMinus);
        plusButt = (Button) findViewById(R.id.buttonPlus);
        quantityTV = (TextView) findViewById(R.id.quantityTextView);

        plusButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(variantState==true){
                    cartQuantity = cartQuantity+1;
                    quantityTV.setText(String.valueOf(cartQuantity));
                }

            }
        });

        minusButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cartQuantity>=2){
                    cartQuantity = cartQuantity-1;
                }
                if(variantState==true){
                    quantityTV.setText(String.valueOf(cartQuantity));
                }


            }
        });
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

        //Description Button
        descriptionTV = (TextView) findViewById(R.id.descriptionTextView);
        descriptionTV.setText(description);
        //

        //AddToCart Button
        addButt = (Button)findViewById(R.id.buttonAddToCart);

        addButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(variantState==true){
                    insertDataBase(productId, variantId, String.valueOf(cartQuantity));
                }
            }
        });






    }

    private void insertDataBase(String productId, String variantId, String quantity) {

        Log.e("El valor de productId es",productId);
        Log.e("El valor de variantId es",variantId);
        Log.e("El valor de quantity es",quantity);

        String variantPrice="";

        //Consultamos precio de la variante
        String[] selectionArgs = {variantId};
        String selection = DataContract.VariantInternalClass.ID + " = ?";

        Cursor auxCursor = getContentResolver().query(DataContract.VariantInternalClass.buildVariantUri(),DataContract.VariantInternalClass.ALL_FIELDS,selection,selectionArgs, null);

        Log.e("COUNT CURSOR",""+auxCursor.getCount());

        auxCursor.moveToFirst();

        int  columnIndex = auxCursor.getColumnIndexOrThrow(DataContract.VariantInternalClass.PRICE);
        variantPrice= auxCursor.getString(columnIndex);


        //

        cartContentValues = new ContentValues();
        Log.e("PRODUCTO INSERTADO PRODUCT ID",productId);
        Log.e("PRODUCTO INSERTADO VARIANT ID",variantId);
        Log.e("PRODUCTO INSERTADO QUANTITY",quantity);
        Log.e("PRODUCTO INSERTADO VARIANT PRICE",variantPrice);

        CartModel cartModel = new CartModel(productId,variantId,quantity,variantPrice);

        cartContentValues = DataContract.CartInternalClass.CartToContentValues(cartModel);
        Uri x =getContentResolver().insert(DataContract.CartInternalClass.buildCartUri(),cartContentValues);


        //COMPROBACION INSERCCIÓN (PROVISIONAL)
        cursor = getContentResolver().query(DataContract.CartInternalClass.buildCartUri(),DataContract.CartInternalClass.ALL_FIELDS,null,null, null);
        Log.e("El numero de productos añadidos al carrito hasta ahora son",""+cursor.getCount());


    }
}