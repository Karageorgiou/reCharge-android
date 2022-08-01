package gr.gov.yme.models.locations;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class FeatureCollection {

    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("features")
    @Expose
    public List<Feature> features = null;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("statusDesc")
    @Expose
    public String statusDesc;

}