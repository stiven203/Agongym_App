package com.agongym.store.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.agongym.store.activities.HomeActivity;
import com.agongym.store.activities.ProductQuery;
import com.agongym.store.database.DataContract;
import com.example.agongym.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ProductsAdapter extends BaseAdapter implements Filterable {

    Cursor mCursor = null;
    private Context mContext;

    Cursor auxCursor;

    public ProductsAdapter(Context context, Cursor cursor){
        this.mCursor=cursor;
        this.mContext=context;

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
        if (convertView == null){

            LayoutInflater li = LayoutInflater.from(mContext);
            convertView = li.inflate(R.layout.product_content,null);
        }

        mCursor.moveToPosition(i);
        String productTitle = mCursor.getString(mCursor.getColumnIndex(DataContract.ProductInternalClass.TITLE));
        String productPrice = mCursor.getString(mCursor.getColumnIndex(DataContract.ProductInternalClass.MAX_VARIANT_PRICE))+" €";
        String mainImage = mCursor.getString(mCursor.getColumnIndex(DataContract.ProductInternalClass.MAIN_IMAGE));

        ((TextView)convertView.findViewById(R.id.product_title)).setText(productTitle);
        ((TextView)convertView.findViewById(R.id.product_price)).setText(productPrice);

        //Picasso.with(mContext).load(mCursor.getColumnIndex(DataContract.ProductInternalClass.MAIN_IMAGE)).into((ImageView)convertView.findViewById(R.id.imageview_product_image));
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageview_product_image);
        Picasso.get().load(mainImage).into(imageView);


        return convertView;
    }


    //test search
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                Log.e("HA BUSCADO:",constraint.toString());

                final FilterResults oReturn = new FilterResults();

                String[] selectionArgs = {"MEN",constraint.toString()};
                String selection = DataContract.ProductInternalClass.COLLECTION + " = ?" + " AND "+DataContract.ProductInternalClass.TITLE + " = ?";

                auxCursor =  mContext.getContentResolver().query(DataContract.ProductInternalClass.buildProductUri(), DataContract.ProductInternalClass.ALL_FIELDS,selection,
                        selectionArgs,null);



                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {

                notifyDataSetChanged();
            }
        };
    }




}
