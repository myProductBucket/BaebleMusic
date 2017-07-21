package com.baeble.www.baebleapp.API;

import com.baeble.www.baebleapp.model.FeaturedVideo;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by ZaneH_000 on 10/14/2015.
 */
public interface SvcApiEndPoint {
    @GET("/getvideoListing/newest/1/1/new/mobile/n")
    void GetFeaturedVideos(SvcApiRestCallback<List<FeaturedVideo>> callback);

    @GET("/getvideoListing/newest/{pageIndex}/{recordCount}/{category}/mobile/n")
    void GetCategoryVideos(
                           @Path("pageIndex") int pageIndex,
                           @Path("recordCount") int recordCount,
                           @Path("category") String category,
                       SvcApiRestCallback<List<FeaturedVideo>> callback
    );

    @GET("/search/{videoName}/{category}/mobile/false")
    void GetSearchVideos(
            @Path("videoName") String videoName,
            @Path("category") String category,
            SvcApiRestCallback<List<FeaturedVideo>> callback
    );

    @GET("/getConcertsJsonByID/{videoId}/mobile")
    void GetConcertsJsonByID(
            @Path("videoId") String category,
            SvcApiRestCallback<List<FeaturedVideo>> callback
    );

    @GET("/getMusicVideosJSONByID/{videoId}/mobile")
    void GetMusicVideosJsonByID(
            @Path("videoId") String category,
            SvcApiRestCallback<List<FeaturedVideo>> callback
    );

    @GET("/getInterviewsJSONByID/{videoId}/mobile")
    void GetInterviewsJsonByID(
            @Path("videoId") String category,
            SvcApiRestCallback<List<FeaturedVideo>> callback
    );

    @POST("/registerpushtoken/{pushToken}/{pushTokenPlatform}/n")
    void PutPushToken(
            @Path("pushToken") String pushToken,
            @Path("pushTokenPlatform") String pushTokenPlatform,
            SvcApiRestCallback<String> callback
    );
}
