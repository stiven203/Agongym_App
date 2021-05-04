package com.agongym.store.database;

import android.content.ContentValues;
import android.provider.BaseColumns;
import android.net.Uri;

import com.agongym.store.database.models.CartModel;
import com.agongym.store.database.models.CustomerModel;
import com.agongym.store.database.models.ImageModel;
import com.agongym.store.database.models.OrderModel;
import com.agongym.store.database.models.ProductModel;
import com.agongym.store.database.models.VariantModel;

public class DataContract {

    public static final String CONTENT_AUTHORITY = ProductContentProvider.AUTHORITY;
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    interface ProductFields
    {

        String ID = "ID";
        String TITLE = "TITLE";
        String AVAILABLE_FOR_SALE = "AVAILABLE_FOR_SALE";
        String DESCRIPTION = "DESCRIPTION";
        String PRODUCT_TYPE = "PRODUCT_TYPE";
        String MAIN_IMAGE = "MAIN_IMAGE";
        String MAX_VARIANT_PRICE = "MAX_VARIANT_PRICE";
        String COLLECTION = "COLLECTION";


    }

    interface ImageFields
    {
        String ID = "ID";
        String PRODUCT_ID = "PRODUCT_ID";
        String ORIGINAL_SRC = "ORIGINAL_SRC";
    }

    interface VariantFields
    {

        String ID = "ID";
        String PRODUCT_ID = "PRODUCT_ID";
        String AVAILABLE_FOR_SALE = "AVAILABLE_FOR_SALE";
        String SKU = "SKU";
        String TITLE = "TITLE";
        String PRICE = "PRICE";
        String COMPARE_AT_PRICE = "COMPARE_AT_PRICE";


    }

    interface CartFields
    {
            String PRODUCT_ID = "PRODUCT_ID";
            String VARIANT_ID = "VARIANT_ID";
            String QUANTITY = "QUANTITY";
            String VARIANT_PRICE = "VARIANT_PRICE";

    }

    interface CustomerFields
    {
        String ACCESS_TOKEN = "ACCESS_TOKEN";
        String ACCESS_TOKEN_EXPIRES_AT = "ACCESS_TOKEN_EXPIRES_AT";
        String FIRST_NAME = "FIRST_NAME";
        String LAST_NAME = "LAST_NAME";
        String EMAIL = "EMAIL";


    }

    interface OrderFields
    {

        String CUSTOMER_ACCESS_TOKEN = "CUSTOMER_ACCESS_TOKEN";
        String NAME = "NAME";
        String PRICE = "PRICE";
        String PROCESSED_AT = "PROCESSED_AT";

    }

    public static class ProductInternalClass implements ProductFields, BaseColumns
    {
        public static String ALL_FIELDS[] =
                {
                        ProductInternalClass._ID,
                        ProductInternalClass.ID,
                        ProductInternalClass.TITLE,
                        ProductInternalClass.AVAILABLE_FOR_SALE,
                        ProductInternalClass.DESCRIPTION,
                        ProductInternalClass.PRODUCT_TYPE,
                        ProductInternalClass.MAIN_IMAGE,
                        ProductInternalClass.MAX_VARIANT_PRICE
                };


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(ProductContentProvider.PRODUCTS_TABLE).build();

        /** for all Products */

        public static Uri buildProductUri() {
            return CONTENT_URI;
        }


        public static ContentValues ProductToContentValues(ProductModel productModel)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ProductInternalClass.ID, productModel.getId());
            contentValues.put(ProductInternalClass.TITLE, productModel.getTitle());
            contentValues.put(ProductInternalClass.AVAILABLE_FOR_SALE, productModel.getAvailableForSale());
            contentValues.put(ProductInternalClass.DESCRIPTION, productModel.getDescription());
            contentValues.put(ProductInternalClass.PRODUCT_TYPE, productModel.getProductType());
            contentValues.put(ProductInternalClass.MAIN_IMAGE, productModel.getMainImage());
            contentValues.put(ProductInternalClass.MAX_VARIANT_PRICE, productModel.getMaxVariantPrice());
            contentValues.put(ProductInternalClass.COLLECTION, productModel.getCollection());

