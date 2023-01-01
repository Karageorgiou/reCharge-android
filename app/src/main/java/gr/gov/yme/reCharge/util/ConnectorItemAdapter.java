package gr.gov.yme.reCharge.util;

import gr.gov.yme.R;
import gr.gov.yme.reCharge.models.location.Evse;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class ConnectorItemAdapter extends RecyclerView.Adapter<ConnectorItemAdapter.ConnectorViewHolder> {

    private static final String TAG = "ConnectorItemAdapter";
    private List<Evse.Connector> connectorItemList;

    // Constructor
    ConnectorItemAdapter(List<Evse.Connector> connectorItemList) {
        this.connectorItemList = connectorItemList;
    }

    @NonNull
    @Override
    public ConnectorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        // Here we inflate the corresponding
        // layout of the child item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.child_item, viewGroup, false);  //todo change child_item layout file

        return new ConnectorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConnectorViewHolder connectorViewHolder, int position) {

        // Create an instance of the connectorItem
        // class for the given position
        Evse.Connector connectorItem = connectorItemList.get(position);

        // For the created instance, set title.
        // No need to set the image for
        // the ImageViews because we have
        // provided the source for the images
        // in the layout file itself

        //todo write set methods

        if (connectorItem.standard.compareTo("IEC_62196_T1") == 0) {
            connectorViewHolder.type.setText(R.string.type_1);
            connectorViewHolder.imageView.setImageResource(R.drawable.ic_connector_typ1);
        } else if (connectorItem.standard.compareTo("IEC_62196_T2") == 0) {
            connectorViewHolder.type.setText(R.string.type_2);
            connectorViewHolder.imageView.setImageResource(R.drawable.ic_connector_typ2);
        } else if (connectorItem.standard.compareTo("IEC_62196_T1_COMBO") == 0) {
            connectorViewHolder.type.setText(R.string.type_1_combo);
            connectorViewHolder.imageView.setImageResource(R.drawable.ic_connector_ccs_typ1);
        } else if (connectorItem.standard.compareTo("IEC_62196_T2_COMBO") == 0) {
            connectorViewHolder.type.setText(R.string.type_2_combo);
            connectorViewHolder.imageView.setImageResource(R.drawable.ic_connector_css_typ2_black);
        } else if (connectorItem.standard.compareTo("CHADEMO") == 0) {
            connectorViewHolder.type.setText(R.string.chademo);
            connectorViewHolder.imageView.setImageResource(R.drawable.ic_connector_chademo);
        } else if (connectorItem.standard.compareTo("DOMESTIC_F") == 0) {
            connectorViewHolder.type.setText(R.string.domestic_f);
            connectorViewHolder.imageView.setImageResource(R.drawable.ic_connector_schuko);
        } else {
            connectorViewHolder.type.setText(R.string.n_a);
            connectorViewHolder.imageView.setImageResource(R.drawable.ic_no_image);
        }


        connectorViewHolder.type_full.setText(connectorItem.standard);

        connectorViewHolder.volt.setText(connectorItem.maxVoltage.toString());
        connectorViewHolder.amp.setText(connectorItem.maxAmperage.toString());
        try {
            connectorViewHolder.power.setText(connectorItem.maxElectricPower.toString());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            connectorViewHolder.power.setText("N/A");

        }

        if (connectorItem.openApiTariffs != null) {
            connectorViewHolder.price.setText(connectorItem.openApiTariffs.get(0).elements.get(0).priceComponents.get(0).price);
        }
        //connectorViewHolder.last_update_time.setText(time);

    }


    @Override
    public int getItemCount() {
        return connectorItemList.size();
    }

    class ConnectorViewHolder extends RecyclerView.ViewHolder {

        //TextView ChildItemTitle;

        ImageView imageView;
        TextView type;
        TextView type_full;
        TextView volt;
        TextView amp;
        TextView power;
        TextView price;
        //TextView last_update_time;


        ConnectorViewHolder(View itemView) {
            super(itemView);
            //ChildItemTitle = itemView.findViewById(R.id.child_item_title);

            imageView = itemView.findViewById(R.id.connector_image);
            type = itemView.findViewById(R.id.connector_type);
            type_full = itemView.findViewById(R.id.connector_full_type);
            volt = itemView.findViewById(R.id.connector_volt);
            amp = itemView.findViewById(R.id.connector_amp);
            power = itemView.findViewById(R.id.connector_power);
            price = itemView.findViewById(R.id.connector_price);
            //last_update_time = itemView.findViewById(R.id.connector_last_updated_time);
        }
    }
}

