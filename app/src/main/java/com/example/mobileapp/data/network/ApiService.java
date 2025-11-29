package com.example.mobileapp.data.network;

import com.example.mobileapp.data.model.ApiResponse;
import com.example.mobileapp.data.model.ArtistResponse;
import com.example.mobileapp.data.model.BookingHistoryResponse;
import com.example.mobileapp.data.model.BookingReq;
import com.example.mobileapp.data.model.CreateEventReq;
import com.example.mobileapp.data.model.TicketListResponse;
import com.example.mobileapp.data.model.TicketType;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/event/create-event")
    Call<ApiResponse> createEvent(
            @Header("Authorization") String token, // "Bearer eyJ..."
            @Body CreateEventReq request
    );
    @Multipart
    @POST("api/event/create-event")
    Call<ApiResponse> createEventMultipart(
            @Header("Authorization") String token,

            // 1. Simple Text Fields (Wrapped in RequestBody)
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("location_name") RequestBody locationName,
            @Part("address") RequestBody address,
            @Part("start_time") RequestBody startTime,
            @Part("end_time") RequestBody endTime,
            @Part("category_id") RequestBody categoryId,

            // 2. Complex Lists (Must be sent as JSON Strings)
            @Part("tickets") RequestBody ticketsJson,     // We will convert List -> String
            @Part("artist_ids") RequestBody artistIdsJson,// We will convert List -> String

            // 3. The Image File
            @Part MultipartBody.Part thumbnail
    );
    @GET("api/artists")
    Call<ArtistResponse> getArtists();
    @GET("api/event/{id}/tickets")
    Call<TicketListResponse> getEventTickets(@Path("id") int eventId,
                                             @Header("Authorization") String token);


    @POST("api/tickets/add")
    Call<ApiResponse> addTicket(@Header("Authorization") String token, @Body TicketType ticket);

    @PUT("api/tickets/{id}")
    Call<ApiResponse> updateTicket(@Header("Authorization") String token, @Path("id") int ticketId, @Body TicketType ticket);

    @DELETE("api/tickets/{id}")
    Call<ApiResponse> deleteTicket(@Header("Authorization") String token, @Path("id") int ticketId);

    @POST("/api/booking/book")
    Call<ApiResponse> bookTickets(String authHeader, BookingReq req);

    @GET("api/booking/my-tickets")
    Call<BookingHistoryResponse> getMyHistory(@Header("Authorization") String token);
}

