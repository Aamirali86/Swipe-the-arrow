package com.techytec.swipethearrow;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class TutorialActivityFragment extends Fragment {

    int position;

    public TutorialActivityFragment() {
    }

    static TutorialActivityFragment init(int val) {
        TutorialActivityFragment truitonFrag = new TutorialActivityFragment();
        // Supply val input as an argument.
        Bundle args = new Bundle();
        args.putInt("val", val);
        truitonFrag.setArguments(args);
        return truitonFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments() != null ? getArguments().getInt("val") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_tutorial, container, false);

        RelativeLayout background = (RelativeLayout) layoutView.findViewById(R.id.background);
        ImageButton btn = (ImageButton) layoutView.findViewById(R.id.btn);
        btn.setTag(position);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                if (pos == 1)
                    play("classic");
                else if(pos == 2) {
                    int highScore = new MyPreferences().retreiveClassicBestScore(getActivity());
                    if(highScore >= 150)
                        play("expert");
                    else
                        play("classic");
                }
                else
                    nextPage();
            }
        });

        if (position == 0) {
            background.setBackgroundResource(R.drawable.tutorial1);
            btn.setImageResource(R.drawable.btn_continue);
        } else if (position == 1) {
            background.setBackgroundResource(R.drawable.tutorial2);
            btn.setImageResource(R.drawable.play_red);
        } else if (position == 2) {
            background.setBackgroundResource(R.drawable.tutorial3);
            btn.setImageResource(R.drawable.play_blue);
        }

        return layoutView;
    }

    private void play(String type) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    private void nextPage() {
        ((TutorialActivity)getActivity()).mPager.setCurrentItem(1);
    }

}
