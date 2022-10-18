package gr.gov.yme.network;

import java.util.Map;

import gr.gov.yme.network.receivers.FeatureReceiver;
import gr.gov.yme.network.receivers.LocationsReceiver;
import gr.gov.yme.network.receivers.LocationSingleReceiver;
import gr.gov.yme.network.receivers.TokenReceiver;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ChargePointApiService {
    @GET("GetToken")
    Call<TokenReceiver> getToken();

    @POST("GetLocations")
    Call<FeatureReceiver> getLocations(@Body Token token);

    @POST("GetPLocations")
    Call<FeatureReceiver> getPLocations(@Body BodyWithFilters bodyWithFilters);

    @POST("GetLocation")
    Call<LocationSingleReceiver> getLocation(@Body Map<String, Object> body);


    @Deprecated
    @POST("GetLocationsDetails")
    Call<LocationsReceiver> getLocationsDetails(@Body Token token);

    @Deprecated
    @POST("GetPLocationsDetails")
    Call<LocationsReceiver> getPLocationsDetails(@Body BodyWithFilters bodyWithFilters);
}