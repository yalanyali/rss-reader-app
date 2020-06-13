package com.pme.rssreader.network;

import com.pme.rssreader.network.model.XmlFeed;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface NetworkApi {
    @GET
    public Call<XmlFeed> getFeed(@Url String url);
}