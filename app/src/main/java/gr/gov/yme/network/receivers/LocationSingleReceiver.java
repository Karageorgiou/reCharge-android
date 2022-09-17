package gr.gov.yme.network.receivers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import gr.gov.yme.models.location.ChargePointLocation;

public class LocationSingleReceiver {
    @SerializedName("Loc")
    @Expose
    public ChargePointLocation loc;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("statusDesc")
    @Expose
    public String statusDesc;
}
