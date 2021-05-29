package com.agongym.store.utils;

import com.apollographql.apollo.ApolloClient;

import okhttp3.OkHttpClient;

public class Apollo {

    String token;
    String url;

    public Apollo(){

        token= "be05b0140c6b668dd468583ae1ca448d";
        url = "https://agon-gym.myshopify.com/api/graphql";
    }


    public ApolloClient getApolloClient(){
            ApolloClient client=null;

            //OkHttpClient

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
            client = ApolloClient.builder()
                    .serverUrl(url)
                    .okHttpClient(httpClient)
                    .build();


            return client;
    }
}