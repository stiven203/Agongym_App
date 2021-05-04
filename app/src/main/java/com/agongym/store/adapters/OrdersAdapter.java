package com.agongym.store.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.agongym.store.database.DataContract;
import com.example.agongym.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class OrdersAdapter extends BaseAdapter{

    Cursor mCursor = null;
    private Context mContext;



    public OrdersAdapter(Context context, Cursor cursor){
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
            convertView = li.inflate(R.layout.order_content,null);
        }

        mCursor.moveToPosition(i);
        String name = mCursor.getString(mCursor.getColumnIndex(DataContract.OrderInternalClass.NAME));
        String price = mCursor.getString(mCursor.getColumnIndex(DataContract.OrderInternalClass.PRICE))+" €";
        String processedAt = mCursor.getString(mCursor.getColumnIndex(DataContract.OrderInternalClass.PROCESSED_AT));



        ((TextView)convertView.findViewById(R.id.order_name)).setText(name);
        ((TextView)convertView.findViewById(R.id.order_price)).setText(price);
        ((TextView)convertView.findViewById(R.id.order_processed_at)).setText(processedAt);


        return convertView;
    }




}