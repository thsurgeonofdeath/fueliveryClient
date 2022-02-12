package com.example.foodapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.example.foodapp.Common.Common;
import com.example.foodapp.Database.Database;
import com.example.foodapp.Model.Order;
import com.example.foodapp.ViewHolder.FavoritesAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FavoritesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

   FavoritesAdapter adapter;
   RelativeLayout rootLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        rootLayout=(RelativeLayout)findViewById(R.id.root_layout);

        recyclerView=(RecyclerView) findViewById(R.id.recycler_fav);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);







    }
}