package com.cybattis.swiftycompanion.backend;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtils {

    public static ApiResponse parseError(Response<?> response) {
        Converter<ResponseBody, ApiResponse> converter =
                Api42Client.getClient().responseBodyConverter(ApiResponse.class, new Annotation[0]);

        ApiResponse error;

        try {
            if (response.errorBody() == null) {
                return new ApiResponse(response.code(), response.message());
            }
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new ApiResponse(500, "An error occurred");
        }

        return error;
    }
}