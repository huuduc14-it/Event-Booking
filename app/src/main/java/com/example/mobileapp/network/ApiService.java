package com.example.mobileapp.network;

import com.example.mobileapp.data.model.Ticket;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("auth/register")
    Call<RegisterResponse> register(
            @Field("full_name") String name,
            @Field("email") String email,
            @Field("password_hash") String password
    );

    @FormUrlEncoded
    @POST("auth/login")
    Call<LoginResponse> login(
            @Field("email") String email,
            @Field("password_hash") String password
    );
    @GET("auth/viewAll")
    Call<ViewAllEventResponse> ViewAllEvents();
    @GET("event/detail/{event_id}")
    Call<EventDetailResponse> getEventDetail(@Path("event_id") int eventId);

    @GET("event/search")
    Call<EventSearchResponse> searchEvent(  @Query("keyword") String keyword,
                                            @Query("date") String date,
                                            @Query("category_id") Integer categoryId);

    @GET("profile/me")
    Call<ProfileResponse> getProfile(@Header("Authorization") String token);

    @GET("tickets/my-tickets")
    Call<TicketResponse> getTicketsByUser(
            @Header("Authorization") String token
    );
    @POST("review/create")
    @FormUrlEncoded
    Call<ReviewResponse> createReview(
            @Header("Authorization") String token,
            @Field("event_id") int eventId,
            @Field("rating") int rating,
            @Field("comment") String comment
    );

    // Nếu bạn muốn riêng API lấy vé
}