            return  contentValues;
        }

    }

    public static class ImageInternalClass implements ImageFields, BaseColumns
    {
        public static String ALL_FIELDS[] =
                {
                        ImageInternalClass._ID,
                        ImageInternalClass.ID,
                        ImageInternalClass.PRODUCT_ID,
                        ImageInternalClass.ORIGINAL_SRC

                };

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(ProductContentProvider.IMAGES_TABLE).build();

        /** for all Images */

        public static Uri buildImageUri() {
            return CONTENT_URI;
        }


        public static ContentValues ImageToContentValues(ImageModel imageModel)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ImageInternalClass.ID, imageModel.getId());
            contentValues.put(ImageInternalClass.PRODUCT_ID, imageModel.getProductId());
            contentValues.put(ImageInternalClass.ORIGINAL_SRC, imageModel.getOriginalSrc());
            return  contentValues;
        }

    }

    public static class VariantInternalClass implements VariantFields, BaseColumns
    {
        public static String ALL_FIELDS[] =
                {
                        VariantInternalClass._ID,
                        VariantInternalClass.ID,
                        VariantInternalClass.PRODUCT_ID,
                        VariantInternalClass.AVAILABLE_FOR_SALE,
                        VariantInternalClass.SKU,
                        VariantInternalClass.TITLE,
                        VariantInternalClass.PRICE,
                        VariantInternalClass.COMPARE_AT_PRICE

                };

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(ProductContentProvider.VARIANTS_TABLE).build();

        /** for all Variants */

        public static Uri buildVariantUri() {
            return CONTENT_URI;
        }


        public static ContentValues VariantToContentValues(VariantModel variantModel)
        {
            ContentValues contentValues = new ContentValues();

            contentValues.put(VariantInternalClass.ID, variantModel.getId());
            contentValues.put(VariantInternalClass.PRODUCT_ID, variantModel.getProductId());
            contentValues.put(VariantInternalClass.AVAILABLE_FOR_SALE, variantModel.getAvailableForSale());
            contentValues.put(VariantInternalClass.SKU, variantModel.getSku());
            contentValues.put(VariantInternalClass.TITLE, variantModel.getTitle());
            contentValues.put(VariantInternalClass.PRICE, variantModel.getPrice());
            contentValues.put(VariantInternalClass.COMPARE_AT_PRICE, variantModel.getCompareAtPrice());

            return  contentValues;
        }

    }

    public static class CartInternalClass implements CartFields, BaseColumns
    {
        public static String ALL_FIELDS[] =
                {
                        CartInternalClass._ID,
                        CartInternalClass.PRODUCT_ID,
                        CartInternalClass.VARIANT_ID,
                        CartInternalClass.QUANTITY,
                        CartInternalClass.VARIANT_PRICE

                };

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(ProductContentProvider.CART_TABLE).build();

        /** for all Variants */

        public static Uri buildCartUri() {
            return CONTENT_URI;
        }


        public static ContentValues CartToContentValues(CartModel cartModel)
        {
            ContentValues contentValues = new ContentValues();

            contentValues.put(CartInternalClass.PRODUCT_ID, cartModel.getProductId());
            contentValues.put(CartInternalClass.VARIANT_ID, cartModel.getVariantId());
            contentValues.put(CartInternalClass.QUANTITY, cartModel.getQuantity());
            contentValues.put(CartInternalClass.VARIANT_PRICE, cartModel.getVariantPrice());
            return  contentValues;
        }

    }

    public static class CustomerInternalClass implements CustomerFields, BaseColumns
    {
        public static String ALL_FIELDS[] =
                {
                        CustomerInternalClass._ID,
                        CustomerInternalClass.ACCESS_TOKEN,
                        CustomerInternalClass.ACCESS_TOKEN_EXPIRES_AT,
                        CustomerInternalClass.FIRST_NAME,
                        CustomerInternalClass.LAST_NAME,
                        CustomerInternalClass.EMAIL


                };

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(ProductContentProvider.CUSTOMER_TABLE).build();

        /** for all Variants */

        public static Uri buildCartUri() {
            return CONTENT_URI;
        }


        public static ContentValues CustomerToContentValues(CustomerModel customerModel)
        {
            ContentValues contentValues = new ContentValues();

            contentValues.put(CustomerInternalClass.ACCESS_TOKEN, customerModel.getAccessToken());
            contentValues.put(CustomerInternalClass.ACCESS_TOKEN_EXPIRES_AT, customerModel.getAccessTokenExpiresAt());
            contentValues.put(CustomerInternalClass.FIRST_NAME, customerModel.getFirstName());
            contentValues.put(CustomerInternalClass.LAST_NAME, customerModel.getLastName());
            contentValues.put(CustomerInternalClass.EMAIL, customerModel.getEmail());


            return  contentValues;
        }

    }

    public static class OrderInternalClass implements OrderFields, BaseColumns
    {
        public static String ALL_FIELDS[] =
                {
                        OrderInternalClass._ID,
                        OrderInternalClass.CUSTOMER_ACCESS_TOKEN,
                        OrderInternalClass.NAME,
                        OrderInternalClass.PRICE,
                        OrderInternalClass.PROCESSED_AT,

                };

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(ProductContentProvider.ORDERS_TABLE).build();

        /** for all Variants */

        public static Uri buildCartUri() {
            return CONTENT_URI;
        }


        public static ContentValues OrderToContentValues(OrderModel orderModel)
        {
            ContentValues contentValues = new ContentValues();

            contentValues.put(OrderInternalClass.CUSTOMER_ACCESS_TOKEN, orderModel.getCustomerAccessToken());
            contentValues.put(OrderInternalClass.NAME, orderModel.getName());
            contentValues.put(OrderInternalClass.PRICE, orderModel.getPrice());
            contentValues.put(OrderInternalClass.PROCESSED_AT, orderModel.getProcessedAt());



            return  contentValues;
        }

    }


}