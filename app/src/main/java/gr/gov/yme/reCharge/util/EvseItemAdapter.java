package gr.gov.yme.reCharge.util;


import gr.gov.yme.R;
import gr.gov.yme.reCharge.models.location.Evse;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EvseItemAdapter extends RecyclerView.Adapter<EvseItemAdapter.EvseViewHolder> {

    String TAG = "EvseItemAdapter";
    // An object of RecyclerView.RecycledViewPool
    // is created to share the Views
    // between the child and
    // the parent RecyclerViews
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<Evse> evseItemList;
    private Context context;

    public EvseItemAdapter(Context context, List<Evse> evseItemList) {
        this.context = context;
        this.evseItemList = evseItemList;
    }

    @NonNull
    @Override
    public EvseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        // Here we inflate the corresponding
        // layout of the parent item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.parent_item, viewGroup, false);
        return new EvseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EvseViewHolder evseViewHolder, int position) {

        // Create an instance of the evseItem
        // class for the given position
        Evse evseItem = evseItemList.get(position);

        // For the created instance,
        // get the title and set it
        // as the text for the TextView


        //todo write setText for everything

        evseViewHolder.evse_id.setText(evseItem.evseId);


        if (evseItem.status.compareTo("AVAILABLE") == 0) {
            evseViewHolder.status.setText("Διαθέσιμο");
        } else if (evseItem.status.compareTo("RESERVED") == 0) {
            evseViewHolder.status.setText("Κατηλειμμένο");
        } else {
            evseViewHolder.status.setText(evseItem.status);
        }

        if(evseItem.capabilities!=null) {
            if (evseItem.capabilities.get(0).compareTo("AVAILABLE") == 0) {
                evseViewHolder.reservable.setText("Διαθέσιμο");
            } else if (evseItem.capabilities.get(0).compareTo("RESERVED") == 0) {
                evseViewHolder.reservable.setText("Κατηλειμμένο");
            } else {
                evseViewHolder.reservable.setText(evseItem.capabilities.get(0));
            }
        }else {
            evseViewHolder.reservable.setText("N/A");
        }

        String dateNtime = evseItem.lastUpdated.toLowerCase(Locale.ROOT).replace("t"," ");
        String newDate = getNewDate(dateNtime);
        Log.i(TAG,newDate);
        evseViewHolder.last_update.setText(newDate);


        GridLayoutManager layoutManager = new GridLayoutManager(context, 1);


        //LinearLayoutManager layoutManager = new LinearLayoutManager(evseViewHolder.ChildRecyclerView.getContext(),LinearLayoutManager.VERTICAL, false);

        // Since this is a nested layout, so
        // to define how many child items
        // should be prefetched when the
        // child RecyclerView is nested
        // inside the parent RecyclerView,
        // we use the following method
        layoutManager.setInitialPrefetchItemCount(
                evseItem
                        .getConnectorItemList()
                        .size());

        // Create an instance of the child
        // item view adapter and set its
        // adapter, layout manager and RecyclerViewPool
        ConnectorItemAdapter connectorItemAdapter = new ConnectorItemAdapter(evseItem.getConnectorItemList());


        evseViewHolder.ChildRecyclerView.setLayoutManager(layoutManager);
        evseViewHolder.ChildRecyclerView.setHasFixedSize(true);

        evseViewHolder.ChildRecyclerView.setAdapter(connectorItemAdapter);

        evseViewHolder.ChildRecyclerView.setRecycledViewPool(viewPool);
    }


    @Override
    public int getItemCount() {

        if (evseItemList == null) {
            return 0;
        } else {
            return evseItemList.size();
        }


    }

    public String getNewDate(String dateAndTime){

        SimpleDateFormat oldFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        oldFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        String dueDateAsNormal ="";
        try {
            value = oldFormatter.parse(dateAndTime);
            SimpleDateFormat newFormatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss ");

            newFormatter.setTimeZone(TimeZone.getDefault());
            dueDateAsNormal = newFormatter.format(value);
        } catch (ParseException e) {
            Log.e(TAG,e.getMessage());
            e.printStackTrace();
        }

        return dueDateAsNormal;
    }


    class EvseViewHolder
            extends RecyclerView.ViewHolder {

        private RecyclerView ChildRecyclerView;

        private TextView evse_id;
        private TextView status;
        private TextView reservable;
        private TextView last_update;


        EvseViewHolder(final View itemView) {
            super(itemView);

            ChildRecyclerView = itemView.findViewById(R.id.child_recyclerview);
            evse_id = itemView.findViewById(R.id.evse_evse_id);
            status = itemView.findViewById(R.id.evse_status);
            reservable = itemView.findViewById(R.id.evse_reservation);
            last_update = itemView.findViewById(R.id.evse_last_updated_date);


        }
    }
}
