package com.agongym.store.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agongym.store.Apollo;
import com.agongym.store.activities.CheckoutShippingLineUpdateMutation;
import com.agongym.store.activities.CheckoutShippingRateQuery;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.agongym.R;

import org.jetbrains.annotations.NotNull;


public class ConfirmPaymentFragment extends Fragment {

    String checkoutId="";
    String shippingRateTitle = "";
    String shippingRatePrice = "";
    String handle = "";
    int i=0;

    int x=0;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        i=0;
        x=0;

        checkoutId = getArguments().getString("checkoutId");
        shippingRateTitle = getArguments().getString("shippingRateTitle");
        shippingRatePrice = getArguments().getString("shippingRatePrice");
        handle = getArguments().getString("handle");

        //comprob.
        Log.e("el checkoutID que llega al ultimo fragment es",checkoutId);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_payment, container, false);



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        queryShippingRate();

        updateShippingRate();

        queryShippingRate();


    }

    private void updateShippingRate() {

        //Apollo
        Apollo apolloObject = new Apollo();
        ApolloClient apolloClient = apolloObject.getApolloClient();
        //

        apolloClient.mutate(new CheckoutShippingLineUpdateMutation(checkoutId,handle)).enqueue(new ApolloCall.Callback<CheckoutShippingLineUpdateMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<CheckoutShippingLineUpdateMutation.Data> response) {
                Log.e("RESPUESTA QUERY update",response.getData().checkoutShippingLineUpdate().toString());

                i=1;
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }
        });

        while(i==0){}

    }

    private void queryShippingRate() {

        //Apollo
        Apollo apolloObject = new Apollo();
        ApolloClient apolloClient = apolloObject.getApolloClient();
        //

        CheckoutShippingRateQuery checkoutShippingRateQuery  = CheckoutShippingRateQuery.builder().input(checkoutId).build();

        apolloClient.query(checkoutShippingRateQuery).enqueue(new ApolloCall.Callback<CheckoutShippingRateQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<CheckoutShippingRateQuery.Data> response) {
                Log.e("RESPUESTA QUERY",response.getData().node().toString());
                x=1;


            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }
        });

        while(x==0){}
    }


}