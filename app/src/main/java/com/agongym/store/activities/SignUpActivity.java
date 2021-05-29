package com.agongym.store.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.agongym.store.utils.Apollo;
import com.agongym.store.activities.type.CustomerAccessTokenCreateInput;
import com.agongym.store.activities.type.CustomerCreateInput;
import com.agongym.store.database.DataContract;
import com.agongym.store.database.models.CustomerModel;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.agongym.R;

import org.jetbrains.annotations.NotNull;

import okhttp3.OkHttpClient;

public class SignUpActivity extends AppCompatActivity {
    CheckBox marketingCB;
    EditText name, lastName, email, pass;
    Button signup_btn;
    ContentValues customerContentValues;

    String customerFirstName = "";
    String customerLastName = "";
    String customerEmail = "";
    int i=0;


    int x=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        i=0;

        x=0;

        signup_btn = (Button) findViewById(R.id.signup_button);


        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marketingCB = (CheckBox) findViewById(R.id.marketing_checkBox);
                name= (EditText) findViewById(R.id.editTextTextName);
                lastName= (EditText) findViewById(R.id.editTextTextLastName);
                email= (EditText) findViewById(R.id.editTextTextEmail);
                pass= (EditText) findViewById(R.id.editTextTextPassword);

                Log.e("TextView Name", name.getText().toString());
                Log.e("TextView Last Name", lastName.getText().toString());
                Log.e("TextView Email", email.getText().toString());
                Log.e("TextView Pass", pass.getText().toString());

                if(name.getText().toString().equals("")||lastName.getText().toString().equals("")||email.getText().toString().equals("")||pass.getText().toString().equals("")){

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(SignUpActivity.this,
                                    "Falta rellenar algún campo", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            ;
                        }
                    });

                }else{
                    CustomerCreateInput input = CustomerCreateInput.builder()
                            .firstName(name.getText().toString())
                            .lastName(lastName.getText().toString())
                            .email(email.getText().toString())
                            .password(pass.getText().toString())
                            .acceptsMarketing(marketingCB.isChecked())
                            .build();

                    createUser(input);

                }
            }
        });





    }

    private void createUser(CustomerCreateInput input) {
        //OkHttpClient
        String token = "be05b0140c6b668dd468583ae1ca448d";
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request.Builder builder = original.newBuilder().method(original.method(), original.body());
                    builder.header("Accept", "application/json");
                    builder.header("X-Shopify-Storefront-Access-Token", token);
                    return chain.proceed(builder.build());
                })
                .build();


        // Create an 'ApolloClient'
        // Replace the serverUrl with your GraphQL endpoint
        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl("https://agon-gym.myshopify.com/api/graphql")
                .okHttpClient(httpClient)
                .build();

        // ejecuto la mutation

        apolloClient.mutate(new CustomerCreateMutation(input)).enqueue(new ApolloCall.Callback<CustomerCreateMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<CustomerCreateMutation.Data> response) {

                Log.d("Apollo", "Data: " + response.getData().toString());

                    try{

                        if (response.getData().customerCreate.customerUserErrors.size() == 0) {
                            Log.d("Apollo", "Cuenta creada correctamente ");
                            generateToken();
                            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                            startActivity(intent);

                        } else if (response.getData().customerCreate.customerUserErrors.get(0).code.name().equals("TOO_SHORT")) {

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(SignUpActivity.this,
                                            "Contraseña demasiado corta", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    ;
                                }
                            });

                        }

                        else if (response.getData().customerCreate.customerUserErrors.get(0).code.name().equals("TAKEN")) {

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(SignUpActivity.this,
                                            "Usuario ya registrado", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    ;
                                }
                            });

                        }

                        else {
                            for (int i = 0; i < response.getData().customerCreate.customerUserErrors.size(); i++) {
                                Log.e("Apollo", "Ha habido otro un error: " + response.getData().customerCreate.customerUserErrors.get(i).code.name() + " : " +
                                        response.getData().customerCreate.customerUserErrors.get(i).message.toString());
                            }

                        }
                    }
                    catch(NullPointerException e){
                        Log.e("NullPointerException", "Error: " + e.getMessage());
                        for(int i=0; i<response.errors().size();i++){
                            Log.e("Apollo", "Server Error: " + response.errors().get(i).getMessage().toString());
                        }

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(SignUpActivity.this,
                                        "Se superó el límite de creación de clientes. Por favor, inténtalo de nuevo más tarde.", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                ;
                            }
                        });

                    }


            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.e("Apollo", "Error: " + e.getMessage());
            }
        });

    }
    
    public void generateToken(){


        CustomerAccessTokenCreateInput input = CustomerAccessTokenCreateInput.builder()
                .email(email.getText().toString())
                .password(pass.getText().toString())
                .build();

        final String[] queryResponse = {null};


        //OkHttpClient
        Apollo apolloObject = new Apollo();
        ApolloClient apolloClient = apolloObject.getApolloClient();


        // ejecuto la mutation

        apolloClient.mutate(new CustomerAccessTokenCreateMutation(input)).enqueue(new ApolloCall.Callback<CustomerAccessTokenCreateMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<CustomerAccessTokenCreateMutation.Data> response) {


                Log.e("Apollo", "Data: " + response.getData().toString());

                if(response.getData().customerAccessTokenCreate.customerAccessToken==null){

                    Log.e("Apollo", "Data:  USUARIO NO EXISTE");
                    //Toast.makeText(getApplicationContext(),"Hola",Toast.LENGTH_LONG);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignUpActivity.this,
                                    "El usuario NO existe", Toast.LENGTH_LONG).show();
                        }
                    });

                }else{
                    Log.e("Apollo", "Acess Token: " + response.getData().customerAccessTokenCreate.customerAccessToken.accessToken);
                    Log.e("Apollo", "Expires At: " + response.getData().customerAccessTokenCreate.customerAccessToken.expiresAt);

                    String token=response.getData().customerAccessTokenCreate.customerAccessToken.accessToken;
                    String expiresAt = response.getData().customerAccessTokenCreate.customerAccessToken.expiresAt.toString();

                    //Insercción BBDD Token de usuario


                    insertCustomerDataBase(token,expiresAt);

                    //

                    //COMPROBACION INSERCCIÓN (PROVISIONAL)
                    Cursor cursor = getContentResolver().query(DataContract.CustomerInternalClass.buildCartUri(),DataContract.CustomerInternalClass.ALL_FIELDS,null,null, null);
                    Log.e("El numero usuarios en BBDD son",""+cursor.getCount());
                    ///

                }
                x=1;

            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.e("Apollo", "Error: " + e.getMessage());
            }
        });

        while(x==0){}



    }

    private void insertCustomerDataBase(String token, String expiresAt) {


        customerFirstName = name.getText().toString();
        Log.e("SIGN UP",customerFirstName);
        customerLastName = lastName.getText().toString();
        Log.e("SIGN UP",customerLastName);
        customerEmail = email.getText().toString();
        Log.e("SIGN UP",customerEmail);


        //insercción BBDD CUSTOMER
        customerContentValues = new ContentValues();
        CustomerModel customerModel = new CustomerModel(token,expiresAt,customerFirstName,customerLastName,customerEmail);

        customerContentValues = DataContract.CustomerInternalClass.CustomerToContentValues(customerModel);
        Uri x =getContentResolver().insert(DataContract.CustomerInternalClass.buildCartUri(),customerContentValues);

        //





    }


}