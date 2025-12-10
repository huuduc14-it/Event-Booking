//package com.example.mobileapp.network;
//
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class RetrofitClient {
//
//    private static Retrofit retrofit;
//
//    public static Retrofit getInstance() {
//        if (retrofit == null) {
//            retrofit = new Retrofit.Builder()
//                    .baseUrl("http://10.0.2.2:3000/api/") // đổi IP của bạn
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//        }
//        return retrofit;
//    }
//}
//
package com.example.mobileapp.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    private static Retrofit retrofit;

    // Đổi IP này theo môi trường:
    // - Emulator: "http://10.0.2.2:3000/"
    // - Điện thoại thật: "http://192.168.x.x:3000/" (IP máy tính)
    private static final String BASE_URL = "http://10.0.2.2:3000/api/";

    public static Retrofit getInstance() {
        if (retrofit == null) {
            // Logging để debug
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

