package com.example.foodapp.ViewHolder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.Common.Common;
import com.example.foodapp.Database.Database;
import com.example.foodapp.FoodDetail;
import com.example.foodapp.FoodList;
import com.example.foodapp.Interface.ItemClickListener;
import com.example.foodapp.Model.Favorites;
import com.example.foodapp.Model.Food;
import com.example.foodapp.Model.Order;
import com.example.foodapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoriteViewHolder> {
    private Context context;
    private List<Favorites> favoritesList;

    public FavoritesAdapter(Context context, List<Favorites> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     View view = LayoutInflater.from(context)
             .inflate(R.layout.favorites_item,parent,false);
     return  new FavoriteViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder foodViewHolder, @SuppressLint("RecyclerView") int position) {
        foodViewHolder.food_name.setText(favoritesList.get(position).getFoodName());
        foodViewHolder.food_price.setText(String.format("$ %s",favoritesList.get(position).getFoodPrice().toString()));
        Picasso.with(context).load(favoritesList.get(position).getFoodImage())
                .into(foodViewHolder.food_image);

        //Quick cart
        foodViewHolder.quickcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(context).addToCart(new Order(
                        favoritesList.get(position).getFoodId(),
                        favoritesList.get(position).getFoodName(),
                        "1",
                        favoritesList.get(position).getFoodPrice(),
                        favoritesList.get(position).getFoodDiscount()

                ));
                Toast.makeText(context,"Added to Cart",Toast.LENGTH_SHORT).show();
            }

        });






        final Favorites local=favoritesList.get(position);
        foodViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent foodDetail=new Intent(context, FoodDetail.class);
                foodDetail.putExtra("FoodId",favoritesList.get(position).getFoodId());
                context.startActivity(foodDetail);

            }
        });


    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }
}
