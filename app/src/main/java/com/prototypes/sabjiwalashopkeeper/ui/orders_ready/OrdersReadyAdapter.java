package com.prototypes.sabjiwalashopkeeper.ui.orders_ready;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prototypes.sabjiwalashopkeeper.R;
import com.prototypes.sabjiwalashopkeeper.classes.Orders;

public class OrdersReadyAdapter extends FirestoreRecyclerAdapter<Orders, OrdersReadyAdapter.MyViewHolder> {

    onOrderClickListener listener;

    public OrdersReadyAdapter(@NonNull FirestoreRecyclerOptions<Orders> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int i, @NonNull Orders orders) {
        holder.total_amount.setText(orders.getTotal_amount());
        holder.delivery_address.setText(orders.getCustomer_address());
        holder.customer_name.setText(orders.getCustomer_name());
        StorageReference customer_image = FirebaseStorage.getInstance().getReference().child("customer_pictures/" + orders.getCustomer_id());
        Glide.with(holder.customer_image)
                .load(customer_image)
                .error(R.drawable.default_profile)
                .into(holder.customer_image);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ready_order_layout, parent, false);
        return new MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView customer_name, delivery_address, total_amount;
        ImageView customer_image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            customer_name = itemView.findViewById(R.id.customer_name);
            customer_image = itemView.findViewById(R.id.customer_image);
            delivery_address = itemView.findViewById(R.id.delivery_address);
            total_amount = itemView.findViewById(R.id.total_amount);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onClick(getSnapshots().getSnapshot(position).getId());
                    }
                }
            });
        }
    }

    public interface onOrderClickListener{
        void onClick(String path);
    }

    public void setOnOrderClickListener(onOrderClickListener listener) {
        this.listener = listener;
    }
}