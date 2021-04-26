package com.agongym.store.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.database.Cursor;

import com.agongym.store.database.ProductOpenHelper;

public class ProductContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.agongym.store" ;

    public static final String PRODUCTS_TABLE = "products";
    public static final String IMAGES_TABLE = "images";
    public static final String VARIANTS_TABLE = "variants";
    public static final String CART_TABLE = "cart";
    public static final String CUSTOMER_TABLE = "customer";


    private ProductOpenHelper database;

    private static final int PRODUCTS = 100;
    private static final int IMAGES = 200;
    private static final int VARIANTS = 300;
    private static final int CART = 400;
    private static final int CUSTOMER = 500;

    public static final String PRODUCTS_BASE_PATH = "products";
    public static final String IMAGES_BASE_PATH = "images";
    public static final String VARIANTS_BASE_PATH = "variants";
    public static final String CART_BASE_PATH = "cart";
    public static final String CUSTOMER_BASE_PATH = "customer";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static
        {
            sURIMatcher.addURI(AUTHORITY, PRODUCTS_BASE_PATH, PRODUCTS);
            sURIMatcher.addURI(AUTHORITY, IMAGES_BASE_PATH, IMAGES);
            sURIMatcher.addURI(AUTHORITY, VARIANTS_BASE_PATH, VARIANTS);
            sURIMatcher.addURI(AUTHORITY, CART_BASE_PATH, CART);
            sURIMatcher.addURI(AUTHORITY, CUSTOMER_BASE_PATH, CUSTOMER);
        }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        int rowsDeleted=0;
        database = new ProductOpenHelper(getContext());
        SQLiteDatabase sqlDB = database.getWritableDatabase();

        switch (uriType) {
            case PRODUCTS:
                rowsDeleted = sqlDB.delete(ProductOpenHelper.PRODUCTS_TABLE,selection,selectionArgs);
                break;
            case IMAGES:
                rowsDeleted = sqlDB.delete(ProductOpenHelper.IMAGES_TABLE,selection,selectionArgs);
                break;
            case VARIANTS:
                rowsDeleted = sqlDB.delete(ProductOpenHelper.VARIANTS_TABLE,selection,selectionArgs);
                break;
            case CART:
                rowsDeleted = sqlDB.delete(ProductOpenHelper.CART_TABLE,selection,selectionArgs);
                break;
            case CUSTOMER:
                rowsDeleted = sqlDB.delete(ProductOpenHelper.CUSTOMER_TABLE,selection,selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        String basePath;

        int uriType = sURIMatcher.match(uri);
        database = new ProductOpenHelper(getContext());
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id;

        switch (uriType) {
            case PRODUCTS:
                id = sqlDB.insert(ProductOpenHelper.PRODUCTS_TABLE, null, values);
                basePath = PRODUCTS_BASE_PATH;
                break;
            case IMAGES:
                id = sqlDB.insert(ProductOpenHelper.IMAGES_TABLE, null, values);
                basePath=IMAGES_BASE_PATH;
                break;
            case VARIANTS:
                id = sqlDB.insert(ProductOpenHelper.VARIANTS_TABLE, null, values);
                basePath = VARIANTS_BASE_PATH;
                break;
            case CART:
                id = sqlDB.insert(ProductOpenHelper.CART_TABLE, null, values);
                basePath = CART_BASE_PATH;
                break;
            case CUSTOMER:
                id = sqlDB.insert(ProductOpenHelper.CUSTOMER_TABLE, null, values);
                basePath = CUSTOMER_BASE_PATH;
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(basePath + "/" + id);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        int uriType = sURIMatcher.match(uri);
        database = new ProductOpenHelper(getContext());
        SQLiteDatabase sqlDB = database.getWritableDatabase();

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        //queryBuilder.setTables(ProductOpenHelper.PRODUCTS_TABLE);

        switch (uriType) {
            case PRODUCTS:
                queryBuilder.setTables(ProductOpenHelper.PRODUCTS_TABLE);
                break;
            case IMAGES:
                queryBuilder.setTables(ProductOpenHelper.IMAGES_TABLE);
                break;
            case VARIANTS:
                queryBuilder.setTables(ProductOpenHelper.VARIANTS_TABLE);
                break;
                case CART:
                queryBuilder.setTables(ProductOpenHelper.CART_TABLE);
                break;
            case CUSTOMER:
                queryBuilder.setTables(ProductOpenHelper.CUSTOMER_TABLE);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null,
                null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        int rowsUpdated=0;
        database = new ProductOpenHelper(getContext());
        SQLiteDatabase sqlDB = database.getWritableDatabase();

        switch (uriType) {
            case PRODUCTS:
                rowsUpdated = sqlDB.update(ProductOpenHelper.PRODUCTS_TABLE, values,selection,selectionArgs);
                break;
            case IMAGES:
                rowsUpdated = sqlDB.update(ProductOpenHelper.IMAGES_TABLE, values,selection,selectionArgs);
                break;
            case VARIANTS:
                rowsUpdated = sqlDB.update(ProductOpenHelper.VARIANTS_TABLE, values,selection,selectionArgs);
                break;
            case CART:
                rowsUpdated = sqlDB.update(ProductOpenHelper.CART_TABLE, values,selection,selectionArgs);
                break;
            case CUSTOMER:
                rowsUpdated = sqlDB.update(ProductOpenHelper.CUSTOMER_TABLE, values,selection,selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }


}


