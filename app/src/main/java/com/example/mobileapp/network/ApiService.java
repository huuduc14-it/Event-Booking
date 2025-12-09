package com.example.mobileapp.network;

import com.example.mobileapp.network.dto.BookingRequest;
import com.example.mobileapp.network.dto.BookingResponse;
import com.example.mobileapp.network.dto.BookingListResponse;
import com.example.mobileapp.network.dto.CheckInRequest;
import com.example.mobileapp.network.dto.CheckInResponse;
import com.example.mobileapp.network.dto.CreateEventRequest;
import com.example.mobileapp.network.dto.OrganizerProfileUpdateRequest;
import com.example.mobileapp.network.dto.OrganizerRegisterRequest;
import com.example.mobileapp.network.dto.TicketTypeResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("api/auth/register")
    Call<RegisterResponse> register(
            @Field("full_name") String name,
            @Field("email") String email,
            @Field("password_hash") String password
    );

    @FormUrlEncoded
    @POST("api/auth/login")
    Call<LoginResponse> login(
            @Field("email") String email,
            @Field("password_hash") String password
    );

    @GET("api/event/viewAll")
    Call<ViewAllEventResponse> ViewAllEvents();

    @GET("api/event/detail/{event_id}")
    Call<EventDetailResponse> getEventDetail(@Path("event_id") int eventId);

    @GET("api/event/search")
    Call<EventSearchResponse> searchEvent(
            @Query("keyword") String keyword,
            @Query("date") String date,
            @Query("category_id") Integer categoryId);

    @GET("api/categories")
    Call<CategoryResponse> getAllCategories();

    @GET("api/categories/{id}")
    Call<CategoryResponse> getCategoryById(@Path("id") int categoryId);

    @GET("api/profile/me")
    Call<ProfileResponse> getProfile(@Header("Authorization") String token);

    @GET("api/tickets/my-tickets")
    Call<TicketResponse> getTicketsByUser(@Header("Authorization") String token);

    @POST("api/review/create")
    @FormUrlEncoded
    Call<ReviewResponse> createReview(
            @Header("Authorization") String token,
            @Field("event_id") int eventId,
            @Field("rating") int rating,
            @Field("comment") String comment
    );

    // Organizer endpoints (require Authorization)
    @POST("api/organizer/register")
    Call<ApiResponse> registerOrganizer(
            @Header("Authorization") String token,
            @Body OrganizerRegisterRequest request
    );

    @POST("api/organizer/events/create")
    Call<ApiResponse> createEvent(
            @Header("Authorization") String token,
            @Body CreateEventRequest request
    );

    @GET("api/organizer/dashboard")
    Call<DashboardResponse> getDashboard(@Header("Authorization") String token);

    @PUT("api/organizer/events/{event_id}")
    Call<ApiResponse> updateEvent(
            @Header("Authorization") String token,
            @Path("event_id") int eventId,
            @Body CreateEventRequest request
    );

    @GET("api/organizer/events/{id}/attendees")
    Call<AttendeeResponse> getAttendees(
            @Header("Authorization") String token,
            @Path("id") int eventId
    );

    @Multipart
    @POST("api/organizer/events/import")
    Call<ApiResponse> importAttendees(
            @Header("Authorization") String token,
            @Part MultipartBody.Part file,
            @Part("event_id") RequestBody eventId,
            @Part("ticket_type_id") RequestBody ticketTypeId,
            @Part("price") RequestBody price
    );

    @GET("api/organizer/events/{id}/export/excel")
    Call<ResponseBody> exportExcel(
            @Header("Authorization") String token,
            @Path("id") int eventId
    );

    @GET("api/organizer/events/{id}/export/pdf")
    Call<ResponseBody> exportPDF(
            @Header("Authorization") String token,
            @Path("id") int eventId
    );

    @PUT("api/organizer/profile")
    Call<ApiResponse> updateOrganizerProfile(
            @Header("Authorization") String token,
            @Body OrganizerProfileUpdateRequest request
    );

    // Booking endpoints
    @GET("api/events/{event_id}/ticket-types")
    Call<TicketTypeResponse> getTicketTypes(@Path("event_id") int eventId);

    @POST("api/bookings")
    Call<BookingResponse> createBooking(
            @Header("Authorization") String token,
            @Body BookingRequest request
    );

    @GET("api/bookings")
    Call<BookingListResponse> getMyBookings(@Header("Authorization") String token);

    @GET("api/bookings/{booking_id}")
    Call<BookingResponse> getBookingDetail(
            @Header("Authorization") String token,
            @Path("booking_id") int bookingId
    );

    @POST("api/bookings/confirm-payment")
    @FormUrlEncoded
    Call<ApiResponse> confirmPayment(
            @Header("Authorization") String token,
            @Field("booking_id") int bookingId
    );

    @DELETE("api/bookings/{booking_id}")
    Call<ApiResponse> cancelBooking(
            @Header("Authorization") String token,
            @Path("booking_id") int bookingId
    );

    // Check-in endpoint
    @POST("api/tickets/check-in")
    Call<CheckInResponse> checkInTicket(@Body CheckInRequest request);

    // Update ticket quantity (Organizer)
    @PUT("api/ticket-types/update-quantity")
    @FormUrlEncoded
    Call<ApiResponse> updateTicketQuantity(
            @Header("Authorization") String token,
            @Field("ticket_type_id") int ticketTypeId,
            @Field("new_quantity") int newQuantity
    );
}
