package gr.gov.yme.models.location;

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


    public List<Connector> getConnectorItemList()
    {
        return connectors;
    }


    public static class Connector {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("standard")
        @Expose
        public String standard;
        @SerializedName("format")
        @Expose
        public String format;
        @SerializedName("power_type")
        @Expose
        public String powerType;
        @SerializedName("max_voltage")
        @Expose
        public Integer maxVoltage;
        @SerializedName("max_amperage")
        @Expose
        public Integer maxAmperage;
        @SerializedName("max_electric_power")
        @Expose
        public Integer maxElectricPower;
        @SerializedName("terms_and_conditions")
        @Expose
        public String termsAndConditions;
        @SerializedName("tariff_ids")
        @Expose
        public List<String> tariffIds = null;
        @SerializedName("last_updated")
        @Expose
        public String lastUpdated;


    }
}
