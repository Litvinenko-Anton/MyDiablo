package com.lego.mydiablo.rest;

import android.util.Base64;

import com.lego.mydiablo.rest.callback.AccessToken;
import com.lego.mydiablo.rest.callback.CheckedToken;
import com.lego.mydiablo.utils.Const;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * @author Lego on 09.09.2016.
 */

public class AuthRequest {

    private static AuthApi api;

    public static Observable<AccessToken> getAccessToken(Boolean isRefresh, String code) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Interceptor.Chain chain) throws IOException {
                                Request original = chain.request();
                                // Request customization: add request headers
                                Request.Builder requestBuilder = original.newBuilder()
                                        .header("Authorization", "Basic "
                                                + Base64.encodeToString((Const.CLIENT_ID+":"+Const.CLIENT_SECRET).getBytes(), Base64.NO_WRAP))
                                        .method(original.method(), original.body());

                                Request request = requestBuilder.build();
                                return chain.proceed(request);
                            }
                        })
                .addInterceptor(interceptor)
                .build();

        Retrofit client = new Retrofit.Builder()
                .baseUrl(Const.BASE_URL)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        api = client.create(AuthApi.class);
        return api.obtainAccessToken(
                isRefresh ? Const.GRANT_TYPE_REFRESH : Const.GRANT_TYPE_AUTHORIZE,
                code,
                Const.REDIRECT_URI, ""
        );
    }

    public static Call<CheckedToken> checkToken(String token){
            return api.checkToken(token);
    }


}