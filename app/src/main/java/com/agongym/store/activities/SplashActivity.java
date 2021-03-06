package com.agongym.store.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import android.content.ContentValues;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.agongym.store.utils.Apollo;
import com.agongym.store.database.DataContract;
import com.agongym.store.database.models.ImageModel;
import com.agongym.store.database.models.ProductModel;
import com.agongym.store.database.models.VariantModel;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.exception.ApolloException;
import com.example.agongym.R;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_LENGTH = 2500;
    private Cursor cursor;

    Intent mainIntent;

    boolean showLogin = true;

    int maxTimes = 5;

    int expiresAtNumber =0;
    int currentTimeNumber =0;

    //animations
    Animation topAnim, bottomAnim;
    ImageView logo;
    TextView logoText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        logo = (ImageView) findViewById(R.id.splash_logo);
        logoText = (TextView) findViewById(R.id.splash_text);

        logo.setAnimation(topAnim);
        logoText.setAnimation(bottomAnim);


        downloadProducts();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                //COMPROBACION LOG IN O NO
                Cursor cursor = getContentResolver().query(DataContract.CustomerInternalClass.buildCartUri(),DataContract.CustomerInternalClass.ALL_FIELDS,null,null, null);
                Log.e("El numero usuarios en BBDD son",""+cursor.getCount());

                if(cursor.getCount()==0){
                    showLogin = loadPreferences();
                    if(showLogin==true){
                        mainIntent = new Intent(SplashActivity.this, LogInActivity.class);
                        //Log.e("LOGGED (SI/NO)","NO");
                    }
                    else {
                        mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
                    }

                }
                else{
                    cursor.moveToFirst();
                    int columnIndex1 = cursor.getColumnIndexOrThrow(DataContract.CustomerInternalClass.ACCESS_TOKEN_EXPIRES_AT);
                    int columnIndex2 = cursor.getColumnIndexOrThrow(DataContract.CustomerInternalClass.ACCESS_TOKEN);

                    String accessTokenExpiresAt = cursor.getString(columnIndex1);
                    String accessToken = cursor.getString(columnIndex2);

                    Log.e("Caduca Token en Splash Activity",accessTokenExpiresAt);



                    //Comparar hora actual con fecha caducidad token
                    //pruebas
                    Log.e("test a??o",accessTokenExpiresAt.substring(0,4));
                    Log.e("test mes",accessTokenExpiresAt.substring(5,7));
                    Log.e("test seg",accessTokenExpiresAt.substring(17,19));
                    //
                    expiresAtNumber = Integer.parseInt(accessTokenExpiresAt.substring(0,4))*100000+  //a??o
                                            +Integer.parseInt(accessTokenExpiresAt.substring(5,7))*10000+  //mes
                                            Integer.parseInt(accessTokenExpiresAt.substring(8,10))*1000+  //dia
                                            Integer.parseInt(accessTokenExpiresAt.substring(11,13))*100+ //hora
                                            Integer.parseInt(accessTokenExpiresAt.substring(14,16))*10+ //min
                                              Integer.parseInt(accessTokenExpiresAt.substring(17,19)); //seg

                    currentTimeNumber = getCurrentTimeNumber();

                    Log.e("valor token: ",String.valueOf(expiresAtNumber));
                    Log.e("valor current: ",String.valueOf(currentTimeNumber));

                    if(expiresAtNumber>currentTimeNumber){
                        mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
                        Log.e("LOGGED (SI/NO)","SI");
                        Log.e("TOKEN IS",accessToken);

                    }

                    else{
                        mainIntent = new Intent(SplashActivity.this, LogInActivity.class);
                        Log.e("LOGGED (SI/NO)","NO");
                        //Aqui borrar BBDD Customer
                    }


                }
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();


            }
        }, SPLASH_LENGTH);


    }

    private boolean loadPreferences() {

            boolean showLogin = false;

            SharedPreferences preferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
            String firstSession = preferences.getString("firstTime","null");
            int numberTimes = 0;
            int auxNumber=0;



            if(firstSession.equals("null")){
                Log.e("SHARED PREFERENCES","Primera sesi??n");
                //Toast.makeText(this, "SHARED PREFERENCES VACIO", Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("firstTime","no");
                editor.commit();
                showLogin=true;
            }

            else{
                String value = preferences.getString("numberTimes","null");

                Log.e("SHARED PREFERENCES","Valor de Value: "+value);

                if(!value.equals("null")){
                    Log.e("SHARED PREFERENCES","Entra aqui 1");



                    //numberTimes = Integer.getInteger(value);
                    numberTimes = Integer.parseInt(value);

                    if (numberTimes<=maxTimes){
                        Log.e("SHARED PREFERENCES","Entra aqui 2");
                        showLogin=false;
                        numberTimes = numberTimes + 1;

                    }
                    else {
                        Log.e("SHARED PREFERENCES","Entra aqui 3");
                        showLogin=true;
                        numberTimes = 0;

                    }
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("numberTimes",String.valueOf(numberTimes));
                    editor.commit();

                }
                else {
                    Log.e("SHARED PREFERENCES","Primera vez");
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("numberTimes","1");
                    editor.commit();
                    showLogin=true;
                }


            }



            return showLogin;
    }

    protected void onStart() {
        super.onStart();
        //descargar datos e insertar en BBDD (o actualizar)

        //metodo para descargar los datos
        //downloadProducts();

    }

    private void downloadProducts() {

        //APOLLO
        Apollo apolloObject = new Apollo();
        ApolloClient apolloClient = apolloObject.getApolloClient();
        //


        // Then enqueue your query

        apolloClient.query(new ProductQuery())
                .enqueue(new ApolloCall.Callback<ProductQuery.Data>() {


                    @Override
                    public void onResponse(@NotNull com.apollographql.apollo.api.Response<ProductQuery.Data> response) {
                        Log.e("Apollo", "Launch site: " + response.getData().toString());
                        Log.e("Apollo", "Size of Edges: " + response.getData().products().edges().size());
                        ProductQuery.Products products = response.getData().products;

                        for(int i=0;i<response.getData().products().edges().size();i++){
                            Log.e("PRODUCTO",""+response.getData().products().edges().get(i).node().title()+" : DISPONIBLE: "
                                    + String.valueOf(products.edges.get(i).node.availableForSale));
                            if(response.getData().products.edges.get(i).node.collections.edges.size()!=0){
                                Log.e("COLECCI??N",response.getData().products.edges.get(i).node.collections.edges.get(0).node.title);
                            }
                            if(response.getData().products.edges.get(i).node.variants.edges().size()==0){
                                //Log.e("MENSAJE: ","Este producto no tiene variantes");
                            }
                            else{

                                for(int z=0;z<response.getData().products().edges().get(i).node.variants.edges.size();z++) {
                                    //Log.e("NOMBRE VARIANTE",response.getData().products.edges.get(i).node().variants.edges.get(z).node.title);
                                    if(response.getData().products.edges.get(i).node().variants.edges.get(z).node.compareAtPriceV2==null){
                                       // Log.e("MENSAJE","Esta variante no tiene precio de comparaci??n");
                                    }
                                    else{
                                      //  Log.e("PRECIO VARIANT ",z+": "+response.getData().products().edges().get(i).node().variants.edges.get(z).node.priceV2.amount.toString());
                                    }


                                }
                            }



                        }

                        String order =null;
                        cursor = getContentResolver().query(DataContract.ProductInternalClass.buildProductUri(), DataContract.ProductInternalClass.ALL_FIELDS,null,null, null);
                        Log.e("PRODUCTOS EN BBDD", Integer.toString(cursor.getCount()));


                        //Comprobaci??n BBDD
                        if(cursor.getCount()==0){
                            Log.d("MESESAGE", "BBDD VAC??A");
                            insertDataBase(products);
                            Log.d("INSERT", "PRODUCTOS INSERTADOS");
                            cursor = getContentResolver().query(DataContract.ProductInternalClass.buildProductUri(),DataContract.ProductInternalClass.ALL_FIELDS,null,null, null);
                            //Log.d("PRODUCTOS EN BBDD", Integer.toString(cursor.getCount()));

                        }
                        else{

                            if(cursor.getCount()<products.edges.size()){
                                getContentResolver().delete(DataContract.ProductInternalClass.buildProductUri(),null,null);
                                insertDataBase(products);
                            }
                            else{
                                Log.e("MESESAGE", "YA EXISTE BBDD --> ACTUALIZAR");
                                updateDataBase(products);
                            }

                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e("Apollo", "Error"+ e.getMessage());
                    }
                });

    }

    private void updateDataBase(ProductQuery.Products products) {

        String compareAtPrice="";

        String[] selectionArgs=null;
        String selection ="";
        int o=0;
        int t=0;
        int y=0;

        try {
            ContentValues productContentValues[] = new ContentValues[products.edges.size()];
            ContentValues imageContentValues[] = null;
            ContentValues variantContentValues[]=null;



            for (int i=0; i<products.edges.size() ;i++) //productos
            {
                Log.e("Producto a actualizar", products.edges.get(i).node.title );


                String collection="";

                if(products.edges.get(i).node.collections.edges.size()==0){
                    collection="OUTLET";
                }
                else{

                    if (products.edges.get(i).node.collections.edges.get(0).node.title().toString().equals("SOCKS")){
                        collection="ACCESSORIES";
                    }else if(products.edges.get(i).node.collections.edges.get(0).node.title().toString().equals("UNDERWEAR")){
                        collection="ACCESSORIES";
                    }else{
                        collection = products.edges.get(i).node.collections.edges.get(0).node.title().toString();
                    }

                }
                Log.e("Colecci??n producto",products.edges.get(i).node.title+": "+collection );



                ProductModel productModel = new ProductModel(products.edges.get(i).node.id,products.edges.get(i).node.title,String.valueOf(products.edges.get(i).node.availableForSale()),
                        products.edges.get(i).node.description,products.edges.get(i).node.productType,products.edges.get(i).node.images().edges.get(0).node.originalSrc.toString(),
                        products.edges.get(i).node.presentmentPriceRanges.edges.get(0).node.maxVariantPrice.amount.toString(),
                        collection);


                selectionArgs = new String[]{products.edges.get(i).node.id};
                selection = DataContract.ProductInternalClass.ID + " = ?";

                o = getContentResolver().update(DataContract.ProductInternalClass.buildProductUri(),
                        DataContract.ProductInternalClass.ProductToContentValues(productModel),selection,selectionArgs);

                Log.e("Num. Productos actualizados",""+o);


                //productContentValues[i] = DataContract.ProductInternalClass.ProductToContentValues(productModel);

                imageContentValues = new ContentValues[products.edges().get(i).node.images.edges().size()];
                for(int j=0; j<products.edges().get(i).node.images.edges().size(); j++)
                { //imagenes

                    ImageModel imageModel = new ImageModel(products.edges.get(i).node.images.edges.get(j).node.id,
                            products.edges.get(i).node.id,products.edges.get(i).node.images.edges.get(j).node.originalSrc.toString());

                    selectionArgs = new String[]{products.edges.get(i).node.images.edges.get(j).node.id};
                    selection = DataContract.ImageInternalClass.ID + " = ?";

                    y = getContentResolver().update(DataContract.ImageInternalClass.buildImageUri(),
                            DataContract.ImageInternalClass.ImageToContentValues(imageModel),selection,selectionArgs);

                    Log.e("Num. Imagenes actualizadas",""+y);

                    //imageContentValues[j] = DataContract.ImageInternalClass.ImageToContentValues(imageModel);
                }

                //Integer p =getContentResolver().bulkInsert(DataContract.ImageInternalClass.buildImageUri(), imageContentValues);
                //Log.d("Numero de Insercciones de IMAGE", Integer.toString(p) );

                variantContentValues = new ContentValues[products.edges().get(i).node.variants.edges().size()];
                for(int z=0; z<products.edges().get(i).node.variants.edges.size(); z++)//variantes
                {

                    Log.e("Variante agregar", products.edges.get(i).node.variants.edges.get(z).node.title );


                    if(products.edges.get(i).node().variants.edges.get(z).node.compareAtPriceV2==null){
                        compareAtPrice="";
                    }
                    else{
                        compareAtPrice=products.edges.get(i).node.variants.edges.get(z).node.compareAtPriceV2.amount.toString();
                    }
                    String available = "";
                    if(products.edges().get(i).node.variants.edges.get(z).node.availableForSale==true){
                        available = "true";
                    }
                    else {
                        available="false";
                    }

                    Log.e("DISPONIBLE",available);

                    VariantModel variantModel = new VariantModel(products.edges.get(i).node.variants.edges.get(z).node.id,
                            products.edges.get(i).node.id,available,
                            products.edges.get(i).node.variants.edges.get(z).node.sku,products.edges.get(i).node.variants.edges.get(z).node.title,
                            products.edges.get(i).node.variants.edges.get(z).node.priceV2.amount.toString(),
                            compareAtPrice);

                    //variantContentValues[z] = DataContract.VariantInternalClass.VariantToContentValues(variantModel);

                    selectionArgs = new String[]{products.edges.get(i).node.variants.edges.get(z).node.id};
                    selection = DataContract.VariantInternalClass.ID + " = ?";

                    t = getContentResolver().update(DataContract.VariantInternalClass.buildVariantUri(),
                            DataContract.VariantInternalClass.VariantToContentValues(variantModel),selection,selectionArgs);

                    Log.e("Num. Variantes actualizadas",""+t);
                }

                //Integer r =getContentResolver().bulkInsert(DataContract.VariantInternalClass.buildVariantUri(), variantContentValues);
                //Log.d("Numero de Insercciones de VARIANT", Integer.toString(r) );



            }

            //Integer o =getContentResolver().bulkInsert(DataContract.ProductInternalClass.buildProductUri(), productContentValues);

            Log.e("MENSAJE", "Todos los productos actualizados!");

        }catch (Exception e){
            Log.e("Exception ", e.getMessage() );
        }
    }


    private void insertDataBase(ProductQuery.Products products) {

        String compareAtPrice="";

        try {
                ContentValues productContentValues[] = new ContentValues[products.edges.size()];
                ContentValues imageContentValues[] = null;
                ContentValues variantContentValues[]=null;


                for (int i=0; i<products.edges.size() ;i++)
                {
                    Log.e("Producto a agregar", products.edges.get(i).node.title );
                    //Log.e("Numero de colecciones del producto", ""+products.edges.get(i).node.collections.edges.size());
                    //Log.e("Colecci??n del producto", products.edges.get(i).node.collections.edges.get(0).node.title);

                    String collection="";

                    if(products.edges.get(i).node.collections.edges.size()==0){
                        collection="OUTLET";
                        Log.e("HOLA", "ENTRA AQUI");
                    }
                    else{

                        if (products.edges.get(i).node.collections.edges.get(0).node.title().toString().equals("SOCKS")){
                            collection="ACCESSORIES";
                        }else if(products.edges.get(i).node.collections.edges.get(0).node.title().toString().equals("UNDERWEAR")){
                            collection="ACCESSORIES";
                        }else{
                            collection = products.edges.get(i).node.collections.edges.get(0).node.title().toString();
                        }

                    }






                    ProductModel productModel = new ProductModel(products.edges.get(i).node.id,products.edges.get(i).node.title,String.valueOf(products.edges.get(i).node.availableForSale()),
                            products.edges.get(i).node.description,products.edges.get(i).node.productType,products.edges.get(i).node.images().edges.get(0).node.originalSrc.toString(),
                             products.edges.get(i).node.presentmentPriceRanges.edges.get(0).node.maxVariantPrice.amount.toString(), collection);

                    productContentValues[i] = DataContract.ProductInternalClass.ProductToContentValues(productModel);

                    imageContentValues = new ContentValues[products.edges().get(i).node.images.edges().size()];
                    for(int j=0; j<products.edges().get(i).node.images.edges().size(); j++){

                        ImageModel imageModel = new ImageModel(products.edges.get(i).node.images.edges.get(j).node.id,
                                products.edges.get(i).node.id,products.edges.get(i).node.images.edges.get(j).node.originalSrc.toString());

                        imageContentValues[j] = DataContract.ImageInternalClass.ImageToContentValues(imageModel);
                    }

                    Integer p =getContentResolver().bulkInsert(DataContract.ImageInternalClass.buildImageUri(), imageContentValues);
                    Log.d("Numero de Insercciones de IMAGE", Integer.toString(p) );

                    variantContentValues = new ContentValues[products.edges().get(i).node.variants.edges().size()];
                    for(int z=0; z<products.edges().get(i).node.variants.edges.size(); z++)
                    {

                        Log.e("Variante agregar", products.edges.get(i).node.variants.edges.get(z).node.title );


                        if(products.edges.get(i).node().variants.edges.get(z).node.compareAtPriceV2==null){
                           compareAtPrice="";
                        }
                        else{
                            compareAtPrice=products.edges.get(i).node.variants.edges.get(z).node.compareAtPriceV2.amount.toString();
                        }

                        VariantModel variantModel = new VariantModel(products.edges.get(i).node.variants.edges.get(z).node.id,
                                products.edges.get(i).node.id,String.valueOf(products.edges().get(i).node.variants.edges.get(z).node.availableForSale),
                                products.edges.get(i).node.variants.edges.get(z).node.sku,products.edges.get(i).node.variants.edges.get(z).node.title,
                                products.edges.get(i).node.variants.edges.get(z).node.priceV2.amount.toString(),
                                compareAtPrice);

                        variantContentValues[z] = DataContract.VariantInternalClass.VariantToContentValues(variantModel);
                    }

                    Integer r =getContentResolver().bulkInsert(DataContract.VariantInternalClass.buildVariantUri(), variantContentValues);
                    Log.d("Numero de Insercciones de VARIANT", Integer.toString(r) );



                }
                Integer o =getContentResolver().bulkInsert(DataContract.ProductInternalClass.buildProductUri(), productContentValues);
                Log.e("Numero de Insercciones de PRODUCT", Integer.toString(o) );

        }catch (Exception e){
            Log.e("Exception ", e.getMessage() );
        }

    }

    private int getCurrentTimeNumber(){
        int number=0;


        //Sacar hora actual
        Date date = new Date();
        DateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String stringCurrentTime = currentTime.format(date);
        Log.e("Hora actual en Splash Activity",stringCurrentTime);
        //

        //pruebas
        Log.e("test a??o current",stringCurrentTime.substring(0,4));
        Log.e("test mes current ",stringCurrentTime.substring(5,7));
        Log.e("test seg current ",stringCurrentTime.substring(17,19));
        //


        number = Integer.parseInt(stringCurrentTime.substring(0,4))*100000+  //a??o
                +Integer.parseInt(stringCurrentTime.substring(5,7))*10000+  //mes
                Integer.parseInt(stringCurrentTime.substring(8,10))*1000+  //dia
                Integer.parseInt(stringCurrentTime.substring(11,13))*100+ //hora
                Integer.parseInt(stringCurrentTime.substring(14,16))*10+ //min
                Integer.parseInt(stringCurrentTime.substring(17,19)); //seg




        return number;
    }

}