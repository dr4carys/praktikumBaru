//package com.example.lordbramasta.praktikum;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//
//
//import androidx.fragment.app.Fragment;
//
//public class HomeFragment extends Fragment {
//
//    private static final String TAG_SUCCESS = "success";
//    private static final String TAG_MESSAGE = "message";
//
//    Button btn_logout;
//    TextView txt_id, txt_username;
//    String id, username;
//    String tag_json_obj = "json_obj_req";
//
//    SharedPreferences sharedpreferences;
//    Boolean session = false;
//    public static final String my_shared_preferences = "my_shared_preferences";
//    public static final String session_status = "session_status";
//
//    public static final String TAG_ID = "id";
//    public static final String TAG_USERNAME = "username";
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//        // Cek session login jika TRUE maka langsung buka MainActivity
//        txt_id = (TextView) view.findViewById(R.id.txt_id);
//        txt_username = (TextView)view.findViewById(R.id.txt_username);
//        btn_logout = (Button) view.findViewById(R.id.btn_logout);
//
//        sharedpreferences = requireContext().getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
//        session = sharedpreferences.getBoolean(session_status, false);
//        id = sharedpreferences.getString(TAG_ID, null);
//        username = sharedpreferences.getString(TAG_USERNAME, null);
//        txt_id.setText("ID : " + id);
//        txt_username.setText("USERNAME : " + username);
//
//        return view;
//
//    }
//}
