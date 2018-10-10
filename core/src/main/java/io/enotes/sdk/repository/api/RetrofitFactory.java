package io.enotes.sdk.repository.api;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import io.enotes.sdk.constant.Constant;
import io.enotes.sdk.utils.LogUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
    private static ApiService apiService;
    private static ApiService transactionThirdService;
    public static final String ENOTES_SERVER="https://api.enotes.io:8443/";
    public static ApiService getTransactionService() {
        if (apiService == null) {
            apiService = getBaseRetrofit(true)
                    .create(ApiService.class);
        }
        return apiService;
    }

    public static ApiService getTransactionThirdService() {
        if (transactionThirdService == null) {
            transactionThirdService = getBaseRetrofit(false)
                    .create(ApiService.class);
        }
        return transactionThirdService;
    }

    private static Retrofit getBaseRetrofit(boolean eNotes) {
        return new Retrofit.Builder()
                .baseUrl(ENOTES_SERVER)
                .client(getOkHttpClient(eNotes))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build();
    }

    private static OkHttpClient getOkHttpClient(boolean eNotes) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.hostnameVerifier((hostname, session) -> true);
        builder.addInterceptor(getHttpLoggingInterceptor());
        builder.addInterceptor(getRetryIntercept());
        if(eNotes) {
            builder.connectTimeout(8000, TimeUnit.MILLISECONDS)
                    .readTimeout(8000, TimeUnit.MILLISECONDS).writeTimeout(20000, TimeUnit.MILLISECONDS);
        }else{
            builder.connectTimeout(2000, TimeUnit.MILLISECONDS)
                    .readTimeout(2000, TimeUnit.MILLISECONDS).writeTimeout(2000, TimeUnit.MILLISECONDS);
        }
        return builder.build();

    }

    private static HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogUtils.d("eNotes_RetrofitLog", "retrofitBack = " + message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    /**
     * retry 3 times if response failed
     *
     * @return
     */
    private static Interceptor getRetryIntercept() {
        return chain -> {
            Request request = chain.request();
            // try the request
            Response response = chain.proceed(request);
            int tryCount = 0;
            while (!response.isSuccessful() && tryCount < 3) {
                LogUtils.d("intercept", "Request is not successful - " + tryCount);
                tryCount++;
                // retry the request
                response = chain.proceed(request);
            }
            return response;
        };
    }
}
