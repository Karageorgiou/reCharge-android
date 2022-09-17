package gr.gov.yme.models.feature;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Properties {

    @SerializedName("location_id")
    @Expose
    public String locationId;
    @SerializedName("isActive")
    @Expose
    public Integer isActive;
    @SerializedName("Provider")
    @Expose
    public String provider;
    @SerializedName("Forecolor")
    @Expose
    public String forecolor;
    @SerializedName("Backcolor")
    @Expose
    public String backcolor;
    @SerializedName("Logo")
    @Expose
    public String logo;
}