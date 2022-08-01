package gr.gov.yme.models.point;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Operator {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("logo")
    @Expose
    public Logo logo;

}