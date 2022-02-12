package com.example.foodapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.foodapp.Database.Database;
import com.example.foodapp.Interface.ItemClickListener;
import com.example.foodapp.Model.Food;
import com.example.foodapp.Model.Order;
import com.example.foodapp.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    String categoryId="";

    FirebaseRecyclerAdapter<Food, FoodViewHolder>adapter;
    Database localDB;

    SwipeRefreshLayout swipeRefreshLayout;


    /*@Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_food_list);
        database=FirebaseDatabase.getInstance();
        foodList=database.getReference("Food");

        //Local DB

        localDB=new Database(this);

        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getIntent() !=null)
                    categoryId=getIntent().getStringExtra("CategoryId");
                if (!categoryId.isEmpty() && categoryId!=null)
                {
                    loadListFood(categoryId);
                }

            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (getIntent() !=null)
                    categoryId=getIntent().getStringExtra("CategoryId");
                if (!categoryId.isEmpty() && categoryId!=null)
                {
                    loadListFood(categoryId);
                }
            }
        });

        recyclerView=(RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);




    }

    private void loadListFood(String categoryId) {
        adapter =new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,R.layout.food_item,FoodViewHolder.class,foodList.orderByChild("MenuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Food food, int i) {
                foodViewHolder.food_name.setText(food.getName());
                foodViewHolder.food_price.setText(String.format("$ %s",food.getPrice().toString()));
                Picasso.with(getBaseContext()).load(food.getImage())
                        .into(foodViewHolder.food_image);

                //Quick cart
                foodViewHolder.quickcart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Database(getBaseContext()).addToCart(new Order(
                                adapter.getRef(i).getKey(),
                                food.getName(),
                                "1",
                                food.getPrice(),
                                food.getDiscount()
                        ));
                        Toast.makeText(FoodList.this,"Added to Cart",Toast.LENGTH_SHORT).show();
                    }

                });

                //Add Favorites
                if (localDB.isFavourite(adapter.getRef(i).getKey()))
                    foodViewHolder.fav_image.setImageResource(R.drawable.ic_baseline_favorite_24);

                //Click to change state of Favourites
                foodViewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!localDB.isFavourite(adapter.getRef(i).getKey()))
                        {
                            localDB.addToFavourites(adapter.getRef(i).getKey());
                            foodViewHolder.fav_image.setImageResource(R.drawable.ic_baseline_favorite_24);
                            Toast.makeText(FoodList.this,""+food.getName()+"was added to Favourites",Toast.LENGTH_SHORT).show();

                        }else{
                            localDB.removeFromFavourites(adapter.getRef(i).getKey());
                            foodViewHolder.fav_image.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                            Toast.makeText(FoodList.this,""+food.getName()+"was removed to Favourites",Toast.LENGTH_SHORT).show();

                        }

                    }
                });


                final Food local=food;
                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetail=new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());
                        startActivity(foodDetail);

                    }
                });



            }
        };
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

    }
}