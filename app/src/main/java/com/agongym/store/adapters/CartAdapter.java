package com.agongym.store.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agongym.store.database.DataContract;
import com.example.agongym.R;
import com.squareup.picasso.Picasso;

public class CartAdapter extends BaseAdapter{

    Cursor mCursor = null;
    private Context mContext;

    String selection;


    ImageView productImage;
    TextView productNameTV;
    TextView productPriceTV;
    TextView productSizeTV;

    Button cart_plus_butt;
    Button cart_minus_butt;
    Button cart_remove_butt;
    TextView productQuantityTV;

    View mainView;


    public CartAdapter(Context context, Cursor cursor){
        this.mCursor=cursor;
        this.mContext=context;

    }

    public void updateView(Cursor cursor) {
        mCursor=cursor;
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (mCursor==null) return 0;
        return mCursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        Log.e("valor de get cursor",""+mCursor.getCount());
        Log.e("valor de i",""+i);

        if (convertView == null){

            LayoutInflater li = LayoutInflater.from(mContext);
            convertView = li.inflate(R.layout.cart_content,null);
        }

        mainView = convertView;

        productImage = (ImageView) convertView.findViewById(R.id.cart_product_image);
        productNameTV = (TextView)convertView.findViewById(R.id.cart_product_name);
        productSizeTV = (TextView)convertView.findViewById(R.id.cart_product_size);
        productPriceTV = (TextView) convertView.findViewById(R.id.cart_product_price);
        cart_plus_butt = (Button)convertView.findViewById(R.id.cart_plus_butt);
        cart_minus_butt = (Button)convertView.findViewById(R.id.cart_minus_butt);
        cart_remove_butt = (Button)convertView.findViewById(R.id.cart_remove_butt);
        productQuantityTV = (TextView)convertView.findViewById(R.id.cart_product_quantity);

        Cursor auxCursor;

        int itemQuantity=0;


        //Datos directos
        mCursor.moveToPosition(i);
        String productQuantity="";
        productQuantity= mCursor.getString(mCursor.getColumnIndex(DataContract.CartInternalClass.QUANTITY));


        itemQuantity = Integer.parseInt(productQuantity);



        //Consulta datos Producto
        String productId= mCursor.getString(mCursor.getColumnIndex(DataContract.CartInternalClass.PRODUCT_ID));
        selection = DataContract.ProductInternalClass.ID+ " = ?";
        String[] productSelectionArgs = {productId};
        auxCursor = mContext.getContentResolver().query(DataContract.ProductInternalClass.buildProductUri(),DataContract.ProductInternalClass.ALL_FIELDS,selection,productSelectionArgs, null);
        int count = auxCursor.getCount();
        Log.e("COUNT values ",""+count);
        auxCursor.moveToFirst();
        String mainImage = auxCursor.getString(auxCursor.getColumnIndex(DataContract.ProductInternalClass.MAIN_IMAGE));
        String productName = auxCursor.getString(auxCursor.getColumnIndex(DataContract.ProductInternalClass.TITLE));
        String productPrice = auxCursor.getString(auxCursor.getColumnIndex(DataContract.ProductInternalClass.MAX_VARIANT_PRICE))+" €";



        //Consulta datos Variante
        String variantId = mCursor.getString(mCursor.getColumnIndex(DataContract.CartInternalClass.VARIANT_ID));
        selection = DataContract.VariantInternalClass.ID+ " = ?";
        String[] variantSelectionArgs = {variantId};
        auxCursor = mContext.getContentResolver().query(DataContract.VariantInternalClass.buildVariantUri(),DataContract.VariantInternalClass.ALL_FIELDS,selection,variantSelectionArgs, null);
        auxCursor.moveToFirst();
        String variantSize = auxCursor.getString(auxCursor.getColumnIndex(DataContract.ProductInternalClass.TITLE));


        Picasso.get().load(mainImage).into(productImage);
        productNameTV.setText(productName);
        productPriceTV.setText(productPrice);
        productSizeTV.setText(variantSize);
        productQuantityTV.setText(productQuantity);

        BaseAdapter va  = this;

        cart_minus_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout vwParentRow = (LinearLayout)v.getParent();
                int position=(Integer) v.getTag();
                Log.e("Position",""+position);
                int value=-1;
                updateElementDataBase(position, value);



                //notifyDataSetChanged();

            }
        });
        cart_plus_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LinearLayout vwParentRow = (LinearLayout)v.getParent();
                int position=(Integer) v.getTag();
                Log.e("Position",""+position);
                int value=1;
                updateElementDataBase(position, value);



            }
        });

        cart_remove_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LinearLayout vwParentRow = (LinearLayout)v.getParent();
                int position=(Integer) v.getTag();
                Log.e("Position",""+position);
                int value=1;
                removeElementDataBase(position);



            }
        });



        //Asignar a los botones su posicion en la lista para luego poder recuperarla
        convertView.findViewById(R.id.cart_minus_butt).setTag(i);
        convertView.findViewById(R.id.cart_plus_butt).setTag(i);
        convertView.findViewById(R.id.cart_remove_butt).setTag(i);
       //

        return convertView;
    }

    private void removeElementDataBase(int pos) {

        int count;

        mCursor.moveToPosition(pos);
        String cartId= mCursor.getString(mCursor.getColumnIndex(DataContract.CartInternalClass._ID));


        selection = DataContract.CartInternalClass._ID+ " = ?";
        String[] selectionArgs = {cartId};
        ContentValues values = new ContentValues();

        count = mContext.getContentResolver().delete(DataContract.CartInternalClass.buildCartUri(),selection,selectionArgs);

        Log.e("PRODUCTOS BORRADOS",""+count);

        Cursor auxCursor =  mContext.getContentResolver().query(DataContract.CartInternalClass.buildCartUri(), DataContract.CartInternalClass.ALL_FIELDS,null,
                null,null);
        updateView(auxCursor);


    }

    private void updateElementDataBase(int pos, int value) {
        int count;
        mCursor.moveToPosition(pos);
        String currentQuantity="";
        currentQuantity= mCursor.getString(mCursor.getColumnIndex(DataContract.CartInternalClass.QUANTITY));
        int quantity=0;
        quantity = Integer.parseInt(currentQuantity);

        if(quantity==1 && value==-1){
            value=0;

        }

        quantity=quantity+value;

        String cartId= mCursor.getString(mCursor.getColumnIndex(DataContract.CartInternalClass._ID));


        selection = DataContract.CartInternalClass._ID+ " = ?";
        String[] selectionArgs = {cartId};
        ContentValues values = new ContentValues();
        values.put(DataContract.CartInternalClass.QUANTITY,String.valueOf(quantity));
        count = mContext.getContentResolver().update(DataContract.CartInternalClass.buildCartUri(),values, selection,selectionArgs);

        Log.e("PRODUCTOS ACTUALIZADOS",""+count);

        Cursor auxCursor =  mContext.getContentResolver().query(DataContract.CartInternalClass.buildCartUri(), DataContract.CartInternalClass.ALL_FIELDS,null,
                null,null);
        updateView(auxCursor);
    }
}
