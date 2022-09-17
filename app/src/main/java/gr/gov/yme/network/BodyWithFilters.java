package gr.gov.yme.network;

import java.util.ArrayList;

public class BodyWithFilters {
    String token;
    CenterPointCoordinates CenterPointCoordinates;
    String MaxNumberOfResponsePoints;
    ArrayList<String> ConnectorTypes;
    ArrayList<String> PowerTypes;
    String ConnectorFormat;
    String EvseStatus;
    boolean EvseIsAvailable;

    public BodyWithFilters(String token,
                           CenterPointCoordinates centerPointCoordinates,
                           String maxNumberOfResponsePoints,
                           ArrayList<String> connectorTypes,
                           ArrayList<String> powerTypes,
                           String connectorFormat,
                           String evseStatus) {
        this.token = token;
        CenterPointCoordinates = centerPointCoordinates;
        MaxNumberOfResponsePoints = maxNumberOfResponsePoints;
        ConnectorTypes = connectorTypes;
        PowerTypes = powerTypes;
        ConnectorFormat = connectorFormat;
        EvseStatus = evseStatus;
    }

    public static class CenterPointCoordinates {
        public String longitude;
        public String latitude;

        public CenterPointCoordinates(String longitude, String latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }



}


























