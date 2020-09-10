package com.example.culs.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.culs.R;
import com.example.culs.helpers.CustomAdapter;
import com.example.culs.helpers.Notification;
import com.example.culs.helpers.PostType;
import com.example.culs.helpers.Sponsor;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationsFragment extends Fragment {

    private RecyclerView notificationsView;
    private View rootView;
    private CustomAdapter customAdapter;
    private FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
    private List<PostType> types = new ArrayList<>();
    private ImageView backbtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        setupCustomAdapter(rootView);

        backbtn = (ImageView) rootView.findViewById(R.id.back_button);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment nextFragment = new HomeFragment();

                Bundle bundle = new Bundle();
                nextFragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, nextFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        types.clear();
        getListItems();
        customAdapter.notifyDataSetChanged();
    }

    public void onStop() {
        super.onStop();
    }

    private void setupCustomAdapter(View rootView) {
        notificationsView = (RecyclerView) rootView.findViewById(R.id.notification_list);
        notificationsView.setHasFixedSize(true);
        notificationsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        customAdapter = new CustomAdapter(types);
        notificationsView.setAdapter(customAdapter);
    }

    private void getListItems() {

        ArrayList<String> notifsArrayList = HomeFragment.currentUser.getMyevents();
        notifsArrayList.add(HomeFragment.currentUser.getUid());
        String notifsList[] = notifsArrayList.toArray(new String[0]);

        mFirebaseFirestore.collection("notifications").whereArrayContainsAny("receiverID", Arrays.asList(notifsList)).orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            System.err.println("Listen failed:" + error);
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Notification notificationAdded = dc.getDocument().toObject(Notification.class);
                                    notificationAdded.setNotificationID(dc.getDocument().getId());
                                    types.add(notificationAdded);
                                    customAdapter.notifyItemInserted(dc.getNewIndex());
                                    break;
                                case MODIFIED:
                                    types.remove(dc.getOldIndex());
                                    customAdapter.notifyItemRemoved(dc.getOldIndex());
                                    Notification notificationChanged = dc.getDocument().toObject(Notification.class);
                                    notificationChanged.setNotificationID(dc.getDocument().getId());
                                    types.add(notificationChanged);
                                    customAdapter.notifyItemChanged(dc.getNewIndex());
                                    break;
                                case REMOVED:
                                    types.remove(dc.getOldIndex());
                                    customAdapter.notifyItemRemoved(dc.getOldIndex());
                                    break;
                                default:
                                    break;
                            }

                        }
                    }
                });
    }

}
