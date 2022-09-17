package gr.gov.yme.models.location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Coordinates {

    @SerializedName("latitude")
    @Expose
    public String latitude;
    @SerializedName("longitude")
    @Expose
    public String longitude;
    @SerializedName("name")
    @Expose
    public Name name;

    public static class Name {

        @SerializedName("language")
        @Expose
        public String language;
        @SerializedName("text")
        @Expose
        public String text;

    }
}
