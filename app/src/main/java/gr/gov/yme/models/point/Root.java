package gr.gov.yme.models.point;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Root {
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
