package gr.gov.yme.activities.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import gr.gov.yme.R;
import gr.gov.yme.models.point.Connector;

public class ListAdapter extends ArrayAdapter<Connector> {
    String TAG = "ListAdapter";

    //todo
    public ListAdapter(@NonNull Context context, ArrayList<Connector> connectorArrayList) {
        super(context, R.layout.util_text_view, R.id.connector_full_type, connectorArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Connector connector = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.connector_image);
        TextView type = convertView.findViewById(R.id.connector_type);
        TextView type_full = convertView.findViewById(R.id.connector_full_type);
        TextView volt = convertView.findViewById(R.id.connector_volt);
        TextView amp = convertView.findViewById(R.id.connector_amp);
        TextView power = convertView.findViewById(R.id.connector_power);
        TextView last_update_date = convertView.findViewById(R.id.connector_last_updated_date);
        TextView last_update_time = convertView.findViewById(R.id.connector_last_updated_time);


        if (connector.standard.compareTo("IEC_62196_T1")==0) {
            type.setText("Type 1");
            imageView.setImageResource(R.drawable.ic_connector_typ1);
        } else if (connector.standard.compareTo("IEC_62196_T2")==0) {
            type.setText("Type 2");
            imageView.setImageResource(R.drawable.ic_connector_typ2);
        } else if (connector.standard.compareTo("IEC_62196_T1_COMBO")==0) {
            type.setText("Type 1 Combo");
            imageView.setImageResource(R.drawable.ic_connector_ccs_typ1);
        } else {
            type.setText("N/A");
            imageView.setImageResource(R.drawable.ic_drag_down);
        }


        type_full.setText(connector.standard);

        volt.setText(connector.maxVoltage.toString());
        amp.setText(connector.maxAmperage.toString());
        try {
            power.setText(connector.maxElectricPower.toString());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            power.setText("N/A");

        }


        String[] parts = connector.lastUpdated.split("T");
        String date = parts[0];
        String time = parts[1];

        last_update_date.setText(date);
        last_update_time.setText(time);


        //return super.getView(position, convertView, parent);
        return convertView;
    }
}
