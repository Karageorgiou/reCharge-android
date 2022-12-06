package gr.gov.yme.reCharge.models.location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChargePointLocation {

    @SerializedName("country_code")
    @Expose
    public String countryCode;
    @SerializedName("party_id")
    @Expose
    public String partyId;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("publish")
    @Expose
    public Boolean publish;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("postal_code")
    @Expose
    public String postalCode;
    @SerializedName("state")
    @Expose
    public String state;
    @SerializedName("country")
    @Expose
    public String country;
    @SerializedName("coordinates")
    @Expose
    public Coordinates coordinates;
    @SerializedName("parking_type")
    @Expose
    public String parkingType;
    @SerializedName("evses")
    @Expose
    public List<Evse> evses = null;
    @SerializedName("facilities")
    @Expose
    public List<String> facilities = null;
    @SerializedName("directions")
    @Expose
    public List<Direction> directions = null;
    @SerializedName("operator")
    @Expose
    public Operator operator;
    @SerializedName("owner")
    @Expose
    public Owner owner;
    @SerializedName("images")
    @Expose
    public List<Image> images = null;
    @SerializedName("_openApiLocation")
    @Expose
    public OpenApiLocation openApiLocation;
    @SerializedName("time_zone")
    @Expose
    public String timeZone;
    @SerializedName("last_updated")
    @Expose
    public String lastUpdated;
}
