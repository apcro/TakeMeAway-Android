package io.takemeaway.takemeaway.services;

import io.takemeaway.takemeaway.datamodels.Airports;
import io.takemeaway.takemeaway.datamodels.HotelAvailabilities;
import io.takemeaway.takemeaway.datamodels.HotelDetails;
import io.takemeaway.takemeaway.datamodels.Hotels;
import io.takemeaway.takemeaway.datamodels.SingleAirport;
import io.takemeaway.takemeaway.datamodels.SingleDestination;
import io.takemeaway.takemeaway.responsemodels.AvatarResponse;
import io.takemeaway.takemeaway.responsemodels.BaseResponse;
import io.takemeaway.takemeaway.responsemodels.CityResponse;
import io.takemeaway.takemeaway.responsemodels.DestinationsResponse;
import io.takemeaway.takemeaway.responsemodels.LoginResponse;
import io.takemeaway.takemeaway.responsemodels.NearestAirportResponse;
import io.takemeaway.takemeaway.responsemodels.OneSavedItemResponse;
import io.takemeaway.takemeaway.responsemodels.RegisterResponse;
import io.takemeaway.takemeaway.responsemodels.SavedItemsResponse;
import io.takemeaway.takemeaway.responsemodels.UserResponse;
import io.takemeaway.takemeaway.responsemodels.UserSettings;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public interface ApiInterface {

    // POST methods
    @POST("login")
    Call<LoginResponse> login(
            @Query("username") String username,
            @Query("password") String password,
            @Query("type") String type);

    @POST("logout")
    Call<BaseResponse> logout(
            @Header("Token") String token
    );

    @POST("register")
    Call<RegisterResponse> register(@Query("email") String email,
                                    @Query("password") String password,
                                    @Query("name") String name,
                                    @Query("locale") String locale);

    @POST("updateProfile")
    Call<BaseResponse> updateProfile(
            @Header("Token") String token,
            @Query("username") String username,
            @Query("firstname") String firstName,
            @Query("lastname") String lastName,
            @Query("email") String emailaddress,
            @Query("home_airport") String home_airport,
            @Query("latitude") float lat,
            @Query("longitude") float lon

    );


    @POST("updateSettings")
    Call<BaseResponse> updateSettings(
            @Header("Token") String token,
            @Query("budget") int budget,
            @Query("people") int people,
            @Query("split") int split,
            @Query("dates") String dates,
            @Query("leaveday") String leaveday,
            @Query("returnday") String returnday,
            @Query("filters") String filters,
            @Query("currency") String currency,
            @Query("locale") String locale,
            @Query("leaveDate") long leaveDate,
            @Query("returnDate") long returnDate
    );

    @POST("saveItem")
    Call<BaseResponse> saveItem(
            @Header("Token") String token,
            @Query("cityId") Integer cityId,
            @Query("hotelId") Integer hotelId,
            @Query("hotelprice") Integer hotelprice,
            @Query("flightprice") String flightprice,
            @Query("packageid") Integer packageid,
            @Query("eventid") String eventid
    );

    @POST("sendFeedback")
    Call<BaseResponse> sendFeedback(
            @Header("Token") String token,
            @Query("feedback") String feedback
    );


    @POST("deleteSavedItem")
    Call<BaseResponse> deleteSavedItem(
            @Header("Token") String token,
            @Query("cityId") Integer cityId,
            @Query("hotelId") Integer hotelId
    );

    @POST("updateDeviceToken")
    Call<BaseResponse> updateDeviceToken(
            @Header("Token") String token,
            @Query("deviceToken") String deviceToken,
            @Query("deviceType") String deviceType
    );


    @Multipart
    @POST("updateAvatar")
    Call<AvatarResponse> updateAvatar(
            @Header("Token") String token,
            @Part MultipartBody.Part avatar
    );

    @POST("emailTrip")
    Call<BaseResponse> emailTrip(
            @Header("Token") String token,
            @Query("cityId") Integer cityId,
            @Query("hotelId") Integer hotelId
    );

    // GET methods
    @GET("findDestination")
    Call<DestinationsResponse> findDestination(
        @Header("Token") String token,
        @Query("data") Integer count
    );

    @GET("findHotel")
    Call<Hotels> findHotel(
            @Header("Token") String token,
            @Query("city_id") Integer cityId,
            @Query("count") Integer count,
            @Query("extraHotelId") Integer extraHotelId,
            @Query("flightcost") String flightcost);

    @GET("getCityDetails")
    Call<SingleDestination> getCityDetails(@Header("Token") String token, @Query("cityid") int cityid);

    @GET("getHotelDetails")
    Call<HotelDetails> getHotelDetails(@Header("Token") String token, @Query("hotelid") int hotelid);

    @GET("getRoomDetails")
    Call<HotelDetails> getRoomDetails(@Header("Token") String token, @Query("hotelid") int hotelid);

    @GET("userLoad")
    Call<UserResponse> userLoad(@Header("Token") String token);

    @GET("getAirports")
    Call<Airports> getAirports(@Query("lat") String lat, @Query("lon") String lon);

    @GET("getNearestAirport")
    Call<NearestAirportResponse> getNearestAirport(@Header("Token") String token,
                                                   @Query("lat") String lat,
                                                   @Query("lon") String lon);

    @GET("getAirportDetails")
    Call<SingleAirport> getAirportDetails(@Header("Token") String token, @Query("iata") String iata);

    @GET("loadSavedItems")
    Call<SavedItemsResponse> loadSavedItems(@Header("Token") String token);

    @GET("loadSavedItem")
    Call<OneSavedItemResponse> loadSavedItem(@Header("Token") String token,
                                             @Query("cityId") int cityId,
                                             @Query("hotelId") int hotelId);

    @GET("checkHotelAvailability")
    Call<HotelAvailabilities> checkHotelAvailability(
            @Header("Token") String token,
            @Query("hotel_ids") String hotelids);

}
