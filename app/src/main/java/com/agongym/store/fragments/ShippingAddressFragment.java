package com.agongym.store.fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.agongym.store.Apollo;
import com.agongym.store.activities.CheckoutCreateMutation;
import com.agongym.store.activities.CustomerAddressCreateMutation;
import com.agongym.store.activities.CustomerAddressUpdateMutation;
import com.agongym.store.activities.CustomerQuery;
import com.agongym.store.activities.type.CheckoutCreateInput;
import com.agongym.store.activities.type.CheckoutLineItemInput;
import com.agongym.store.activities.type.MailingAddressInput;
import com.agongym.store.database.DataContract;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.agongym.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ShippingAddressFragment extends Fragment {

    String checkoutId = "";
    String shippingRateTitle = "";
    String shippingRatePrice ="";
    String totalOrderAmount = "";
    String handle = "";


    Cursor cursor;
    String token = "";
    CustomerQuery.DefaultAddress defaultAddress =  null;
    int i=0;
    int j=0;
    int x=0;

    String defaultAddressId="";

    //Valores
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


    //views
    EditText name;
    EditText lastName;
    EditText address1;
    EditText address2;
    EditText zip;
    EditText city;
    EditText province;
    EditText country;
    EditText phone;
    CheckBox checkBox;
    Button button;

    boolean userExists=false;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_shipping_address, container, false);

        //checkoutId=getArguments().getString("checkoutId");

        Log.e("CHECKOUT ID EN LOGGEDUSER...",checkoutId);

        i=0;
        j=0;
        x=0;
        checkoutId="";



        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        name = (EditText) getView().findViewById(R.id.checkout_name);
        lastName = (EditText)getView().findViewById(R.id.checkout_lastname);
        address1 = (EditText)getView().findViewById(R.id.checkout_address1);
        address2 = (EditText)getView().findViewById(R.id.checkout_address2);
        zip = (EditText)getView().findViewById(R.id.checkout_zip);
        city = (EditText)getView().findViewById(R.id.checkout_city);
        province = (EditText)getView().findViewById(R.id.checkout_province);
        country = (EditText)getView().findViewById(R.id.checkout_country);
        phone = (EditText)getView().findViewById(R.id.checkout_phone);
        checkBox = (CheckBox)getView().findViewById(R.id.checkout_checkbox);
        button = (Button)getView().findViewById(R.id.checkout_button);

        checkShippingInfo();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (name.getText().toString().equals("") || lastName.getText().toString().equals("") || address1.getText().toString().equals("") ||
                        address2.getText().toString().equals("") ||
                        zip.getText().toString().equals("") || city.getText().toString().equals("") || province.getText().toString().equals("") ||
                        country.getText().equals("") || phone.getText().equals("")) {

                    Toast.makeText(getActivity().getApplicationContext(), "¡Hay campos vacios!", Toast.LENGTH_LONG).show();

                }

                else{

                    String checkoutId = "";


                    if(checkBox.isChecked()&& !userExists){
                        createDefaultAddress();
                        checkoutId=checkoutAction();
                    }

                    if(!checkBox.isChecked()&&userExists){
                        checkoutId=checkoutAction();
                    }

                    if(checkBox.isChecked() && userExists){
                        Log.e("Usuario solicita:", "ACTUALIZAR DATOS");
                        updateDefaultAddress();
                        checkoutId=checkoutAction();
                    }
                }

                Toast.makeText(getActivity().getApplicationContext(),"TODO OK JOSE LUIS", Toast.LENGTH_LONG).show();

                if(!checkoutId.equals("")){

                    //

                   Bundle bundle = new Bundle();
                   bundle.putString("checkoutId",checkoutId);
                   bundle.putString("shippingRateTitle",shippingRateTitle);
                   bundle.putString("shippingRatePrice", shippingRatePrice);
                    bundle.putString("handle", handle);

                   Navigation.findNavController(v).navigate(R.id.fragment_confirm_payment,bundle);


                }


            }
        });



    }

    private void updateDefaultAddress() {

        //Cliente Apollo
        Apollo apolloObject = new Apollo();
        ApolloClient apolloClient = apolloObject.getApolloClient();
        //

        //Shipping Address
        Input<String> valueFirstname = new Input<>(name.getText().toString(),true);
        Input<String> valueLastName = new Input<>(lastName.getText().toString(),true);
        Input<String> valueAddress1 = new Input<>(address1.getText().toString(),true);
        Input<String> valueAddress2 = new Input<>(address2.getText().toString(),true);
        Input<String> valueZip = new Input<>(zip.getText().toString(),true);
        Input<String> valueCity = new Input<>(city.getText().toString(),true);
        Input<String> valueProvince = new Input<>(province.getText().toString(),true);
        Input<String> valueCountry = new Input<>(country.getText().toString(),true);
        Input<String> valuePhone = new Input<>(phone.getText().toString(),true);

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



        apolloClient.mutate(new CustomerAddressUpdateMutation(token,defaultAddressId,mailingAddressInput)).enqueue(new ApolloCall.Callback<CustomerAddressUpdateMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<CustomerAddressUpdateMutation.Data> response) {
                Log.e("update ok, customer id","mola");

                Log.e("New Country",response.getData().customerAddressUpdate().customerAddress().country());
                x=1;
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }
        });
        while (x==0){ }



    }


    private void createDefaultAddress() {

        //Cliente Apollo
        Apollo apolloObject = new Apollo();
        ApolloClient apolloClient = apolloObject.getApolloClient();
        //

        //Shipping Address
        Input<String> valueFirstname = new Input<>(name.getText().toString(),true);
        Input<String> valueLastName = new Input<>(lastName.getText().toString(),true);
        Input<String> valueAddress1 = new Input<>(address1.getText().toString(),true);
        Input<String> valueAddress2 = new Input<>(address2.getText().toString(),true);
        Input<String> valueZip = new Input<>(zip.getText().toString(),true);
        Input<String> valueCity = new Input<>(city.getText().toString(),true);
        Input<String> valueProvince = new Input<>(province.getText().toString(),true);
        Input<String> valueCountry = new Input<>(country.getText().toString(),true);
        Input<String> valuePhone = new Input<>(phone.getText().toString(),true);

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

        apolloClient.mutate(new CustomerAddressCreateMutation(token, mailingAddressInput)).enqueue(new ApolloCall.Callback<CustomerAddressCreateMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<CustomerAddressCreateMutation.Data> response) {
                String address_response_id = response.getData().customerAddressCreate().customerAddress().id();

                Log.e("created address id",address_response_id);
                j=1;
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }

        });

        while(j==0) {

        }


    }


    public void checkShippingInfo() {


        //aqui debo comprobar si el usuario está loggeado o no y mostrar un fragment u otro
        //Cliente Apollo
        Apollo apolloObject = new Apollo();
        ApolloClient apolloClient = apolloObject.getApolloClient();
        //

        //Argumentos mutación
        cursor = getActivity().getContentResolver().query(DataContract.CustomerInternalClass.buildCartUri(), DataContract.CustomerInternalClass.ALL_FIELDS, null,
                null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(DataContract.CustomerInternalClass.ACCESS_TOKEN);
        cursor.moveToFirst();
        token = cursor.getString(columnIndex);
        Log.e("TOKEN ACTUAL", token);


        apolloClient.query(new CustomerQuery(token)).enqueue(new ApolloCall.Callback<CustomerQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<CustomerQuery.Data> response) {

                if(response.getData().customer().defaultAddress()!=null){

                    defaultAddress = response.getData().customer().defaultAddress();
                    Log.e("Response Apollo", response.getData().toString());
                    Log.e("Default Address ID", response.getData().customer().defaultAddress().id());
                    defaultAddressId=defaultAddress.id();
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

            userExists = true;

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

            updateViewsValues();

        } else {
            userExists = false;
            Log.e("USER INFO", "USUARIO SIN DATOS DE ENVÍO");
        }
    }


    private void updateViewsValues() {

        if(userName!=null){
            name.setText(userName);
        }

        if(userLastName!=null){
            lastName.setText(userLastName);
        }

        if(userAddress1!=null){
            address1.setText(userAddress1);
        }

        if(userAddress2!=null){
            address2.setText(userAddress2);
        }

        if(userZip!=null){
            zip.setText(userZip);
        }

        if(userCity!=null){
            city.setText(userCity);
        }

        if(userProvince!=null){
            province.setText(userProvince);
        }

        if(userCountry!=null){
            country.setText(userCountry);
        }

        if(userPhone!=null){
            phone.setText(userPhone);
        }




    }


    private String checkoutAction() {

        final String[] checkoutAuxId = {""};

        cursor =  getActivity().getContentResolver().query(DataContract.CartInternalClass.buildCartUri(), DataContract.CartInternalClass.ALL_FIELDS,null,
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

            //Shipping Address
            Input<String> valueFirstname = new Input<>(name.getText().toString(),true);
            Input<String> valueLastName = new Input<>(lastName.getText().toString(),true);
            Input<String> valueAddress1 = new Input<>(address1.getText().toString(),true);
            Input<String> valueAddress2 = new Input<>(address2.getText().toString(),true);
            Input<String> valueZip = new Input<>(zip.getText().toString(),true);
            Input<String> valueCity = new Input<>(city.getText().toString(),true);
            Input<String> valueProvince = new Input<>(province.getText().toString(),true);
            Input<String> valueCountry = new Input<>(country.getText().toString(),true);
            Input<String> valuePhone = new Input<>(phone.getText().toString(),true);

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

                    if(response.getData().checkoutCreate().checkout()==null){
                        for(int h=0;h<response.getData().checkoutCreate().checkoutUserErrors().size();h++){
                            Log.e("ERROR",response.getData().checkoutCreate().checkoutUserErrors().get(h).message());
                            checkoutId="ERROR";
                        }


                    }
                    else{
                        checkoutAuxId[0] =response.getData().checkoutCreate().checkout().id();
                        checkoutId = checkoutAuxId[0].toString();
                        shippingRateTitle = response.getData().checkoutCreate().checkout().availableShippingRates().shippingRates().get(0).title();
                        shippingRatePrice = response.getData().checkoutCreate().checkout().availableShippingRates().shippingRates().get(0).priceV2().toString();
                        totalOrderAmount = response.getData().checkoutCreate().checkout().totalPriceV2().amount().toString();
                        handle = response.getData().checkoutCreate().checkout().availableShippingRates().shippingRates().get(0).handle();

                        Log.e("SUBTOTAL PRICE" , response.getData().checkoutCreate().checkout().subtotalPriceV2().amount().toString());


                        Log.e("TOTAL PRICE" , totalOrderAmount);

                        Log.e("LineItemsSIZE" , ""+response.getData().checkoutCreate().checkout().lineItems().edges().size());

                        Log.e("ID CHECKOUT ID" , checkoutId);




                        //pruebas
                        for(int j=0;j<response.getData().checkoutCreate().checkout().lineItems().edges().size();j++){
                            Log.e("LineItemsTitle" , response.getData().checkoutCreate().checkout().lineItems().edges().get(j).node().title());
                        }

                        for(int w=0;w<response.getData().checkoutCreate().checkout().availableShippingRates().shippingRates().size();w++){
                            Log.e("Tarifa envio" , response.getData().checkoutCreate().checkout().availableShippingRates().shippingRates().get(w).title());
                            Log.e("PRECIO Tarifa envio" , response.getData().checkoutCreate().checkout().availableShippingRates().shippingRates().get(w).priceV2().amount().toString());
                            Log.e("HANDLE" , response.getData().checkoutCreate().checkout().availableShippingRates().shippingRates().get(w).handle());
                        }



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

        return checkoutId;


    }


}