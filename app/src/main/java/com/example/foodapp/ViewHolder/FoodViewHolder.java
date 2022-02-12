package com.example.foodapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.Interface.ItemClickListener;
import com.example.foodapp.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView food_name,food_price;
    public ImageView food_image,fav_image,quickcart;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

        food_name=(TextView) itemView.findViewById(R.id.food_name);
        food_image=(ImageView) itemView.findViewById(R.id.food_image);
        fav_image=(ImageView) itemView.findViewById(R.id.fav);
        food_price=(TextView)itemView.findViewById(R.id.food_price) ;
        quickcart=(ImageView)itemView.findViewById(R.id.btn_quick_cart);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);

    }
}
