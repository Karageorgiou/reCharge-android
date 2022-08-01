package gr.gov.yme.models.point;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Locat {

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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getPublish() {
        return publish;
    }

    public void setPublish(Boolean publish) {
        this.publish = publish;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getParkingType() {
        return parkingType;
    }

    public void setParkingType(String parkingType) {
        this.parkingType = parkingType;
    }

    public List<Evse> getEvses() {
        return evses;
    }

    public void setEvses(List<Evse> evses) {
        this.evses = evses;
    }

    public List<String> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }

    public List<Direction> getDirections() {
        return directions;
    }

    public void setDirections(List<Direction> directions) {
        this.directions = directions;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public OpenApiLocation getOpenApiLocation() {
        return openApiLocation;
    }

    public void setOpenApiLocation(OpenApiLocation openApiLocation) {
        this.openApiLocation = openApiLocation;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


    @Override
    public String toString() {
        return "Loc{" +"\n"+
                "countryCode='" + countryCode + '\'' +
                "\n"+", partyId='" + partyId + '\'' +
                "\n"+", id='" + id + '\'' +
                "\n"+", publish=" + publish +
                "\n"+", name='" + name + '\'' +
                "\n"+", address='" + address + '\'' +
                "\n"+", city='" + city + '\'' +
                "\n"+", postalCode='" + postalCode + '\'' +
                "\n"+", state='" + state + '\'' +
                "\n"+", country='" + country + '\'' +
                "\n"+", coordinates=" + coordinates +
                "\n"+", parkingType='" + parkingType + '\'' +
                "\n"+", evses=" + evses +
                "\n"+", facilities=" + facilities +
                "\n"+", directions=" + directions +
                "\n"+", operator=" + operator +
                "\n"+", owner=" + owner +
                "\n"+", images=" + images +
                "\n"+", openApiLocation=" + openApiLocation +
                "\n"+", timeZone='" + timeZone + '\'' +
                "\n"+", lastUpdated='" + lastUpdated + '\'' +
                '}';
    }
}
