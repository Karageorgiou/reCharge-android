package gr.gov.yme.util;


import gr.gov.yme.R;
import gr.gov.yme.models.location.Evse;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class EvseItemAdapter extends RecyclerView.Adapter<EvseItemAdapter.EvseViewHolder> {

    // An object of RecyclerView.RecycledViewPool
    // is created to share the Views
    // between the child and
    // the parent RecyclerViews
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<Evse> evseItemList;

    public EvseItemAdapter(List<Evse> evseItemList) {
        this.evseItemList = evseItemList;
    }

    @NonNull
    @Override
    public EvseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        // Here we inflate the corresponding
        // layout of the parent item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.parent_item, viewGroup, false);   //todo: change parent_item layout file

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


        // Create a layout manager
        // to assign a layout
        // to the RecyclerView.

        // Here we have assigned the layout
        // as LinearLayout with vertical orientation
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                evseViewHolder
                        .ChildRecyclerView
                        .getContext(),
                LinearLayoutManager.VERTICAL,
                false);

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

        evseViewHolder.ChildRecyclerView.setAdapter(connectorItemAdapter);

        evseViewHolder.ChildRecyclerView.setRecycledViewPool(viewPool);
    }

    // This method returns the number
    // of items we have added in the
    // ParentItemList i.e. the number
    // of instances we have created
    // of the ParentItemList
    @Override
    public int getItemCount() {

        if (evseItemList == null) {
            return 0;
        } else {
            return evseItemList.size();
        }


    }

    // This class is to initialize
    // the Views present in
    // the parent RecyclerView
    class EvseViewHolder
            extends RecyclerView.ViewHolder {

        private RecyclerView ChildRecyclerView;

        private TextView evse_id;
        private TextView status;
        private TextView reservable;


        EvseViewHolder(final View itemView) {
            super(itemView);

            ChildRecyclerView = itemView.findViewById(R.id.child_recyclerview);
            evse_id = itemView.findViewById(R.id.evse_evse_id);
            status = itemView.findViewById(R.id.evse_status);
            reservable = itemView.findViewById(R.id.evse_reservation);


        }
    }
}
