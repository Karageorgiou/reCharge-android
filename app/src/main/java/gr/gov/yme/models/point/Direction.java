package gr.gov.yme.models.point;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Direction {

    @SerializedName("language")
    @Expose
    public String language;
    @SerializedName("text")
    @Expose
    public String text;

}
