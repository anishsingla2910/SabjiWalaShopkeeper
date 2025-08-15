package com.prototypes.sabjiwalashopkeeper.ui.order_history;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prototypes.sabjiwalashopkeeper.MainActivity;
import com.prototypes.sabjiwalashopkeeper.R;
import com.prototypes.sabjiwalashopkeeper.classes.Orders;
import com.prototypes.sabjiwalashopkeeper.order_history.OrderHistoryActivity;

import java.util.ArrayList;

public class OrderHistoryFragment extends Fragment {

    FirebaseFirestore fStore;
    OrderHistoryAdapter adapter;
    RecyclerView recyclerView;
    FirebaseAuth fAuth;
    String path;
    ArrayList<Orders> orders;

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    public static OrderHistoryFragment newInstance(String param1, String param2) {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).setTitle(getString(R.string.order_history));

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        //path = fAuth.getCurrentUser().getUid();
        path = "IUvAYRhZfahjB5mh6a7yL1znfU72";
        orders = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);
        fStore.collection("Orders")
                .whereEqualTo("sabjiwala_id", path)
                .orderBy("order_date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            Orders order = documentSnapshot.toObject(Orders.class).withId(documentSnapshot.getId());
                            orders.add(order);
                        }
                        adapter = new OrderHistoryAdapter(orders);
                        adapter.setOnOrderClickListener(new OrderHistoryAdapter.onOrderClickListener() {
                            @Override
                            public void onClick(String path) {
                                Intent intent = new Intent(getContext(), OrderHistoryActivity.class);
                                intent.putExtra("path", path);
                                startActivity(intent);
                            }
                        });
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(adapter);
                    }
                });
    }
}