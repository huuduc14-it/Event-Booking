package com.example.mobileapp.network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Body;

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
    
            // Organizer Register
            @POST("api/organizer/register")
            Call<ApiResponse> registerOrganizer(@Body com.example.mobileapp.ui.activity.OrganizerRegisterActivity.OrganizerRequest request);

            // Create Event
            @POST("api/organizer/events/create")
            Call<ApiResponse> createEvent(@Body com.example.mobileapp.ui.activity.CreateEventActivity.CreateEventRequest request);

            // Get Attendees
            @GET("api/organizer/events/{id}/attendees")
            Call<AttendeeResponse> getAttendees(@Path("id") int eventId);

            // Import Excel (Multipart)
            @Multipart
            @POST("api/organizer/events/import")
            Call<ApiResponse> importAttendees(
                    @Part MultipartBody.Part file,
                    @Part("event_id") RequestBody eventId,
                    @Part("ticket_type_id") RequestBody ticketTypeId,
                    @Part("price") RequestBody price
            );

            // Export Excel (Download)
            @GET("api/organizer/events/{id}/export/excel")
            Call<ResponseBody> exportExcel(@Path("id") int eventId);

            // Export PDF (Download)
            @GET("api/organizer/events/{id}/export/pdf")
            Call<ResponseBody> exportPDF(@Path("id") int eventId);
}