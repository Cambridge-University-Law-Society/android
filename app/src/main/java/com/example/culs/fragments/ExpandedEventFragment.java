package com.example.culs.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.culs.R;
import com.example.culs.helpers.Card;
import com.example.culs.helpers.GlideApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ExpandedEventFragment extends Fragment {
    Card currentCard;
    TextView exEventName;
    TextView exEventDnT;
    TextView exEventLoc;
    TextView exEventDesc;
    TextView exEventTagNote;

    ImageView exEventPic;
    ImageView exFriendProfs[] = new ImageView[3];
    ImageView exEventTagIcon;
    LinearLayout exEventTagHolder;

    private int highestTag = 0;

    private View rootView;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        rootView = inflater.inflate(R.layout.fragment_expanded_events, container, false);
        exEventName = rootView.findViewById(R.id.ex_event_name_text_view);
        exEventDnT = rootView.findViewById(R.id.ex_event_date_and_time_text_view);
        exEventLoc = rootView.findViewById(R.id.ex_event_location_text_view);
        exEventDesc = rootView.findViewById(R.id.ex_event_description_text_view);
        exEventTagNote = rootView.findViewById(R.id.ex_tag_note);

        exEventPic = rootView.findViewById(R.id.ex_event_pic_image_view);
        exFriendProfs[0] = rootView.findViewById(R.id.ex_event_friend_1_image_view);
        exFriendProfs[1] = rootView.findViewById(R.id.ex_event_friend_2_image_view);
        exFriendProfs[2] = rootView.findViewById(R.id.ex_event_friend_3_image_view);
        exEventTagIcon = rootView.findViewById(R.id.ex_tag_icon);

        exEventTagHolder = rootView.findViewById(R.id.ex_event_tag_holder);

        currentCard = bundle.getParcelable("Current Card");
        Toast.makeText(getActivity(), currentCard.getLocation(), Toast.LENGTH_SHORT).show();

        exEventName.setText(currentCard.getName());
//        exEventDnT.setText(currentCard.getEventDateAndTime());
        exEventLoc.setText(currentCard.getLocation());
        exEventDesc.setText(currentCard.getDescription());
        GlideApp.with(getContext()).load(currentCard.getImageURL()).into(exEventPic);

        return rootView;

    }

}
