package gr.gov.yme.network.receivers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenReceiver {
    @SerializedName("Token")
    @Expose
    public String token;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("statusDesc")
    @Expose
    public String statusDesc;
}
