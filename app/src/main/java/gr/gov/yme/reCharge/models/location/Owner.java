package gr.gov.yme.reCharge.models.location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Owner {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("website")
    @Expose
    public String website;
    @SerializedName("logo")
    @Expose
    public Logo logo;

}