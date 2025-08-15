package com.prototypes.sabjiwalashopkeeper.ui.current_orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prototypes.sabjiwalashopkeeper.R;
import com.prototypes.sabjiwalashopkeeper.classes.Orders;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentOrdersAdapter extends FirestoreRecyclerAdapter<Orders, CurrentOrdersAdapter.CurrentOrdersHolder> {

    OnOrderClickedListener listener;

    public CurrentOrdersAdapter(@NonNull FirestoreRecyclerOptions<Orders> options, OnOrderClickedListener listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull CurrentOrdersHolder holder, int i, @NonNull Orders orders) {
        holder.total_amount.setText(orders.getTotal_amount());
        holder.customer_name.setText(orders.getCustomer_name());
        holder.customer_address.setText(orders.getCustomer_address());
        Timestamp timestamp = orders.getOrder_date();
        Date date = timestamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String orderDate = dateFormat.format(date);
        holder.order_date.setText(orderDate);
        holder.order_status.setText(orders.getOrder_status());
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("customer_pictures/" + orders.getCustomer_id());
        Glide.with(holder.customer_image)
                .load(reference)
                .error(R.drawable.default_profile)
                .into(holder.customer_image);
    }

    @NonNull
    @Override
    public CurrentOrdersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
        return new CurrentOrdersHolder(view);
    }

    class CurrentOrdersHolder extends RecyclerView.ViewHolder {
        TextView order_status, customer_address, customer_name, total_amount, order_date;
        ImageView customer_image;

        public CurrentOrdersHolder(@NonNull View itemView) {
            super(itemView);
            order_status = itemView.findViewById(R.id.order_status);
            order_date = itemView.findViewById(R.id.order_date);
            customer_address = itemView.findViewById(R.id.customer_address);
            customer_name = itemView.findViewById(R.id.customer_name);
            customer_image = itemView.findViewById(R.id.customer_image);
            total_amount = itemView.findViewById(R.id.total_amount);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.orderClicked(getSnapshots().getSnapshot(position).getId());
                    }
                }
            });
        }
    }

    public interface OnOrderClickedListener {
        void orderClicked(String path);
    }
}
