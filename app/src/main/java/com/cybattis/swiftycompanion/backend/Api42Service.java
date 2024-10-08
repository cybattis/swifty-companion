package com.cybattis.swiftycompanion.backend;

import com.cybattis.swiftycompanion.auth.TokenInfo;
import com.cybattis.swiftycompanion.auth.Tokens;
import com.cybattis.swiftycompanion.profile.User;
import com.cybattis.swiftycompanion.profile.Users;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Tag;

public interface Api42Service {

    @FormUrlEncoded
    @POST("oauth/token")
    Call<Tokens> getToken(@Field("grant_type") String grantType,
                          @Field("client_id") String clientId,
                          @Field("client_secret") String clientSecret);

    @FormUrlEncoded
    @POST("oauth/token")
    Call<Tokens> refreshToken(@Field("grant_type") String grantType,
                              @Field("client_id") String clientId,
                              @Field("client_secret") String clientSecret,
                              @Field("refresh_token") String refreshToken);

    @GET("oauth/token/info")
    Call<TokenInfo> getTokenInfo(@Header("Authorization") String accessToken);

    @GET("/v2/users")
    Call<Users[]> getUsers(@Header("Authorization") String accessToken, @Query(value = "filter%5Blogin%5D", encoded = true) String login);

    @GET("v2/users/{id}")
    Call<User> getMe(@Path("id") String id, @Header("Authorization") String accessToken);
}
