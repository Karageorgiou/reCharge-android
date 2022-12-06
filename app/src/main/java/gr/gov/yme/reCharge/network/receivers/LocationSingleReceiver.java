package gr.gov.yme.reCharge.network.receivers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import gr.gov.yme.reCharge.models.location.ChargePointLocation;

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
