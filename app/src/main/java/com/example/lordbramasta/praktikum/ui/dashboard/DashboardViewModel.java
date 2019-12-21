package com.example.lordbramasta.praktikum.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;

import com.example.lordbramasta.praktikum.Login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    Button btn_logout;
    TextView txt_id, txt_username;
    String id, username;
    String tag_json_obj = "json_obj_req";

    SharedPreferences sharedpreferences;
    Boolean session = false;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";

    public static final String TAG_ID = "id";
    public static final String TAG_USERNAME = "username";


    private MutableLiveData<String> mText;
    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}