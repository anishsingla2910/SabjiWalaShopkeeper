package com.prototypes.sabjiwalashopkeeper.ui.current_orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.prototypes.sabjiwalashopkeeper.MainActivity;
import com.prototypes.sabjiwalashopkeeper.R;
import com.prototypes.sabjiwalashopkeeper.classes.Orders;
import com.prototypes.sabjiwalashopkeeper.order_view.OrderViewActivity;

public class CurrentOrdersFragment extends Fragment {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userId;
    RecyclerView recyclerView;
    CurrentOrdersAdapter adapter;
    RelativeLayout relativeLayout;

    public CurrentOrdersFragment() {
        // Required empty public constructor
    }

    public static CurrentOrdersFragment newInstance() {
        CurrentOrdersFragment fragment = new CurrentOrdersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).setTitle(getString(R.string.current_orders));
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        //userId = FirebaseAuth.getInstance().getUid();
        userId = "IUvAYRhZfahjB5mh6a7yL1znfU72";
        recyclerView = view.findViewById(R.id.recycler_view);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        Query query = fStore.collection("Orders")
                .whereEqualTo("sabjiwala_id", userId)
                .whereEqualTo("order_status", "new")
                .orderBy("order_date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Orders> options = new FirestoreRecyclerOptions.Builder<Orders>()
                .setQuery(query, Orders.class)
                .build();
        adapter = new CurrentOrdersAdapter(options, new CurrentOrdersAdapter.OnOrderClickedListener() {
            @Override
            public void orderClicked(String path) {
                fStore.collection("Orders")
                        .document(path)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                String order_status = task.getResult().getString("order_status");
                                if (order_status.equals("new")) {
                                    Intent intent = new Intent(getContext(), OrderViewActivity.class);
                                    intent.putExtra("path", path);
                                    startActivity(intent);
                                }
                            }
                        });
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