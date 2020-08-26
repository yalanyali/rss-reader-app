package org.pme.rssreader.network;

import org.pme.rssreader.network.model.XmlFeed;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Simple retrofit2 API interface that provides a GET request method.
 */
public interface NetworkApi {
    @GET
    public Call<XmlFeed> getFeed(@Url String url);
}