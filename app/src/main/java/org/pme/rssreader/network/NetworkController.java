package org.pme.rssreader.network;

import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Singleton controller wrapper for the network API.
 */
public class NetworkController {

    private static final String PLACEHOLDER_API_BASE_URL = "https://will.change.anyways";

    private static Retrofit INSTANCE;
    public static NetworkApi API;

    public static NetworkApi getApi()
    {

        if( INSTANCE == null ) {
            synchronized ( NetworkController.class ) {
                if( INSTANCE == null ) {
                    // SimpleXml is deprecated without a stable replacement.
                    // https://github.com/square/retrofit/issues/2733
                    //noinspection deprecation
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
