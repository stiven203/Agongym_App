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

import com.agongym.store.utils.Apollo;
import com.agongym.store.activities.type.CustomerAccessTokenCreateInput;
import com.agongym.store.database.DataContract;
import com.agongym.store.database.models.CustomerModel;
import com.agongym.store.database.models.OrderModel;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.agongym.R;

import org.jetbrains.annotations.NotNull;


public class LogInActivity extends AppCompatActivity {

    Button login_btn, signup_btn;
    TextView textView;
    int i=0;
    EditText email, pass;
    Intent intent;

    String firstName = "";
    String lastName = "";
    String customerEmail = "";

    ContentValues customerContentValues;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        i=0;

        //Poner Custom toolbar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_content);
        //


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


        //Apollo
        Apollo apolloObject =  new Apollo();
        ApolloClient apolloClient  = apolloObject.getApolloClient();

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

                    String token=response.getData().customerAccessTokenCreate.customerAccessToken.accessToken;
                    String expiresAt = response.getData().customerAccessTokenCreate.customerAccessToken.expiresAt.toString();

                    //Insercción BBDD Token de usuario
                    insertCustomerDataBase(token,expiresAt);
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

    private void insertCustomerDataBase(String token, String expiresAt) {

        final CustomerQuery.Orders[] orders = {null};

        final boolean[] userOrders = {false};

        //Apollo Client
        final Apollo[] apolloObject = {new Apollo()};
        ApolloClient apolloClient = apolloObject[0].getApolloClient();
        //

        //Pedir al servidor nombre y apellidos del cliente
        apolloClient.query(new CustomerQuery(token)).enqueue(new ApolloCall.Callback<CustomerQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<CustomerQuery.Data> response) {


                firstName = response.getData().customer.firstName;
                lastName = response.getData().customer.lastName;
                customerEmail = response.getData().customer.email;

                /*
                if(response.getData().customer.orders.edges.size()!=0){

                    orders[0] = response.getData().customer.orders;
                    userOrders[0] = true;

                }
                else {
                    Log.e("ORDER INFO","Este usuario no tiene pedidos");
                }

                 */




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


        //insercción BBDD Customer
        customerContentValues = new ContentValues();
        CustomerModel customerModel = new CustomerModel(token,expiresAt,firstName,lastName,customerEmail);

        customerContentValues = DataContract.CustomerInternalClass.CustomerToContentValues(customerModel);
        Uri x =getContentResolver().insert(DataContract.CustomerInternalClass.buildCartUri(),customerContentValues);

        //

        //insercción BBDD Orders

        /*



        String auxDate = "";
        String date = "";

        if(userOrders[0]!=false){
            ContentValues orderContentValues[] = new ContentValues[orders[0].edges.size()];

            for(int i = 0; i< orders[0].edges.size(); i++){

                date = orders[0].edges.get(i).node.processedAt.toString();
                auxDate = date.substring(0,4)+"-"+date.substring(5,7)+"-"+date.substring(8,10);

                OrderModel orderModel = new OrderModel(token, orders[0].edges.get(i).node.name,
                        orders[0].edges.get(i).node.totalPriceV2.amount.toString(),auxDate);

                orderContentValues[i] = DataContract.OrderInternalClass.OrderToContentValues(orderModel);
            }

            Integer p =getContentResolver().bulkInsert(DataContract.OrderInternalClass.buildCartUri(), orderContentValues);
            Log.d("Numero de Insercciones de ORDERS", Integer.toString(p) );



        }
        else {
            Log.e("ORDER INFO","Este usuario no tiene pedidos");
        }

         */

        //




    }

}