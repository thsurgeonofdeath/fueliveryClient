package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.foodapp.Common.Common;
import com.example.foodapp.Database.Database;
import com.example.foodapp.Model.Food;
import com.example.foodapp.Model.Order;
import com.example.foodapp.Model.Rating;
import com.example.foodapp.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    TextView food_name,food_price,food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnRating;
    CounterFab btnCart;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;


    String foodId="";
    Food currentFood;

    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingTbl;
    Button btnShowComment;

   /* @Override
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

        setContentView(R.layout.activity_food_detail);

        btnShowComment=(Button)findViewById(R.id.btnShowComment);
        btnShowComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(FoodDetail.this,ShowComment.class);
                intent.putExtra(Common.INTENT_FOOD_ID,foodId);
                startActivity(intent);
            }
        });

         database=FirebaseDatabase.getInstance();
         foods=database.getReference("Food");
         ratingTbl=database.getReference("Rating");

         numberButton=(ElegantNumberButton) findViewById(R.id.number_button);
         btnCart=(CounterFab) findViewById(R.id.btnCart);
         btnRating=(FloatingActionButton)findViewById(R.id.btn_rating);
         ratingBar=(RatingBar)findViewById(R.id.ratingBar);
         
         
         btnRating.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 showRatingDialog();
             }
         });

         btnCart.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                  new Database(getBaseContext()).addToCart(new Order(
                          foodId,
                          currentFood.getName(),
                          numberButton.getNumber(),
                          currentFood.getPrice(),
                          currentFood.getDiscount()
                  ));
                 Toast.makeText(FoodDetail.this,"Added to Cart",Toast.LENGTH_SHORT).show();
             }
         });
         btnCart.setCount(new Database(this).getCountCart());

         food_description=(TextView) findViewById(R.id.food_description);
         food_name=(TextView) findViewById(R.id.food_name);
         food_price=(TextView) findViewById(R.id.food_price);
         food_image=(ImageView) findViewById(R.id.img_food);

         collapsingToolbarLayout=(CollapsingToolbarLayout) findViewById(R.id.collapsing);
         collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandAppbar);
         collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

         //Get Food Id from Intent
        if (getIntent()!=null)
            foodId=getIntent().getStringExtra("FoodId");
        if (!foodId.isEmpty())
        {
            getDetailFood(foodId);
            getRatingFood(foodId);
        }

    }

    private void getRatingFood(String foodId) {
        Query foodRating=ratingTbl.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot:snapshot.getChildren())
                {
                    Rating item=postSnapshot.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;
                }
               if (count !=0)
               {
                   float average=sum/count;
                   ratingBar.setRating(average);
               }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("very Bad","Not Good","Quite Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please give your rating and your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your feedback here.....")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();

    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentFood=snapshot.getValue(Food.class);

                Picasso.with(getBaseContext()).load(currentFood.getImage())
                        .into(food_image);
                collapsingToolbarLayout.setTitle(currentFood.getName());
                food_price.setText(currentFood.getPrice());
                food_name.setText(currentFood.getName());
                food_description.setText(currentFood.getDescription());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {


    }

    @Override
    public void onPositiveButtonClicked(int value, @NonNull String s) {
        //Get Rating and upload to firebase
        Rating rating=new Rating(Common.currentUser.getPhone(),
                foodId,
                String.valueOf(value),
                s);
        ratingTbl.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FoodDetail.this,"Thank you for your rating!!",Toast.LENGTH_SHORT).show();

                    }
                });
        /*ratingTbl.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(Common.currentUser.getPhone()).exists())
                {
                    ratingTbl.child(Common.currentUser.getPhone()).removeValue();

                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                }
                else
                {
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);
                }
                Toast.makeText(FoodDetail.this,"Thank you for your rating!!",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });*/

    }
}