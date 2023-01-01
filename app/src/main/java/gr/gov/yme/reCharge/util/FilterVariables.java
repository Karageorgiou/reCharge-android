package gr.gov.yme.reCharge.util;

public class FilterVariables {
    /********************** Filters FilterVariables **********************/

    public static String type1_Key = "key_type1";
    public static  String type2_Key = "key_type2";
    public static  String type1ccs_Key = "key_type1css";
    public static final String type2ccs_Key = "key_type2css";
    public static final String chademo_Key = "key_chademo";
    public static final String schuko_Key = "key_schuko";
    public static final String tesla_Key = "key_tesla";
    public static final String plug_Key = "key_plug";
    public static final String status_Key = "key_status";
    public boolean type1;
    public boolean type2;
    public boolean type1ccs;
    public boolean type2ccs;
    public boolean chademo;
    public boolean schuko;
    public boolean tesla;
    public static final String dc_Key = "key_dc";
    public static final String ac1_Key = "key_ac1";
    public static final String ac2_Key = "key_ac2";
    public static final String ac2split_Key = "key_ac2split";
    public static final String ac3_Key = "key_ac3";
    public boolean dc;
    public boolean ac1;
    public boolean ac2;
    public boolean ac2split;
    public boolean ac3;
    public boolean socket;
    public boolean cable;
    public String plug;
    public String status;


    private static final FilterVariables ourInstance = new FilterVariables();
    public static FilterVariables getInstance() {
        return ourInstance;
    }
    private FilterVariables() {
    }
}