package com.agongym.store.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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
    int result = 0;
    Intent intent;
    ImageView cartIcon;
    int nProducts=0;
    String number = "+34657992807";
    boolean installed = false;

    TextView customerNameTextView;
    String customerName = "";

    SearchView mSearchView;

    NavigationView navigationView;
    boolean loginHide = false;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkLogin();


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

    private void checkLogin() {

        Cursor cursor =  getContentResolver().query(DataContract.CustomerInternalClass.buildCartUri(), DataContract.CustomerInternalClass.ALL_FIELDS,null,
                null,null);

        Log.e("HOME MESSAGE", "LLEGA AQUI");

        if(cursor.getCount()>0 && !loginHide){
            Log.e("HOME MESSAGE", "USUARIO  LOGGEADO");
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu nav_login = navigationView.getMenu();
            nav_login.findItem(R.id.nav_log_in).setVisible(false);

            loginHide=true;



        }

        else {
            Log.e("HOME MESSAGE", "USUARIO NO LOGGEADO");
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu nav_logout = navigationView.getMenu();
            Menu nav_account = navigationView.getMenu();

            nav_logout.findItem(R.id.nav_log_out).setVisible(false);
            nav_account.findItem(R.id.nav_account).setVisible(false);
        }










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



        //icono carrito
        cartIcon = (ImageView) view.findViewById(R.id.cart_icon);
        cartIcon.setOnClickListener(new View.OnClickListener() {
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

        //PRUEBA

        MenuItem menuItem = menu.findItem(R.id.search_product);

        mSearchView=(SearchView) menuItem.getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Toast.makeText(getApplicationContext(),"Se ha escrito: "+query, Toast.LENGTH_LONG).show();


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(getApplicationContext(),"Se ha escrito: "+newText, Toast.LENGTH_LONG).show();
                return false;
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

        Log.e("HOME MESSAGE", "Seleccionado ID: "+item.getItemId());

        if(item.getGroupId()== R.id.nav_log_out){
            Log.e("HOME MESSAGE", "Seleccionado!!!!!! ");
        }

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Log.e("HOME MESSAGE", "Seleccionado ID: "+item.getItemId());

                return false;
            }
        });

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.whatsapp_menu){
            //Toast.makeText(getApplicationContext(), "¡Pulsado botón de WHATSAPP!", Toast.LENGTH_LONG).show();

            installed = isWhatsappInstalled("com.whatsapp");

            if(installed){
                Intent intent =  new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+number));
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(), "WhatsApp no está instalado!", Toast.LENGTH_LONG).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isWhatsappInstalled(String s) {
        PackageManager packageManager = getPackageManager();
        boolean isInstalled;

        try{
            packageManager.getPackageInfo(s, PackageManager.GET_ACTIVITIES);
            isInstalled=true;
        } catch (PackageManager.NameNotFoundException e) {
            isInstalled=false;
            e.printStackTrace();
        }

        return isInstalled;



    }

    @Override
    protected void onStart() {
        super.onStart();

        //PRUEBA LOG OUT

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Logout = navigationView.getMenu();



        nav_Logout.findItem(R.id.nav_log_out).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //Log.e("HOME MESSAGE", "Botón clickado");

                //result = getApplicationContext().getContentResolver().delete(DataContract.CustomerInternalClass.buildCartUri(),null);
                //Log.e("HOME MESSAGE", "Numero de usuarios borrados: "+result);

                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(intent);
                finish();

                return false;
            }
        });

        //


    }
}