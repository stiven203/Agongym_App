package com.agongym.store.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.agongym.store.Apollo;
import com.agongym.store.activities.type.CheckoutCreateInput;
import com.agongym.store.activities.type.CheckoutLineItemInput;
import com.agongym.store.activities.type.MailingAddressInput;
import com.agongym.store.adapters.CartAdapter;
import com.agongym.store.database.DataContract;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.agongym.R;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {


    Button place_order_butt;
    Cursor cursor;
    CartAdapter ca=null;
    Intent intent;
    TextView subtotalTV;
    String subtotalPrice="";

    //Valores
    CustomerQuery.DefaultAddress defaultAddress =  null;
    String token = "";
    String checkoutId = "";
    String addressId = "";
    String userName= null;
    String userLastName= null;
    String userAddress1= null;
    String userAddress2= null;
    String userZip= null;
    String userCity= null;
    String userProvince= null;
    String userCountry= null;
    String userPhone= null;
    boolean userShippingInfo =false;
    boolean userLogged = false;
    String webUrl = "";

    //control callbacks
    int i=0;
    int j=0;
    int x=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        subtotalTV = (TextView) findViewById(R.id.subtotal_text_view);

        i = 0;
        j=0;
        checkoutId="";
        token="";

        ListView cartLV = findViewById(R.id.list_view_cart);

        cursor =  getContentResolver().query(DataContract.CartInternalClass.buildCartUri(), DataContract.CartInternalClass.ALL_FIELDS,null,
                null,null);


        ca = new CartAdapter(this, cursor);
        cartLV.setAdapter(ca);
        ca.notifyDataSetChanged();




    }



    @Override
    protected void onResume() {
        super.onResume();

        Log.e("Vuelvo por aqui","ONRESUMEEEEEE");


    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e("Vuelvo por aqui","ONSTART");


        place_order_butt = (Button)findViewById(R.id.buttonPlaceOrder);


        place_order_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cursor =  getContentResolver().query(DataContract.CartInternalClass.buildCartUri(), DataContract.CartInternalClass.ALL_FIELDS,null,
                        null,null);

                //Toast.makeText(getApplicationContext(),"GOING...Payment Activity", Toast.LENGTH_LONG).show();

                checkLoggedUser();

                if(userLogged){

                    checkShippingInfo();

                    if(userShippingInfo){

                        Log.e("USUARIO ESTADO ","CON DATOS DE ENVIO");
                        checkoutLoggedShippingInfo();

                    }

                    else{
                        Log.e("USUARIO ESTADO ","SIN DATOS DE ENVIO");
                        checkoutNoShippingInfoAction();
                    }
                }

                else {
                    Log.e("USUARIO ESTADO ","NO LOGGEADO");
                    checkoutNoShippingInfoAction();

                }

                //webViewActivity();

                openBrowser();

            }
        });


        setSubtotalPrice();




    }

    private void setSubtotalPrice() {



        Handler h = new Handler();
        int delay = 50; //milliseconds

        h.postDelayed(new Runnable(){
            public void run(){
                //do something
                subtotalPrice = updateSubtotalPrice();
                subtotalTV.setText(subtotalPrice+ "€");
                h.postDelayed(this, delay);
            }
        }, delay);


    }

    private String updateSubtotalPrice() {
        String subPrice = "";



        Cursor auxCursor;
        auxCursor = getContentResolver().query(DataContract.CartInternalClass.buildCartUri(), DataContract.CartInternalClass.ALL_FIELDS,null,
                null,null);

        Log.e("SIZE OF CURSOR",""+auxCursor.getCount());

        String [] variantPrices = new String[auxCursor.getCount()];
        String [] variantQuantity = new String[auxCursor.getCount()];

        int w=0;

        int columnIndex1 = auxCursor.getColumnIndexOrThrow(DataContract.CartInternalClass.VARIANT_PRICE);
        int columnIndex2 = auxCursor.getColumnIndexOrThrow(DataContract.CartInternalClass.QUANTITY);

        auxCursor.moveToFirst();

        do{

            variantPrices[w] = auxCursor.getString(columnIndex1);
            variantQuantity[w] = auxCursor.getString(columnIndex2);
            Log.e("PRECIO DE VARIANTE","POS "+ w+ " "+auxCursor.getString(columnIndex1));
            w++;


        }while (auxCursor.moveToNext());

        double subtotal=0.0;

        for(int i=0;i<variantPrices.length;i++){
            subtotal =  subtotal + (Double.parseDouble(variantPrices[i])*Double.parseDouble(variantQuantity[i]));
        }

        DecimalFormat numberFormat = new DecimalFormat("#.00");

        subPrice = numberFormat.format(subtotal).toString();

        Log.e("SUBTOTAL AHORA ES",""+subtotal);








        return subPrice;


    }


    private void checkLoggedUser() {

        cursor = getContentResolver().query(DataContract.CustomerInternalClass.buildCartUri(), DataContract.CustomerInternalClass.ALL_FIELDS, null,
                null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(DataContract.CustomerInternalClass.ACCESS_TOKEN);

        if(cursor.getCount()!=0){
            cursor.moveToFirst();
            token = cursor.getString(columnIndex);
            Log.e("TOKEN ACTUAL", token);
            userLogged=true;
        }


    }

    private void openBrowser() {

        intent = new Intent (Intent.ACTION_VIEW);
        intent.setData(Uri.parse(webUrl));
        startActivity(intent);

        //aqui borar productos del carrito y cerrar activity



    }

    private void webViewActivity() {
        intent = new Intent(getApplicationContext(), PaymentActivity.class);
        intent.putExtra("webUrl",webUrl);
        startActivity(intent);
    }


    private void checkoutNoShippingInfoAction() {

        final String[] checkoutAuxId = {""};

        cursor =  getContentResolver().query(DataContract.CartInternalClass.buildCartUri(), DataContract.CartInternalClass.ALL_FIELDS,null,
                null,null);


        if(cursor!=null){


            List<CheckoutLineItemInput> listCheckoutLineItemInput = new ArrayList<>();

            CheckoutLineItemInput auxCheckoutLineItemInput;

            int quantity=0;
            String variantId = "";
            int  columnIndex1 = cursor.getColumnIndexOrThrow(DataContract.CartInternalClass.QUANTITY);
            int  columnIndex2 = cursor.getColumnIndexOrThrow(DataContract.CartInternalClass.VARIANT_ID);

            cursor.moveToFirst();

            do{
                quantity = Integer.parseInt(cursor.getString(columnIndex1));
                Log.e("Quantity",""+quantity);

                variantId = cursor.getString(columnIndex2);
                Log.e("VariantId",variantId);

                auxCheckoutLineItemInput = CheckoutLineItemInput.builder()

                        .quantity(quantity)
                        .variantId(variantId).build();

                listCheckoutLineItemInput.add(auxCheckoutLineItemInput);



            }while(cursor.moveToNext());

            Input<List<CheckoutLineItemInput>> CheckoutLineItemInput = new Input<>(listCheckoutLineItemInput,true);



            CheckoutCreateInput checkoutCreateInput = CheckoutCreateInput.builder()
                    .lineItemsInput(CheckoutLineItemInput)
                    .allowPartialAddresses(true)
                    .build();
            //

            //cliente apollo
            Apollo apolloObject = new Apollo();
            ApolloClient apolloClient = apolloObject.getApolloClient();
            //



            apolloClient.mutate(new CheckoutCreateMutation(checkoutCreateInput)).enqueue(new ApolloCall.Callback<CheckoutCreateMutation.Data>() {
                @Override
                public void onResponse(@NotNull Response<CheckoutCreateMutation.Data> response) {

                    if(response.getData().checkoutCreate().checkout()==null){
                        for(int h=0;h<response.getData().checkoutCreate().checkoutUserErrors().size();h++){
                            Log.e("ERROR",response.getData().checkoutCreate().checkoutUserErrors().get(h).message());
                            checkoutId="ERROR";
                        }


                    }
                    else{
                        checkoutAuxId[0] =response.getData().checkoutCreate().checkout().id();
                        checkoutId = checkoutAuxId[0].toString();

                        webUrl = response.getData().checkoutCreate.checkout.webUrl.toString();


                        Log.e("SUBTOTAL PRICE" , response.getData().checkoutCreate().checkout().subtotalPriceV2().amount().toString());

                        Log.e("LineItemsSIZE" , ""+response.getData().checkoutCreate().checkout().lineItems().edges().size());

                        Log.e("ID CHECKOUT ID" , checkoutId);

                        Log.e("WEB URL" , webUrl);




                        //pruebas
                        for(int j=0;j<response.getData().checkoutCreate().checkout().lineItems().edges().size();j++){
                            Log.e("LineItemsTitle" , response.getData().checkoutCreate().checkout().lineItems().edges().get(j).node().title());
                        }

                        if(response.getData().checkoutCreate().checkout().availableShippingRates()!=null){

                            for(int w=0;w<response.getData().checkoutCreate().checkout().availableShippingRates().shippingRates().size();w++){
                                Log.e("Tarifa envio" , response.getData().checkoutCreate().checkout().availableShippingRates().shippingRates().get(w).title());
                                Log.e("PRECIO Tarifa envio" , response.getData().checkoutCreate().checkout().availableShippingRates().shippingRates().get(w).priceV2().amount().toString());
                                Log.e("HANDLE" , response.getData().checkoutCreate().checkout().availableShippingRates().shippingRates().get(w).handle());
                            }
                        }



                        ///

                    }


                }

                @Override
                public void onFailure(@NotNull ApolloException e) {

                    Log.e("ERROR CHECKOUT" , e.getMessage().toString());



                }
            });

            //Se espera hasta que el metodo callback de apollo devuelva el ID del checkout

            Log.e("VALOR DE CHECKOUTID ANTES DE BUCLE",checkoutId);

            while(checkoutId.equals("")){

                //Log.e("ENTRA EN BUCLE","BUCLEWWWW");

            }

            //Log.e("SALE DEL BUCLE","BUCLEWWWW");




        }



    }


    private void checkoutLoggedShippingInfo() {

        checkoutShippingInfoAction();

        //Apollo
        Apollo apolloObject = new Apollo();
        ApolloClient apolloClient = apolloObject.getApolloClient();
        //

        apolloClient.mutate(new AssociateCustomerWithCheckoutMutation(checkoutId,token)).enqueue(new ApolloCall.Callback<AssociateCustomerWithCheckoutMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<AssociateCustomerWithCheckoutMutation.Data> response) {
                j=10;
                Log.e("todo ok", "JOSE LUIS");

            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }
        });

        while(j==0){}


    }


    private void checkoutShippingInfoAction() {

        final String[] checkoutAuxId = {""};

        cursor = getContentResolver().query(DataContract.CartInternalClass.buildCartUri(), DataContract.CartInternalClass.ALL_FIELDS, null,
                null, null);


        if (cursor != null) {


            List<CheckoutLineItemInput> listCheckoutLineItemInput = new ArrayList<>();

            CheckoutLineItemInput auxCheckoutLineItemInput;

            int quantity = 0;
            String variantId = "";
            int columnIndex1 = cursor.getColumnIndexOrThrow(DataContract.CartInternalClass.QUANTITY);
            int columnIndex2 = cursor.getColumnIndexOrThrow(DataContract.CartInternalClass.VARIANT_ID);

            cursor.moveToFirst();

            do {
                quantity = Integer.parseInt(cursor.getString(columnIndex1));
                Log.e("Quantity", "" + quantity);

                variantId = cursor.getString(columnIndex2);
                Log.e("VariantId", variantId);

                auxCheckoutLineItemInput = CheckoutLineItemInput.builder()

                        .quantity(quantity)
                        .variantId(variantId).build();

                listCheckoutLineItemInput.add(auxCheckoutLineItemInput);


            } while (cursor.moveToNext());

            Input<List<CheckoutLineItemInput>> CheckoutLineItemInput = new Input<>(listCheckoutLineItemInput, true);


            //Shipping Address
            Input<String> valueFirstname = new Input<>(userName, true);
            Input<String> valueLastName = new Input<>(userLastName, true);
            Input<String> valueAddress1 = new Input<>(userAddress1, true);
            Input<String> valueAddress2 = new Input<>(userAddress2, true);
            Input<String> valueZip = new Input<>(userZip, true);
            Input<String> valueCity = new Input<>(userCity, true);
            Input<String> valueProvince = new Input<>(userProvince, true);
            Input<String> valueCountry = new Input<>(userCountry, true);
            Input<String> valuePhone = new Input<>(userPhone, true);

            MailingAddressInput mailingAddressInput = MailingAddressInput.builder()
                    .firstNameInput(valueFirstname)
                    .lastNameInput(valueLastName)
                    .address1Input(valueAddress1)
                    .address2Input(valueAddress2)
                    .zipInput(valueZip)
                    .cityInput(valueCity)
                    .provinceInput(valueProvince)
                    .countryInput(valueCountry)
                    .phoneInput(valuePhone)
                    .build();

            //


            CheckoutCreateInput checkoutCreateInput = CheckoutCreateInput.builder()
                    .lineItemsInput(CheckoutLineItemInput)
                    .shippingAddress(mailingAddressInput)
                    .allowPartialAddresses(true)
                    .build();
            //

            //cliente apollo
            Apollo apolloObject = new Apollo();
            ApolloClient apolloClient = apolloObject.getApolloClient();
            //


            apolloClient.mutate(new CheckoutCreateMutation(checkoutCreateInput)).enqueue(new ApolloCall.Callback<CheckoutCreateMutation.Data>() {
                @Override
                public void onResponse(@NotNull Response<CheckoutCreateMutation.Data> response) {

                    if (response.getData().checkoutCreate().checkout() == null) {
                        for (int h = 0; h < response.getData().checkoutCreate().checkoutUserErrors().size(); h++) {
                            Log.e("ERROR", response.getData().checkoutCreate().checkoutUserErrors().get(h).message());
                            checkoutId = "ERROR";
                        }


                    } else {
                        checkoutAuxId[0] = response.getData().checkoutCreate().checkout().id();
                        checkoutId = checkoutAuxId[0].toString();
                        webUrl = response.getData().checkoutCreate.checkout.webUrl.toString();


                        Log.e("SUBTOTAL PRICE", response.getData().checkoutCreate().checkout().subtotalPriceV2().amount().toString());

                        Log.e("LineItemsSIZE", "" + response.getData().checkoutCreate().checkout().lineItems().edges().size());

                        Log.e("ID CHECKOUT ID", checkoutId);

                        Log.e("WEB URL", webUrl);


                        //pruebas
                        for (int j = 0; j < response.getData().checkoutCreate().checkout().lineItems().edges().size(); j++) {
                            Log.e("LineItemsTitle", response.getData().checkoutCreate().checkout().lineItems().edges().get(j).node().title());
                        }

                        if (response.getData().checkoutCreate().checkout().availableShippingRates() != null) {

                            for (int w = 0; w < response.getData().checkoutCreate().checkout().availableShippingRates().shippingRates().size(); w++) {
                                Log.e("Tarifa envio", response.getData().checkoutCreate().checkout().availableShippingRates().shippingRates().get(w).title());
                                Log.e("PRECIO Tarifa envio", response.getData().checkoutCreate().checkout().availableShippingRates().shippingRates().get(w).priceV2().amount().toString());
                                Log.e("HANDLE", response.getData().checkoutCreate().checkout().availableShippingRates().shippingRates().get(w).handle());
                            }
                        }


                        ///

                    }


                }

                @Override
                public void onFailure(@NotNull ApolloException e) {

                    Log.e("ERROR CHECKOUT", e.getMessage().toString());


                }
            });


            while (checkoutId.equals("")) {
            }


        }
    }


    private void checkShippingInfo() {

        //aqui debo comprobar si el usuario está loggeado o no y mostrar un fragment u otro
        //Cliente Apollo
        Apollo apolloObject = new Apollo();
        ApolloClient apolloClient = apolloObject.getApolloClient();
        //

        //Consultar Token o no

        /*
        cursor = getContentResolver().query(DataContract.CustomerInternalClass.buildCartUri(), DataContract.CustomerInternalClass.ALL_FIELDS, null,
                null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(DataContract.CustomerInternalClass.ACCESS_TOKEN);
        cursor.moveToFirst();
        token = cursor.getString(columnIndex);
        Log.e("TOKEN ACTUAL", token);

         */


        apolloClient.query(new CustomerQuery(token)).enqueue(new ApolloCall.Callback<CustomerQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<CustomerQuery.Data> response) {

                if(response.getData().customer().defaultAddress()!=null){

                    defaultAddress = response.getData().customer().defaultAddress();
                    Log.e("Response Apollo", response.getData().toString());
                    Log.e("Default Address ID", response.getData().customer().defaultAddress().id());
                    //defaultAddressId=defaultAddress.id();
                }

                //
                i = 1;
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

                Log.e("Apollo Error", e.getMessage());

            }
        });

        while (i == 0) {
        }

        if (defaultAddress != null) {

            userShippingInfo = true;

            Log.e("USER INFO", "USUARIO CON DATOS DE ENVÍO");
            addressId = defaultAddress.id();
            userName = defaultAddress.firstName();
            userLastName = defaultAddress.lastName();
            userAddress1 = defaultAddress.address1();
            userAddress2 = defaultAddress.address2();
            userZip = defaultAddress.zip();
            userCity = defaultAddress.city();
            userProvince = defaultAddress.province();
            userCountry = defaultAddress.country();
            userPhone = defaultAddress.phone();


        } else {
            userShippingInfo = false;
            Log.e("USER INFO", "USUARIO SIN DATOS DE ENVÍO");
        }


    }

}