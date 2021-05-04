package com.agongym.store.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.agongym.store.database.DataContract;
import com.example.agongym.R;
import com.google.android.material.navigation.NavigationView;
import com.nex3z.notificationbadge.NotificationBadge;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    NotificationBadge badge;
    Cursor cursor;
    Intent intent;
    ImageView cart_icon;
    int nProducts=0;

    TextView customerNameTextView;
    String customerName = "";

    SearchView mSearchView;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_man, R.id.nav_women, R.id.nav_accessories, R.id.nav_main, R.id.nav_outlet, R.id.nav_account)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        updateCustomerNameInfo();
    }

    private void updateCustomerNameInfo() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        customerNameTextView = (TextView) headerView.findViewById(R.id.customerNameTV);



        Cursor cursor = getContentResolver().query(DataContract.CustomerInternalClass.buildCartUri(),DataContract.CustomerInternalClass.ALL_FIELDS,null,null, null);
        Log.e("El numero usuarios en BBDD son",""+cursor.getCount());

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            customerName = cursor.getString(cursor.getColumnIndex(DataContract.CustomerInternalClass.FIRST_NAME));

            Log.e("Customer Name",customerName);


        }


        if(customerNameTextView!=null)
        {
            customerNameTextView.setText(customerName);
        }







    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);

        //numero de articulos carrito
        View view = menu.findItem(R.id.cart_menu).getActionView();
        badge = (NotificationBadge)view.findViewById(R.id.badge);
        updateCartNumber();



        //test search
        mSearchView=(SearchView) findViewById(R.id.search_product);


        //icono carrito
        cart_icon = (ImageView) view.findViewById(R.id.cart_icon);
        cart_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(nProducts!=0){
                    intent = new Intent(HomeActivity.this, CartActivity.class);
                    HomeActivity.this.startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "¡Carrito Vacio!", Toast.LENGTH_LONG).show();
                }


            }
        });



        return true;
    }

    //funcion test
    private void setUpSearchView() {

        mSearchView.setIconified(true);
        mSearchView.setOnQueryTextListener((SearchView.OnQueryTextListener) this);
        mSearchView.setSubmitButtonEnabled(true);
        //mSearchView.setQueryHint("Search Here");
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void updateCartNumber() {
        if(badge == null){
            return;
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Consulta
                cursor = getContentResolver().query(DataContract.CartInternalClass.buildCartUri(),DataContract.CartInternalClass.ALL_FIELDS,null,null, null);
                Log.e("El numero de productos añadidos al carrito hasta ahora son",""+cursor.getCount());

                if(cursor.getCount()==0){
                    badge.setVisibility(View.INVISIBLE);
                }
                else{
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(String.valueOf(cursor.getCount()));
                    nProducts=cursor.getCount();
                }
            }
        });



    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateCartNumber();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}