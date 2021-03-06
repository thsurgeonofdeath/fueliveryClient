package com.example.foodapp.Database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.foodapp.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME="EatItDB.db";
    private static final int DB_VER=1;
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @SuppressLint("Range")
    public List<Order> getCarts()
    {
        SQLiteDatabase db=getReadableDatabase();
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();

        String[] sqlSelect={"ProductName","ProductId","Quantity","Price","Discount"};
        String sqlTable="OrderDetail";

        qb.setTables(sqlTable);
        Cursor c=qb.query(db,sqlSelect,null,null,null,null,null);

        final List<Order> result=new ArrayList<>();
        if (c.moveToFirst())
        {
            do {
                result.add(new Order(c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount"))
                ));
            }while (c.moveToNext());

        }
        return result;
    }

    public void addToCart(Order order)
    {
        SQLiteDatabase db=getReadableDatabase();
        String query =String.format("INSERT INTO OrderDetail(ProductId,ProductName,Quantity,Price,Discount) Values('%s','%s','%s','%s','%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount());
        db.execSQL(query);
    }
    public void cleanCart()
    {
        SQLiteDatabase db=getReadableDatabase();
        String query =String.format("DELETE FROM OrderDetail");
        db.execSQL(query);
    }

    public void addToFavourites(String foodId){
        SQLiteDatabase db=getReadableDatabase();
        String query =String.format("INSERT INTO Favourites(FoodId) VALUES('%s');",foodId);
        db.execSQL(query);
    }
    public void removeFromFavourites(String foodId){
        SQLiteDatabase db=getReadableDatabase();
        String query =String.format("DELETE FROM Favourites WHERE FoodId='%s';",foodId);
        db.execSQL(query);
    }
    public boolean isFavourite(String foodId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favourites WHERE FoodId='%s';", foodId);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public int getCountCart() {
        int count=0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT (*) FROM OrderDetail");
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                count=cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return  count;

    }
}
