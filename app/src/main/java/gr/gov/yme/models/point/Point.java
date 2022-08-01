package gr.gov.yme.models.point;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Point {

    @SerializedName("Loc")
    @Expose
    public Locat loc;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("statusDesc")
    @Expose
    public String statusDesc;

}
