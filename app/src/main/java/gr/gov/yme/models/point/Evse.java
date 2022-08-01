package gr.gov.yme.models.point;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Evse {

    @SerializedName("uid")
    @Expose
    public String uid;
    @SerializedName("evse_id")
    @Expose
    public String evseId;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("coordinates")
    @Expose
    public Coordinates coordinates;
    @SerializedName("capabilities")
    @Expose
    public List<String> capabilities = null;
    @SerializedName("parking_restrictions")
    @Expose
    public List<String> parkingRestrictions = null;
    @SerializedName("connectors")
    @Expose
    public List<Connector> connectors = null;
    @SerializedName("physical_reference")
    @Expose
    public String physicalReference;
    @SerializedName("floor_level")
    @Expose
    public String floorLevel;
    @SerializedName("directions")
    @Expose
    public List<Direction> directions = null;
    @SerializedName("last_updated")
    @Expose
    public String lastUpdated;


}
