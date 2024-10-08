package com.cybattis.swiftycompanion.backend;

import com.cybattis.swiftycompanion.auth.Tokens;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api42Service {

    @FormUrlEncoded
    @POST("oauth/token")
    Call<Tokens> getToken(@Field("grant_type") String grantType,
                          @Field("client_id") String clientId,
                          @Field("client_secret") String clientSecret,
                          @Field("code") String code,
                          @Field("redirect_uri") String redirectUri,
                          @Field("state") String state);
}
