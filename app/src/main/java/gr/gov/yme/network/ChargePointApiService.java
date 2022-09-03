package gr.gov.yme.network;

import java.util.Map;

import gr.gov.yme.models.locations.FeatureCollection;
import gr.gov.yme.models.point.Root;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ChargePointApiService {
    @GET("GetToken")
    Call<TokenReceiver> getToken();

    @POST("GetLocations")
    Call<FeatureCollection> createFeatureCollection(@Body Token token);

    @POST("GetLocation")
    Call<Root> getLocation(@Body Map<String, Object> body);

    @POST("GetLocationsDetails")
    Call<Root> getLocationsDetails(@Body Token token);



}