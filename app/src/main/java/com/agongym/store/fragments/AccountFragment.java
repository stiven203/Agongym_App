package com.agongym.store.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.agongym.store.database.models.OrderModel;
import com.agongym.store.utils.Apollo;
import com.agongym.store.activities.CustomerQuery;
import com.agongym.store.activities.LogInActivity;
import com.agongym.store.adapters.OrdersAdapter;
import com.agongym.store.database.DataContract;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.agongym.R;

import org.jetbrains.annotations.NotNull;

public class AccountFragment extends Fragment {

    int i=0;
    int z=0;

    ListView ordersLV;
    Cursor cursor;
    Cursor auxCursor;
    private OrdersAdapter oa;

    TextView accountName;
    TextView accountEmail;
    TextView addressTV;
    TextView cityZipTV;
    TextView countryTV;
    TextView phoneTV;

    boolean addressData=false;
    boolean updateCursorOrders=false;

    String name = "";
    String lastName = "";
    String email = "";
    String token ="";
    String address1= "";
    String address2= "";
    String city = "";
    String zip = "";
    String country = "";
    String phone = "";




    @Override
    public void onCreate(Bundle savedInstanceState) {

        i=0;
        z=0;

        Log.e("INFO FRAGMENT", "Llega aqui 1");

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("INFO FRAGMENT", "Llega aqui 1.5");

        i=0;
        z=0;

        return inflater.inflate(R.layout.account_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {



        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ΛGONGYM");




        ordersLV = getActivity().findViewById(R.id.orders_list_view);
        accountName = getActivity().findViewById(R.id.account_name);
        accountEmail = getActivity().findViewById(R.id.account_email);
        addressTV = getActivity().findViewById(R.id.account_address_adress12);
        cityZipTV = getActivity().findViewById(R.id.account_address_cityzip);
        countryTV = getActivity().findViewById(R.id.account_address_country);
        phoneTV = getActivity().findViewById(R.id.account_address_phone);



        cursor =  getActivity().getContentResolver().query(DataContract.CustomerInternalClass.buildCartUri(), DataContract.CustomerInternalClass.ALL_FIELDS,null,
                null,null);

        if(cursor.getCount()>0)
        {


            cursor.moveToFirst();


            name = cursor.getString(cursor.getColumnIndexOrThrow(DataContract.CustomerInternalClass.FIRST_NAME));
            Log.e("INFO ACCOUNT", name);
            lastName = cursor.getString(cursor.getColumnIndexOrThrow(DataContract.CustomerInternalClass.LAST_NAME));
            Log.e("INFO ACCOUNT", lastName);
            email = cursor.getString(cursor.getColumnIndexOrThrow(DataContract.CustomerInternalClass.EMAIL));
            Log.e("INFO ACCOUNT", email);
            token = cursor.getString(cursor.getColumnIndexOrThrow(DataContract.CustomerInternalClass.ACCESS_TOKEN));
            Log.e("INFO ACCOUNT", token);

            updateAddress(token);


            Log.e("INFO FRAGMENT", "Llega aqui 4.5");

            String sortOrder = DataContract.OrderInternalClass.PROCESSED_AT + " DESC";

            auxCursor = getActivity().getContentResolver().query(DataContract.OrderInternalClass.buildOrderUri(), DataContract.OrderInternalClass.ALL_FIELDS,null,
                    null,sortOrder);

            updateCursorOrders = checkNewOrders(token);

            if(updateCursorOrders){

                auxCursor = getActivity().getContentResolver().query(DataContract.OrderInternalClass.buildOrderUri(), DataContract.OrderInternalClass.ALL_FIELDS,null,
                        null,sortOrder);
            }
            else {
                Log.e("INFO FRAGMENT", "El usuario tiene el MISMO NUMERO DE PEDIDOS");
            }



            if(auxCursor.getCount()>0){

                oa = new OrdersAdapter(this.getContext(), auxCursor);
                ordersLV.setAdapter(oa);

                Log.e("INFO FRAGMENT", "Llega aqui 8");
            }
            else{
                Log.e("INFO ACCOUNT", "Este usuario no tiene pedidos");

            }



        }

        accountName.setText(name+ " "+lastName);
        accountEmail.setText(email);

        Log.e("INFO FRAGMENT", "Llega aqui 10");

    }

    private boolean checkNewOrders(String token) {

        //Apollo
        Apollo apolloObject = new Apollo();
        ApolloClient apolloClient = apolloObject.getApolloClient();
        //

        final CustomerQuery.Orders[] orders = {null};
        final boolean[] updateDataBase = {false};
        final boolean[] insertDataBase = {false};
        final boolean[] dataChange = {false};


        final int[] totalOrders = {0};


        apolloClient.query(new CustomerQuery(token)).enqueue(new ApolloCall.Callback<CustomerQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<CustomerQuery.Data> response) {


                if(response.getData().customer().orders().edges().size()!=0){

                    orders[0] = response.getData().customer().orders();

                    totalOrders[0] = response.getData().customer().orders().edges().size();

                    if(totalOrders[0]>auxCursor.getCount()){ //hay nuevos pedidos en Shopify y ya habian algunos en la BBDD
                        updateDataBase[0] = true;
                        dataChange[0]=true;
                        Log.e("ORDER INFO","Se debe ACTUALIZAR la BBDD ya existente");
                    }
                    if(auxCursor.getCount()==0){ //BBDD vacia y hay pedidos del cliente en Shopify
                        insertDataBase[0]=true;
                        dataChange[0]=true;
                        Log.e("ORDER INFO","Se deben insertar pedidos en la BBDD vacia");
                    }

                    if(auxCursor.getCount()==totalOrders[0]){ //mismo numeros de pedidos Shopify y BBDD
                        dataChange[0]=false;
                        Log.e("ORDER INFO","El usuario tiene el mismo numero pedidos Shopify y BBDD");
                    }


                }
                else {
                    Log.e("ORDER INFO","Este usuario no tiene pedidos");
                }


                //
                z = 1;
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

                Log.e("Apollo Error", e.getMessage());

            }
        });

        while (z == 0) {

            Log.e("INFO FRAGMENT", "BUCLE 1");
        }

        //insercción BBDD Orders Vacia
        String auxDate = "";
        String date = "";

        if(insertDataBase[0]==true){

            ContentValues orderContentValues[] = new ContentValues[orders[0].edges().size()];

            for(int i = 0; i< orders[0].edges().size(); i++){

                date = orders[0].edges().get(i).node().processedAt().toString();
                auxDate = date.substring(0,4)+"-"+date.substring(5,7)+"-"+date.substring(8,10);

                OrderModel orderModel = new OrderModel(token, orders[0].edges().get(i).node().name(),
                        orders[0].edges().get(i).node().totalPriceV2().amount().toString(),auxDate);

                orderContentValues[i] = DataContract.OrderInternalClass.OrderToContentValues(orderModel);
            }

            Integer p =getActivity().getContentResolver().bulkInsert(DataContract.OrderInternalClass.buildOrderUri(), orderContentValues);
            Log.d("Numero de Insercciones de ORDERS", Integer.toString(p) );



        }

        //


        //Actualizar BBDD existente con pedidos nuevos

        String selection = "";
        String[] selectionArgs=null;
        int o=0;

        if(updateDataBase[0]==true){

            ContentValues orderContentValues[] = new ContentValues[orders[0].edges().size()];

            for(int i = 0; i< orders[0].edges().size(); i++){

                date = orders[0].edges().get(i).node().processedAt().toString();
                auxDate = date.substring(0,4)+"-"+date.substring(5,7)+"-"+date.substring(8,10);

                OrderModel orderModel = new OrderModel(token, orders[0].edges().get(i).node().name(),
                        orders[0].edges().get(i).node().totalPriceV2().amount().toString(),auxDate);


                selectionArgs = new String[]{orders[0].edges().get(i).node().name()};
                selection = DataContract.OrderInternalClass.NAME+ " = ?";

                o = getActivity().getContentResolver().update(DataContract.OrderInternalClass.buildOrderUri(),
                        DataContract.OrderInternalClass.OrderToContentValues(orderModel),selection,selectionArgs);

                Log.e("Num. Productos actualizados",""+o);

            }


        }


        return dataChange[0];
    }

