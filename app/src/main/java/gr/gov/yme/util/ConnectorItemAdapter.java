package gr.gov.yme.util;

import gr.gov.yme.R;
import gr.gov.yme.models.location.Evse;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ConnectorItemAdapter extends RecyclerView.Adapter<ConnectorItemAdapter.ConnectorViewHolder> {

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
            connectorViewHolder.type.setText("Type 1");
            connectorViewHolder.imageView.setImageResource(R.drawable.ic_connector_typ1);
        } else if (connectorItem.standard.compareTo("IEC_62196_T2") == 0) {
            connectorViewHolder.type.setText("Type 2");
            connectorViewHolder.imageView.setImageResource(R.drawable.ic_connector_typ2);
        } else if (connectorItem.standard.compareTo("IEC_62196_T1_COMBO") == 0) {
            connectorViewHolder.type.setText("Type 1 Combo");
            connectorViewHolder.imageView.setImageResource(R.drawable.ic_connector_ccs_typ1);
        } else if (connectorItem.standard.compareTo("CHADEMO") == 0) {
            connectorViewHolder.type.setText("Chademo");
            connectorViewHolder.imageView.setImageResource(R.drawable.ic_connector_chademo);
            } else if (connectorItem.standard.compareTo("DOMESTIC_F") == 0) {
            connectorViewHolder.type.setText("Πρίζα");
            connectorViewHolder.imageView.setImageResource(R.drawable.ic_connector_schuko);
        } else {
            connectorViewHolder.type.setText("N/A");
            connectorViewHolder.imageView.setImageResource(R.drawable.ic_no_image);
        }


        connectorViewHolder.type_full.setText(connectorItem.standard);

        connectorViewHolder.volt.setText(connectorItem.maxVoltage.toString());
        connectorViewHolder.amp.setText(connectorItem.maxAmperage.toString());
        try {
            connectorViewHolder.power.setText(connectorItem.maxElectricPower.toString());
        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
            connectorViewHolder.power.setText("N/A");

        }


        String[] parts = connectorItem.lastUpdated.split("T");
        String date = parts[0];
        String time = parts[1];

        connectorViewHolder.last_update_date.setText(date);
        connectorViewHolder.last_update_time.setText(time);

    }

    @Override
    public int getItemCount() {

        // This method returns the number
        // of items we have added
        // in the ChildItemList
        // i.e. the number of instances
        // of the ChildItemList
        // that have been created
        return connectorItemList.size();
    }

    // This class is to initialize
    // the Views present
    // in the child RecyclerView
    class ConnectorViewHolder extends RecyclerView.ViewHolder {

        //TextView ChildItemTitle;

        ImageView imageView;
        TextView type;
        TextView type_full;
        TextView volt;
        TextView amp;
        TextView power;
        TextView last_update_date;
        TextView last_update_time;


        ConnectorViewHolder(View itemView) {
            super(itemView);
            //ChildItemTitle = itemView.findViewById(R.id.child_item_title);

            imageView = itemView.findViewById(R.id.connector_image);
            type = itemView.findViewById(R.id.connector_type);
            type_full = itemView.findViewById(R.id.connector_full_type);
            volt = itemView.findViewById(R.id.connector_volt);
            amp = itemView.findViewById(R.id.connector_amp);
            power = itemView.findViewById(R.id.connector_power);
            last_update_date = itemView.findViewById(R.id.connector_last_updated_date);
            last_update_time = itemView.findViewById(R.id.connector_last_updated_time);
        }
    }
}

