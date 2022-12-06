package gr.gov.yme.reCharge.models.location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpenApiLocation {

    @SerializedName("location_id")
    @Expose
    public String locationId;
    @SerializedName("VatNo")
    @Expose
    public String vatNo;
    @SerializedName("CompanyMail")
    @Expose
    public String companyMail;
    @SerializedName("CompanyCommercialName")
    @Expose
    public String companyCommercialName;
    @SerializedName("ProfileImage")
    @Expose
    public String profileImage;
    @SerializedName("WebSite")
    @Expose
    public String webSite;
    @SerializedName("NumberOfEvses")
    @Expose
    public Integer numberOfEvses;

}