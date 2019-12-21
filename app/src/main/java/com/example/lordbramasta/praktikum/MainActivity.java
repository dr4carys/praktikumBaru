//package com.example.lordbramasta.praktikum;
//
//import android.os.Bundle;
//import android.content.Context;
//import android.content.Intent;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import android.content.SharedPreferences;
//import androidx.appcompat.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.view.MenuItem;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//
//public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener  {
//
//    Button btn_logout;
//    TextView txt_id, txt_username;
//    String id, username;
//    SharedPreferences sharedpreferences;
//
//    public static final String TAG_ID = "id";
//    public static final String TAG_USERNAME = "username";
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        loadFragment(new HomeFragment());
//        // inisialisasi BottomNavigaionView
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bn_main);
//        // beri listener pada saat item/menu bottomnavigation terpilih
//        bottomNavigationView.setOnNavigationItemSelectedListener(this);
////        txt_id = (TextView) findViewById(R.id.txt_id);
////        txt_username = (TextView) findViewById(R.id.txt_username);
////        btn_logout = (Button) findViewById(R.id.btn_logout);
//
////        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
//
////        id = getIntent().getStringExtra(TAG_ID);
////        username = getIntent().getStringExtra(TAG_USERNAME);
////
////        txt_id.setText("ID : " + id);
////        txt_username.setText("USERNAME : " + username);
//
////        btn_logout.setOnClickListener(new View.OnClickListener() {
////
////            @Override
////            public void onClick(View v) {
////                // TODO Auto-generated method stub
////                // update login session ke FALSE dan mengosongkan nilai id dan username
////                SharedPreferences.Editor editor = sharedpreferences.edit();
////                editor.putBoolean(Login.session_status, false);
////                editor.putString(TAG_ID, null);
////                editor.putString(TAG_USERNAME, null);
////                editor.commit();
////
////                Intent intent = new Intent(MainActivity.this, Login.class);
////                finish();
////                startActivity(intent);
////            }
////        });
//
//    }
//
//    private boolean loadFragment(Fragment fragment){
//        if (fragment != null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fl_container, fragment)
//                    .commit();
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        Fragment fragment = null;
//        switch (menuItem.getItemId()){
//            case R.id.home_menu:
//                fragment = new HomeFragment();
//                break;
//            case R.id.search_menu:
//                fragment = new SearchFragment();
//                break;
//            case R.id.account_menu:
//                fragment = new AccountFragment();
//                break;
//        }
//        return loadFragment(fragment);
//    }
//}