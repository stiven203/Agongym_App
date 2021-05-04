package com.agongym.store.database;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class ProductOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "products.db";
    public static final int DATABASE_VERSION = 15;

    //TABLE NAMES
    public static final String PRODUCTS_TABLE = "PRODUCTS";
    public static final String IMAGES_TABLE = "IMAGES";
    public static final String VARIANTS_TABLE = "VARIANTS";
    public static final String CART_TABLE = "CART";
    public static final String CUSTOMER_TABLE = "CUSTOMER";
    public static final String ORDERS_TABLE = "ORDERS";

    //CREATE PRODUCTS TABLE
    public static final String CREATE_PRODUCTS_TABLE = "create table "
            + PRODUCTS_TABLE + "( "
            + DataContract.ProductInternalClass._ID + " integer primary key autoincrement, "
            + DataContract.ProductInternalClass.ID + " integer not null, "
            + DataContract.ProductInternalClass.TITLE + " text, "
            + DataContract.ProductInternalClass.AVAILABLE_FOR_SALE + " text, "
            + DataContract.ProductInternalClass.DESCRIPTION + " text, "
            + DataContract.ProductInternalClass.PRODUCT_TYPE + " text, "
            + DataContract.ProductInternalClass.MAIN_IMAGE + " text, "
            + DataContract.ProductInternalClass.MAX_VARIANT_PRICE + " text, "
            + DataContract.ProductInternalClass.COLLECTION + " text "
            + ");";



    //CREATE IMAGES TABLE
    public static final String CREATE_IMAGES_TABLE = "create table "
            + IMAGES_TABLE + "( "
            + DataContract.ImageInternalClass._ID + " integer primary key autoincrement, "
            + DataContract.ImageInternalClass.ID + " integer not null, "
            + DataContract.ImageInternalClass.PRODUCT_ID + " integer not null, "
            + DataContract.ImageInternalClass.ORIGINAL_SRC + " text "
            + ");";

    //CREATE VARIANTS TABLE
    public static final String CREATE_VARIANTS_TABLE = "create table "
            + VARIANTS_TABLE + "( "
            + DataContract.VariantInternalClass._ID + " integer primary key autoincrement, "
            + DataContract.VariantInternalClass.ID + " integer not null, "
            + DataContract.VariantInternalClass.PRODUCT_ID + " integer not null, "
            + DataContract.VariantInternalClass.AVAILABLE_FOR_SALE + " text, "
            + DataContract.VariantInternalClass.SKU + " text, "
            + DataContract.VariantInternalClass.TITLE + " text, "
            + DataContract.VariantInternalClass.PRICE + " text, "
            + DataContract.VariantInternalClass.COMPARE_AT_PRICE + " text "
            + ");";

    //CREATE CART TABLE
    public static final String CREATE_CART_TABLE = "create table "
            + CART_TABLE + "( "
            + DataContract.CartInternalClass._ID + " integer primary key autoincrement, "
            + DataContract.CartInternalClass.PRODUCT_ID + " integer not null, "
            + DataContract.CartInternalClass.VARIANT_ID + " integer not null, "
            + DataContract.CartInternalClass.QUANTITY + " integer not null, "
            + DataContract.CartInternalClass.VARIANT_PRICE + " integer not null "
            + ");";


    //CREATE CUSTOMER TABLE
    public static final String CREATE_CUSTOMER_TABLE = "create table "
            + CUSTOMER_TABLE + "( "
            + DataContract.CartInternalClass._ID + " integer primary key autoincrement, "
            + DataContract.CustomerInternalClass.ACCESS_TOKEN + " text not null, "
            + DataContract.CustomerInternalClass.ACCESS_TOKEN_EXPIRES_AT + " text not null, "
            + DataContract.CustomerInternalClass.FIRST_NAME + " text, "
            + DataContract.CustomerInternalClass.LAST_NAME + " text, "
            + DataContract.CustomerInternalClass.EMAIL + " text "

            + ");";

    //CREATE ORDERS TABLE
    public static final String CREATE_ORDERS_TABLE = "create table "
            + ORDERS_TABLE + "( "
            + DataContract.OrderInternalClass._ID + " integer primary key autoincrement, "
            + DataContract.OrderInternalClass.CUSTOMER_ACCESS_TOKEN + " text not null, "
            + DataContract.OrderInternalClass.NAME + " text, "
            + DataContract.OrderInternalClass.PRICE + " text, "
            + DataContract.OrderInternalClass.PROCESSED_AT + " text "

            + ");";



    public ProductOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_PRODUCTS_TABLE);
        db.execSQL(CREATE_IMAGES_TABLE);
        db.execSQL(CREATE_VARIANTS_TABLE);
        db.execSQL(CREATE_CART_TABLE);
        db.execSQL(CREATE_CUSTOMER_TABLE);
        db.execSQL(CREATE_ORDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + IMAGES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + VARIANTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CART_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CUSTOMER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ORDERS_TABLE);

        //onCreate(db);
        db.execSQL(CREATE_PRODUCTS_TABLE);
        db.execSQL(CREATE_IMAGES_TABLE);
        db.execSQL(CREATE_VARIANTS_TABLE);
        db.execSQL(CREATE_CART_TABLE);
        db.execSQL(CREATE_CUSTOMER_TABLE);
        db.execSQL(CREATE_ORDERS_TABLE);
    }

}
