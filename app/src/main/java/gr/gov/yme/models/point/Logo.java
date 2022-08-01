package gr.gov.yme.models.point;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Logo {

    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("category")
    @Expose
    public String category;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("width")
    @Expose
    public Integer width;
    @SerializedName("height")
    @Expose
    public Integer height;

}
