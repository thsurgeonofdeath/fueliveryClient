package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodapp.Common.Common;
import com.example.foodapp.Model.Rating;
import com.example.foodapp.ViewHolder.ShowCommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.stepstone.apprating.C;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowComment extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference ratingTbl;

    SwipeRefreshLayout mSwipeRefreshLayout;

    FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder> adapter;
    String foodId="";

   /* @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
        .setDefaultFontPath("fonts/cf.otf")
         .setFontAttrId(R.attr.fontPath)
         .build()
        );*/
        setContentView(R.layout.activity_show_comment);

        database=FirebaseDatabase.getInstance();
        ratingTbl=database.getReference("Rating");

        recyclerView=(RecyclerView) findViewById(R.id.recyclerComment);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        loadComment(foodId);





    }

    private void loadComment(String foodId) {
        adapter=new FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(Rating.class,R.layout.show_comment_layout,ShowCommentViewHolder.class,ratingTbl) {
            @Override
            protected void populateViewHolder(ShowCommentViewHolder showCommentViewHolder, Rating rating, int i) {
                showCommentViewHolder.ratingBar.setRating(Float.parseFloat(rating.getRateValue()));
                showCommentViewHolder.txtComment.setText(rating.getComment());
                showCommentViewHolder.txtUserPhone.setText(rating.getUserPhone());


            }
                      /*@Override
                        public ShowCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view= LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.show_comment_layout,parent,false);
                            return  new ShowCommentViewHolder(view);

                        }*/
        };


        recyclerView.setAdapter(adapter);

    }
}