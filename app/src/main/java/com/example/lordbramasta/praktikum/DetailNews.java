package com.example.lordbramasta.praktikum;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.example.lordbramasta.praktikum.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Created by Kuncoro on 29/02/2016.
 */
public class DetailNews extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    NetworkImageView thumb_image;
    TextView judul, tgl, isi;
    ProgressDialog pDialog;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    SwipeRefreshLayout swipe;
    String id_news,id,idEvent,idUser;
    SharedPreferences sharedpreferences;
    Boolean session = false;
    ConnectivityManager conMgr;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";
    private static final String TAG = DetailNews.class.getSimpleName();
    int success;
    public static final String TAG_ID       = "id";
    public static final String TAG_USER_ID  = "user_id";
    public static final String TAG_JUDUL    = "judul";
    public static final String TAG_TGL      = "tgl";
    public static final String TAG_ISI      = "isi";
    public static final String TAG_GAMBAR   = "gambar";

    private static final String url_detail  = Server.URL + "detail_news.php";
    private String url_join = Server.URL + "join_event.php";
    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_news);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Detail News");
        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        id = sharedpreferences.getString(TAG_ID, null);
        thumb_image = (NetworkImageView) findViewById(R.id.gambar_news);
        judul       = (TextView) findViewById(R.id.judul_news);
        tgl         = (TextView) findViewById(R.id.tgl_news);
        isi         = (TextView) findViewById(R.id.isi_news);
        swipe       = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        id_news = getIntent().getStringExtra(TAG_ID);
        Button btn_join_event= (Button)findViewById(R.id.btn_join);
        btn_join_event.setOnClickListener(this);
        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
                       @Override
                       public void run() {
                           if (!id_news.isEmpty()) {
                               callDetailNews(id_news);
                           }
                       }
                   }
        );

    }
    @Override
    public void onClick(View v) {
        Integer id1= Integer.parseInt(id);
        Integer id2= Integer.parseInt(idEvent);
        Integer id3= Integer.parseInt(idUser);
        // mengecek kolom yang kosong
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
//            String token = FirebaseInstanceId.getInstance().getToken();
//            FirebaseMessaging.getInstance().subscribeToTopic("news");
//            Log.i("TOKEN", token);
            checkLogin(id2,id1,id3);
        } else {
            Toast.makeText(this ,"No Internet Connection", Toast.LENGTH_LONG).show();
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
    private void checkLogin(final Integer id2,final Integer id1, final Integer id3) {
       pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Logging in ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, url_join, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

//                        Log.e("berhasil cuk", jObj.toString());
                        Toast.makeText(getApplicationContext(), "berhasil terdaftar",
                                Toast.LENGTH_LONG).show();


                    } else {
                        Toast.makeText(getApplicationContext(), "sudah terdaftar",
                            Toast.LENGTH_LONG).show();

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
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                HashMap<String, String> params = new HashMap<>();
                params.put("client_id", String.valueOf(id1));
                params.put("user_id", String.valueOf(id3));
                params.put("id_event", String.valueOf(id2));

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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
                    idEvent= obj.getString(TAG_ID);
                    idUser = obj.getString(TAG_USER_ID);

                    String Judul    = obj.getString(TAG_JUDUL);
                    String Gambar   = obj.getString(TAG_GAMBAR);
                    String Tgl      = obj.getString(TAG_TGL);
                    String Isi      = obj.getString(TAG_ISI);

                    judul.setText(Judul);
                    tgl.setText(Tgl);
                    isi.setText(Html.fromHtml(Isi));

                    if (obj.getString(TAG_GAMBAR)!=""){
                        thumb_image.setImageUrl(Gambar, imageLoader);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Detail News Error: " + error.getMessage());
                Toast.makeText(DetailNews.this,
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

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        callDetailNews(id_news);
    }
}
