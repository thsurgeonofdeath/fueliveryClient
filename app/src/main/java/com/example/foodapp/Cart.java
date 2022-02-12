package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodapp.Common.Common;
import com.example.foodapp.Database.Database;
import com.example.foodapp.Model.Order;
import com.example.foodapp.Model.Request;
import com.example.foodapp.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Cart extends AppCompatActivity {
     RecyclerView recyclerView;
     RecyclerView.LayoutManager layoutManager;

     FirebaseDatabase database;
     DatabaseReference requests;

     TextView txtTotalPrice;
     Button btnPlaceOrder;

     List<Order> cart=new ArrayList<>();
     CartAdapter adapter;

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

        setContentView(R.layout.activity_cart);

        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        recyclerView=(RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
        txtTotalPrice=(TextView) findViewById(R.id.total);
        btnPlaceOrder=(Button) findViewById(R.id.btnPlaceOrder);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.size() >0)
                     showAlertDialog();
                else
                    Toast.makeText(Cart.this,"Your Cart is empty!!",Toast.LENGTH_SHORT).show();



            }

            
        });
        
        loadListFood();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more Step!");
        alertDialog.setMessage("Enter your address");

        final EditText edtAddress=new EditText(Cart.this);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        edtAddress.setLayoutParams(lp);
        alertDialog.setView(edtAddress);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request request=new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        cart

                );
                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this,"Thank you,Order Placed successfully",Toast.LENGTH_SHORT).show();
                finish();


            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    };

    private void loadListFood() {
        cart =new Database(this).getCarts();
        adapter=new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //Calculate Total
        int total=0;
        for (Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale=new Locale("en","US");
        NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected( MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int order) {
        cart.remove(order);
        new Database(this).cleanCart();
        for (Order item:cart)
            new Database(this).addToCart(item);
        loadListFood();
    }
}