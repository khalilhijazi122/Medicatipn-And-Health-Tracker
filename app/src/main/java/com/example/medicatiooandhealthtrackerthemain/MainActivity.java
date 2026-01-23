package com.example.medicatiooandhealthtrackerthemain;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.example.medicatiooandhealthtrackerthemain.data.local.AppDatabase;
import com.example.medicatiooandhealthtrackerthemain.data.local.entities.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.DashboardFragment,
                        R.id.MedicationListFragment,R.id.HealthTrackerFragment
                        ,R.id.ReportFragment,R.id.ProfileFragment).build();
       // NavHostFragment navHostFragment =
             //   (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        //if (navHostFragment == null) {
           // throw new RuntimeException("nav_host_fragment not found in activity_main.xml");
       // }

       NavController navController;
        navController = Navigation.findNavController(MainActivity.this,R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(bottomNav, navController);
        User u = new User();
         // أو autoGenerate حسب تعريفك
        u.name = "test";

        db=db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class,
                "medication_db").build();
        new Thread(() -> {
            db.userDao().insert(u);
        }).start();


    }
}
