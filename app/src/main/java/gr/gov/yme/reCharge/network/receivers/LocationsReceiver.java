package gr.gov.yme.reCharge.network.receivers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import gr.gov.yme.reCharge.models.location.ChargePointLocation;

public class LocationsReceiver {


    @SerializedName("Locations")
    @Expose
    public List<ChargePointLocation> locations = null;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("statusDesc")
    @Expose
    public String statusDesc;


    public LocationsReceiver(LocationsReceiver locationsReceiver) {
        this.locations = locationsReceiver.locations;
        this.status = locationsReceiver.status;
        this.statusDesc = locationsReceiver.statusDesc;
    }


}
