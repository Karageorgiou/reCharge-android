package gr.gov.yme.models.point;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Connector {

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
