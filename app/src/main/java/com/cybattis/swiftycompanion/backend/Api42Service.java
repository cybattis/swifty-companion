package com.cybattis.swiftycompanion.backend;

import com.cybattis.swiftycompanion.auth.TokenInfo;
import com.cybattis.swiftycompanion.auth.Tokens;
import com.cybattis.swiftycompanion.profile.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
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

    @FormUrlEncoded
    @POST("oauth/token")
    Call<Tokens> refreshToken(@Field("grant_type") String grantType,
                              @Field("client_id") String clientId,
                              @Field("client_secret") String clientSecret,
                              @Field("refresh_token") String refreshToken);

    @GET("oauth/token/info")
    Call<TokenInfo> getTokenInfo(@Header("Authorization") String accessToken);

    @GET("v2/me")
    Call<User> getMe(@Header("Authorization") String accessToken);
}
