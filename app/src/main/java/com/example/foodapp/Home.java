package com.example.foodapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.foodapp.Common.Common;
import com.example.foodapp.Database.Database;
import com.example.foodapp.Interface.ItemClickListener;
import com.example.foodapp.Model.Banner;
import com.example.foodapp.Model.Category;
import com.example.foodapp.Service.ListenOrder;
import com.example.foodapp.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodapp.databinding.ActivityHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    FirebaseDatabase database;
    DatabaseReference category;
    TextView txtFullName;
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    CounterFab fab;

    //Slider
    HashMap<String,String>image_list;
    SliderLayout mSlider;


//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());



        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Setup Slider



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");

        setSupportActionBar(binding.appBarHome.toolbar);

        //View
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMenu();

            }
        });
        //Default load for first time

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadMenu();

            }
        });


        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        Paper.init(this);
        fab =(CounterFab)findViewById(R.id.fab) ;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);
            }
        });
        fab.setCount(new Database(this).getCountCart());


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        // NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        // NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        // NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());

        recycler_menu = (RecyclerView) findViewById(R.id.recycler_menu);
        recycler_menu.setLayoutManager(new GridLayoutManager(this,2));
        LayoutAnimationController controller= AnimationUtils.loadLayoutAnimation(recycler_menu.getContext(),
                R.anim.layout_fall_down);
        recycler_menu.setLayoutAnimation(controller);


        loadMenu();

        Intent service = new Intent(Home.this, ListenOrder.class);
        startService(service);

        setupSlider();



    }

    private void setupSlider() {
        mSlider=(SliderLayout) findViewById(R.id.slider);
        image_list=new HashMap<>();
        DatabaseReference banner=database.getReference("Banner");

        banner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnaShot:snapshot.getChildren())
                {
                    Banner banner=postSnaShot.getValue(Banner.class);
                    image_list.put(banner.getName()+"@@@"+banner.getId(),banner.getImage());
                }
                for (String key:image_list.keySet())
                {
                    String[] keySplit=key.split("@@@");
                    String nameOfFood=keySplit[0];
                    String idOfFood=keySplit[1];

                    final TextSliderView textSliderView=new TextSliderView(getBaseContext());
                    textSliderView
                            .description(nameOfFood)
                            .image(image_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent=new Intent(Home.this,FoodDetail.class);
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);

                                }
                            });
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("FoodId",idOfFood);

                    mSlider.addSlider(textSliderView);
                    banner.removeEventListener(this);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart());




    }

    @Override
    protected void onStop() {
        super.onStop();
        mSlider.stopAutoCycle();
    }

    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class, category) {
            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, Category category, int i) {
                menuViewHolder.txtMenuName.setText(category.getName());
                Picasso.with(getBaseContext()).load(category.getImage())
                        .into(menuViewHolder.imageView);
                Category clickItem = category;
                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodList = new Intent(Home.this, FoodList.class);
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });


            }
        };

        recycler_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        recycler_menu.getAdapter().notifyDataSetChanged();
        recycler_menu.scheduleLayoutAnimation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_cart:
                Intent cartIntent = new Intent(Home.this, Cart.class);
                startActivity(cartIntent);

                return true;
            case R.id.nav_orders:
                Intent orderIntent = new Intent(Home.this, OrderStatus.class);
                startActivity(orderIntent);
                return true;
            case R.id.nav_log_out:
                Paper.book().destroy();
                Intent signIn = new Intent(Home.this, SignIn.class);
                signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signIn);


            default:
                return super.onOptionsItemSelected(item);


        }
    }


    // @Override
    // public boolean onSupportNavigateUp() {
    // NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
    //return NavigationUI.navigateUp(navController, mAppBarConfiguration)
    //|| super.onSupportNavigateUp();
    //}



}