    private void updateAddress(String token) {

        //Apollo
        Apollo apolloObject = new Apollo();
        ApolloClient apolloClient = apolloObject.getApolloClient();
        //




        apolloClient.query(new CustomerQuery(token)).enqueue(new ApolloCall.Callback<CustomerQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<CustomerQuery.Data> response) {
                if(response.getData().customer().defaultAddress()!=null){

                    addressData=true;

                    address1= response.getData().customer().defaultAddress().address1();
                    address2=  response.getData().customer().defaultAddress().address2();
                    city = response.getData().customer().defaultAddress().city();
                    zip = response.getData().customer().defaultAddress().zip();
                    country = response.getData().customer().defaultAddress().country();
                    phone = response.getData().customer().defaultAddress().phone();

                }



                i=1;

            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }
        });

        while(i==0){

            Log.e("INFO FRAGMENT", "BUCLE 2");
        }


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(addressData){

                    addressTV.setText(address1+", "+address2);
                    cityZipTV.setText(city+", "+zip);
                    countryTV.setText(country);
                    phoneTV.setText(phone);
                    Log.e("ACCOUNT INFO","ADDRESS UPDATE!!!!!!!");

                }else{
                    
                    addressTV.setText("Sin datos");
                    cityZipTV.setText("Sin datos");
                    countryTV.setText("Sin datos");
                    phoneTV.setText("Sin datos");
                }


            }
        });




    }


    @Override
    public void onAttach(@NonNull Context context) {

        Log.e("INFO FRAGMENT", "Vuelve por aquí");

        i=0;
        z=0;


        cursor =  getActivity().getContentResolver().query(DataContract.CustomerInternalClass.buildCartUri(), DataContract.CustomerInternalClass.ALL_FIELDS,null,
                null,null);

        if(cursor.getCount()<=0){

            Intent intent = new Intent(getActivity().getApplicationContext(), LogInActivity.class);
            startActivity(intent);
            getActivity().finish();

        }
        super.onAttach(context);
    }
}