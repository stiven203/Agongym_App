package com.agongym.store.fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.agongym.store.Apollo;
import com.agongym.store.activities.AssociateCustomerWithCheckoutMutation;
import com.agongym.store.activities.CustomerQuery;
import com.agongym.store.adapters.OrdersAdapter;
import com.agongym.store.adapters.ProductsAdapter;
import com.agongym.store.database.DataContract;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.agongym.R;

import org.jetbrains.annotations.NotNull;

public class AccountFragment extends Fragment {

    int i=0;

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


    String name = "No hay";
    String lastName = "datos";
    String email = "No hay datos";
    String token ="";
    String address1= "";
    String address2= "";
    String city = "";
    String zip = "";
    String country = "";
    String phone = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        i=0;

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ΛGONGYM");
        return inflater.inflate(R.layout.account_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
            lastName = cursor.getString(cursor.getColumnIndexOrThrow(DataContract.CustomerInternalClass.LAST_NAME));
            email = cursor.getString(cursor.getColumnIndexOrThrow(DataContract.CustomerInternalClass.EMAIL));
            token = cursor.getString(cursor.getColumnIndexOrThrow(DataContract.CustomerInternalClass.ACCESS_TOKEN));


            updateAddress(token);

            String sortOrder = DataContract.OrderInternalClass.PROCESSED_AT + " DESC";

            auxCursor = getActivity().getContentResolver().query(DataContract.OrderInternalClass.buildCartUri(), DataContract.OrderInternalClass.ALL_FIELDS,null,
                    null,sortOrder);

            if(auxCursor.getCount()>0){
                oa = new OrdersAdapter(this.getContext(), auxCursor);
                ordersLV.setAdapter(oa);
            }
            else{
                Log.e("INFO ACCOUNT", "Este usuario no tiene pedidos");

            }



        }else {
            Log.e("INFO ACCOUNT", "Este usuario no está loggeado");

        }

        accountName.setText(name+ " "+lastName);
        accountEmail.setText(email);






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

                    address1= response.getData().customer().defaultAddress().address1();
                    address2=  response.getData().customer().defaultAddress().address2();
                    city = response.getData().customer().defaultAddress().city();
                    zip = response.getData().customer().defaultAddress().zip();
                    country = response.getData().customer().defaultAddress().country();
                    phone = response.getData().customer().defaultAddress().phone();
                    i=1;





                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }
        });

        while(i==0){}


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                addressTV.setText(address1+", "+address2);
                cityZipTV.setText(city+", "+zip);
                countryTV.setText(country);
                phoneTV.setText(phone);
                Log.e("ACCOUNT INFO","ADDRESS UPDATE!!!!!!!");
            }
        });














    }
}