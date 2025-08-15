package com.prototypes.sabjiwalashopkeeper.ui.orders_ready;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.prototypes.sabjiwalashopkeeper.MainActivity;
import com.prototypes.sabjiwalashopkeeper.R;
import com.prototypes.sabjiwalashopkeeper.classes.Orders;
import com.prototypes.sabjiwalashopkeeper.order_ready.OrderReadyActivity;

public class OrdersReadyFragment extends Fragment {

    FirebaseFirestore fStore;
    OrdersReadyAdapter adapter;
    RecyclerView recyclerView;

    public OrdersReadyFragment() {
        // Required empty public constructor
    }

    public static OrdersReadyFragment newInstance(String param1, String param2) {
        OrdersReadyFragment fragment = new OrdersReadyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders_ready, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).setTitle(getString(R.string.order_ready));

        recyclerView = view.findViewById(R.id.recyclerView);

        //String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userId = "IUvAYRhZfahjB5mh6a7yL1znfU72";
        fStore = FirebaseFirestore.getInstance();
        Query query = fStore.collection("Orders")
                .whereEqualTo("sabjiwala_id", userId)
                .whereEqualTo("order_status", "Order ready")
                .orderBy("order_date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Orders> recyclerOptions = new FirestoreRecyclerOptions.Builder<Orders>()
                .setQuery(query, Orders.class)
                .build();
        adapter = new OrdersReadyAdapter(recyclerOptions);
        adapter.setOnOrderClickListener(new OrdersReadyAdapter.onOrderClickListener() {
            @Override
            public void onClick(String path) {
                Intent intent = new Intent(getContext(), OrderReadyActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}