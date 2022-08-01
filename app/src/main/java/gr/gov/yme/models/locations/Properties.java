package gr.gov.yme.models.locations;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Properties {

    @SerializedName("location_id")
    @Expose
    public String locationId;
    @SerializedName("isActive")
    @Expose
    public Integer isActive;

}