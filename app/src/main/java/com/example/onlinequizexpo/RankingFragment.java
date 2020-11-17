package com.example.onlinequizexpo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinequizexpo.Interface.ItemClickListener;
import com.example.onlinequizexpo.Interface.RankingCallback;
import com.example.onlinequizexpo.Model.QuestionScore;
import com.example.onlinequizexpo.Model.Ranking;
import com.example.onlinequizexpo.ViewHolder.RankingViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class RankingFragment extends Fragment {

    View myFragment;

    RecyclerView rankingList;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<Ranking, RankingViewHolder> adapter;
    FirebaseDatabase database;
    DatabaseReference questionSore, rankingTbl;
    int sum=0;

    public RankingFragment() {
        // Required empty public constructor
    }

    public static androidx.fragment.app.Fragment newInstance(){
        RankingFragment rankingFragment = new RankingFragment();
        return rankingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        questionSore = database.getReference("Question_Score");
        rankingTbl = database.getReference("Ranking");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.fragment_ranking, container, false);

        //Init View
        rankingList = myFragment.findViewById(R.id.rankingList);
        layoutManager = new LinearLayoutManager(getActivity());
        rankingList.setHasFixedSize(true);

        // beacuse orderByChild method of firebase will sort list with ascending
        //so we need to reverse our recycler data by layoutManager
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rankingList.setLayoutManager(layoutManager);

        updateScore(Common.currentUser.getUserName(), new RankingCallback<Ranking>() {
            @Override
            public void callBack(Ranking ranking) {
//                Update to ranking table
                rankingTbl.child(ranking.getUserName())
                        .setValue(ranking);
                //showRanking(); //After upload, we will sort ranking table and show result
            }
        });

        //sett adapter
        adapter= new FirebaseRecyclerAdapter<Ranking, RankingViewHolder>(
                Ranking.class,
                R.layout.layout_ranking,
                RankingViewHolder.class,
                rankingTbl.orderByChild("score")
        ) {
            @Override
            protected void populateViewHolder(RankingViewHolder rankingViewHolder, final Ranking ranking, int i) {
                rankingViewHolder.txt_name.setText(ranking.getUserName());
                rankingViewHolder.txt_score.setText(String.valueOf(ranking.getScore()));

                //Fixed crash when click to item
                rankingViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLong) {
                        Intent scoreDetail = new Intent(getActivity(), ScoreDetail.class);
                        scoreDetail.putExtra("viewUser", ranking.getUserName());
                        startActivity(scoreDetail);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        rankingList.setAdapter(adapter);
        return myFragment;
    }



    private void updateScore(final String userName, final RankingCallback<Ranking> callback) {
        questionSore.orderByChild("user").equalTo(userName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren() ){
                            QuestionScore ques = data.getValue(QuestionScore.class);
                            sum+=Integer.parseInt(ques.getScore());
                        }
                        //After sum of all score, we need to process sum variable here
                        //Because firebase is async db, if we process outside our 'sum' value will be reset to 0
                        Ranking ranking = new Ranking(userName,sum);
                        callback.callBack(ranking);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}
