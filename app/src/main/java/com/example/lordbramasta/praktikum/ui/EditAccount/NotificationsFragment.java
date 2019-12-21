package com.example.lordbramasta.praktikum.ui.EditAccount;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.example.lordbramasta.praktikum.Login;
import com.example.lordbramasta.praktikum.R;
import com.example.lordbramasta.praktikum.Server;
import com.example.lordbramasta.praktikum.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class NotificationsFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener,View.OnClickListener{
    NetworkImageView thumb_image;
    SwipeRefreshLayout swipe;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private NotificationsViewModel notificationsViewModel;
    ProgressDialog pDialog;
    ConnectivityManager conMgr;
    Bitmap bitmap, decoded;
    public static final String TAG_ID       = "id";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private String url = Server.URL + "editProfile.php";
    int success;
    public static final String TAG_GAMBAR   = "gambar";
    private static final String TAG = NotificationsFragment.class.getSimpleName();
    //    Button btn_logout;
    TextView txt_id, txt_username,txt_password,txt_nama;
//    Button btn_edit;
    String id,username;
    String tag_json_obj = "json_obj_req";
    SharedPreferences sharedpreferences;
    Boolean session = false;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60;
    private static final String url_detail  = Server.URL + "detailAkun.php";
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";

    public static final String TAG_NAMA = "nama";
    public static final String TAG_USERNAME = "username";
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        conMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getActivity(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        sharedpreferences = requireContext().getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        swipe       = (SwipeRefreshLayout)root.findViewById(R.id.swipe_refresh_edit_account);
        id = sharedpreferences.getString(TAG_ID, null);
        username = sharedpreferences.getString(TAG_USERNAME, null);
        txt_username = root.findViewById(R.id.tv_username);
        txt_nama = root.findViewById(R.id.tv_name);
        thumb_image = (NetworkImageView)root.findViewById(R.id.thumb_image);
        gantiShared(id,username);
        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
                       @Override
                       public void run() {
                           if (!id.isEmpty()) {
                               callDetailNews(id);
                           }
                       }
                   }
        );
        Button btn_edit= root.findViewById(R.id.btn_edit);
        btn_edit= root.findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(this);
        Button btn_logout= root.findViewById(R.id.btn_logout);
//        btn_logout.setOnClickListener(this);
//        btn_logout.setOnClickListener(this);
        btn_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(Login.session_status, false);
                editor.putString(TAG_ID, null);
                editor.putString(TAG_USERNAME, null);
                editor.commit();
                Intent intent = new Intent(getActivity(), Login.class);
                getActivity().finish();
                startActivity(intent);
            }
        });

//
//        final TextView textView = root.findViewById(R.id.text_notifications);
//        notificationsViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }
    public void gantiShared(String id, String username){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(session_status, true);
        editor.putString(TAG_ID, id);
        editor.putString(TAG_USERNAME, username);
        editor.commit();
    }
    @Override
    public void onClick(View v) {
        String username = txt_username.getText().toString();
        String password = txt_password.getText().toString();
        Integer id1= Integer.parseInt(id);
        // mengecek kolom yang kosong
        if (username.trim().length() > 0 && password.trim().length() > 0) {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                checkLogin(id1,username, password);
            } else {
                Toast.makeText(getActivity() ,"No Internet Connection", Toast.LENGTH_LONG).show();
            }
        } else {
            // Prompt user to enter credentials
            Toast.makeText(getActivity() ,"Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
        }
//        SharedPreferences.Editor editor = sharedpreferences.edit();
//        editor.putBoolean(Login.session_status, false);
//        editor.putString(TAG_ID, null);
//        editor.putString(TAG_USERNAME, null);
//        editor.commit();
//        Intent intent = new Intent(getActivity(), Login.class);
//        getActivity().finish();
//        startActivity(intent);
    }
    private void callDetailNews(final String id){
        swipe.setRefreshing(true);

        StringRequest strReq = new StringRequest(Request.Method.POST, url_detail, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response " + response.toString());
                swipe.setRefreshing(false);

                try {
                    JSONObject obj = new JSONObject(response);


                    String Nama    = obj.getString(TAG_NAMA);
                    String Gambar1   = obj.getString(TAG_GAMBAR);
                    String Username    = obj.getString(TAG_USERNAME);

                    txt_username.setText(Username);
                    txt_nama.setText(Nama);
//                    txt_deskripsiEvent.setText(Html.fromHtml(Isi));
//                    txt_maxOrang.setText(max_orang);
//                    txt_lokasiEvent.setText(lokasi_event);
//                    txt_noHp_event.setText(no_hp1);

                    if (obj.getString(TAG_GAMBAR)!=""){
                        thumb_image.setImageUrl(Gambar1, imageLoader);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Detail News Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                swipe.setRefreshing(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
    private void checkLogin(final Integer id1,final String username, final String password) {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        pDialog.setMessage("proses update");
        showDialog();
//        JSONObject objec = new JSONObject();
//        obj.put("id1",id1)
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        Log.e("berhasil di update", jObj.toString());

                        Toast.makeText(getActivity(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getActivity(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "update error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("id", String.valueOf(id1));

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
    public String getStringImage(Bitmap bmp) {
//        if (bmp != null){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
//        }
//        else{
//            String kosong="kosong";
//            return kosong;
//        }

    }
    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        thumb_image.setImageBitmap(decoded);
    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onRefresh() {
        callDetailNews(id);
    }
}