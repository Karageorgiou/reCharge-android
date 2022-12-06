package gr.gov.yme.reCharge.network.receivers;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import gr.gov.yme.reCharge.models.feature.Feature;


public class FeatureReceiver {

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

    public FeatureReceiver(FeatureReceiver featureReceiver) {
        this.type = featureReceiver.type;
        this.features = featureReceiver.features;
        this.status = featureReceiver.status;
        this.statusDesc = featureReceiver.statusDesc;
    }

}