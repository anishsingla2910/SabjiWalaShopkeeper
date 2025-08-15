package com.prototypes.sabjiwalashopkeeper.ui.order_history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prototypes.sabjiwalashopkeeper.R;
import com.prototypes.sabjiwalashopkeeper.classes.Orders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    ArrayList<Orders> orders;
    onOrderClickListener listener;

    public OrderHistoryAdapter(ArrayList<Orders> orders){
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_history_layout, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.customer_name.setText(orders.get(position).getCustomer_name());
        holder.customer_address.setText(orders.get(position).getCustomer_address());

        Timestamp timestamp = orders.get(position).order_date;
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd:mm:yyyy hh-mm-ss");
        String orderDate = sdf.format(date);
        holder.order_date.setText(orderDate);
        StorageReference fStorage = FirebaseStorage.getInstance().getReference();
        StorageReference customerImage = fStorage.child("customer_pictures/" + orders.get(position).getCustomer_id());
        Glide.with(holder.customer_image)
                .load(customerImage)
                .error(R.drawable.default_profile)
                .into(holder.customer_image);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView customer_name, customer_address, order_date;
        ImageView customer_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customer_name = itemView.findViewById(R.id.customer_name);
            customer_image = itemView.findViewById(R.id.customer_image);
            customer_address = itemView.findViewById(R.id.customer_address);
            order_date = itemView.findViewById(R.id.order_date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onClick(orders.get(position).getId());
                    }
                }
            });
        }
    }

    public interface onOrderClickListener{
        void onClick(String path);
    }

    public void setOnOrderClickListener(onOrderClickListener onOrderClickListener){
        this.listener = onOrderClickListener;
    }
}
