package com.agongym.store.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.agongym.store.activities.type.CustomerAccessTokenCreateInput;
import com.agongym.store.database.DataContract;
import com.agongym.store.database.models.CartModel;
import com.agongym.store.database.models.CustomerModel;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.agongym.R;

import org.jetbrains.annotations.NotNull;

import okhttp3.OkHttpClient;


public class LogInActivity extends AppCompatActivity {

    Button login_btn, signup_btn;
    TextView textView;

    EditText email, pass;
    Intent intent;

    ContentValues customerContentValues;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Poner Custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_title);
        setContentView(R.layout.activity_log_in);

        email= (EditText) findViewById(R.id.editTextTextHomeEmail);
        pass= (EditText) findViewById(R.id.editTextTextHomePassword);
        login_btn = (Button) findViewById(R.id.login_button);
        signup_btn = (Button) findViewById(R.id.signuphome_button);
        textView = (TextView)findViewById(R.id.skip_text_view);





        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //datos de los edit text
                String correo = email.getText().toString();
                String contrasena = pass.getText().toString();

                //String correo = "stiven203@hotmail.com";
                //String contrasena = "1234abcd";

                if(correo.equals("")&contrasena.equals("")){

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LogInActivity.this,
                                    "Introduzca correo y contraseña", Toast.LENGTH_LONG).show();
                        }
                    });

                }

                else if(correo.equals("")){

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LogInActivity.this,
                                    "Introduzca el correo", Toast.LENGTH_LONG).show();
                        }
                    });

                }else if(contrasena.equals("")){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LogInActivity.this,
                                    "Introduzca la contraseña", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{
                    CustomerAccessTokenCreateInput input = CustomerAccessTokenCreateInput.builder()
                            .email(correo)
                            .password(contrasena)
                            .build();

                    logIn(input);
                }



            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getBaseContext(), SignUpActivity.class);
                //intent = new Intent(getBaseContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });



        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getBaseContext(), HomeActivity.class);
                startActivity(intent);
            }
        });






    }

    @Override
    protected void onStart(){
        super.onStart();


    }

    private void logIn(CustomerAccessTokenCreateInput input) {

        final String[] queryResponse = {null};


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


        // Create an `ApolloClient`
        // Replace the serverUrl with your GraphQL endpoint
        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl("https://agon-gym.myshopify.com/api/graphql")
                .okHttpClient(httpClient)
                .build();


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
                            Toast.makeText(LogInActivity.this,
                                    "El usuario NO existe", Toast.LENGTH_LONG).show();
                        }
                    });

                }else{
                    Log.e("Apollo", "Acess Token: " + response.getData().customerAccessTokenCreate.customerAccessToken.accessToken);
                    Log.e("Apollo", "Expires At: " + response.getData().customerAccessTokenCreate.customerAccessToken.expiresAt);

                    //Insercción BBDD Token de usuario

                    customerContentValues = new ContentValues();
                    CustomerModel customerModel = new CustomerModel(response.getData().customerAccessTokenCreate.customerAccessToken.accessToken,
                            response.getData().customerAccessTokenCreate.customerAccessToken.expiresAt.toString());

                    customerContentValues = DataContract.CustomerInternalClass.CustomerToContentValues(customerModel);
                    Uri x =getContentResolver().insert(DataContract.CustomerInternalClass.buildCartUri(),customerContentValues);
                    //

                    //COMPROBACION INSERCCIÓN (PROVISIONAL)
                    Cursor cursor = getContentResolver().query(DataContract.CustomerInternalClass.buildCartUri(),DataContract.CustomerInternalClass.ALL_FIELDS,null,null, null);
                    Log.e("El numero usuarios en BBDD son",""+cursor.getCount());
                    ///


                    intent = new Intent(getBaseContext(), HomeActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.e("Apollo", "Error: " + e.getMessage());
            }
        });


    }

}