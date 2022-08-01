package gr.gov.yme.models.locations;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feature {

    @SerializedName("geometry")
    @Expose
    public Geometry geometry;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("properties")
    @Expose
    public Properties properties;

}