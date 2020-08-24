package org.pme.rssreader.network;

import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class NetworkController {

    private static final String PLACEHOLDER_API_BASE_URL = "https://will.change";

    private static Retrofit INSTANCE;
    public static NetworkApi API;

    public static NetworkApi getApi()
    {

        if( INSTANCE == null ) {
            synchronized ( NetworkController.class ) {
                if( INSTANCE == null ) {
                    INSTANCE = new Retrofit.Builder()
                            .baseUrl(PLACEHOLDER_API_BASE_URL)
                            .addConverterFactory(SimpleXmlConverterFactory.create())
                            .build();

                    API = INSTANCE.create(NetworkApi.class);
                }
            }
        }

        return API;
    }

}
