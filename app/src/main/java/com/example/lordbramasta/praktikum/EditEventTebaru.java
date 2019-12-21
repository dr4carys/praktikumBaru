package com.example.lordbramasta.praktikum;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import androidx.appcompat.app.AppCompatActivity;

public class EditEventTebaru extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    NetworkImageView thumb_image;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    SwipeRefreshLayout swipe;
    ProgressDialog pDialog;
    Button btn_register, btn_login,btn_register_client,btn_up_image;
    EditText txt_username, txt_password, txt_confirm_password,txt_no_hp,txt_name;
    private static final String url_detail  = Server.URL + "detail_news.php";
    Intent intent;
    ImageView imageView;
    Bitmap bitmap, decoded;
    String id_news,id,idEvent,idUser;
    SharedPreferences sharedpreferences;
    int success;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60; // range 1 - 100
    ConnectivityManager conMgr;
    TextView txt_namaEvent,txt_lokasiEvent,txt_deskripsiEvent,txt_maxOrang,txt_noHp_event,txt_tanggal_event;
    ImageView imageViewEvent;
    private String url = Server.URL + "update_event.php";
    public static final String TAG_ID       = "id";
    public static final String TAG_USER_ID  = "user_id";
    public static final String TAG_JUDUL    = "judul";
    public static final String TAG_TGL      = "tgl";
    public static final String TAG_ISI      = "isi";
    public static final String TAG_GAMBAR   = "gambar";
    public static final String TAG_MAXORANG  = "max_orang";
    private static final String TAG = EditEventTebaru.class.getSimpleName();
    int reg=0,reg1=1;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private String KEY_IMAGE = "image";
    Boolean session = false;
    String tag_json_obj = "json_obj_req";
    String id123, username;
    public static final String session_status = "session_status";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editevent);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }
        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        id123 = sharedpreferences.getString(TAG_ID, null);
        id_news = getIntent().getStringExtra(TAG_ID);
        thumb_image = (NetworkImageView) findViewById(R.id.gambar_event);
        txt_namaEvent = findViewById(R.id.txt_namaEvent);
        txt_lokasiEvent= findViewById(R.id.txt_lokasiEvent);
        txt_deskripsiEvent= findViewById(R.id.txt_deskripsiEvent);
        txt_maxOrang= findViewById(R.id.txt_maxOrang);
        txt_noHp_event= findViewById(R.id.txt_noHp_Event);
        txt_tanggal_event=findViewById(R.id.txt_tanggalEvent);
        swipe       = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_event);
        imageViewEvent = (ImageView) findViewById(R.id.imageEvent);
        Button btn_up_image_event=(Button) findViewById(R.id.btn_up_image_event);
        Button btn_uploadEvenet= findViewById(R.id.btn_UploadEvent);
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
//        Toast.makeText(this, id_news, Toast.LENGTH_SHORT).show();
        btn_up_image_event.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
//        btn_login.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                intent = new Intent(EditEventTebaru.this, Login.class);
//                finish();
//                startActivity(intent);
//            }
//        });

        btn_uploadEvenet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String namaEvent = txt_namaEvent.getText().toString();
                String deskripsiEvent = txt_deskripsiEvent.getText().toString();
                int maxOrang = Integer.parseInt(txt_maxOrang.getText().toString());
                int tanggal_event= Integer.parseInt(txt_tanggal_event.getText().toString());
                int statusEvent=1;
                int no_hp_event = Integer.parseInt(txt_noHp_event.getText().toString());
                String lokasi = txt_lokasiEvent.getText().toString();
                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {
                    checkRegister(namaEvent,deskripsiEvent,no_hp_event,statusEvent,maxOrang,tanggal_event,lokasi);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });


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
    private void checkRegister(final String namaEvent,final String deskripsiEvent, final int no_hp_event,final int statusEvent, final int maxOrang,final int tanggal_event,final String lokasi) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Register ...");
        showDialog();

//        if(reg==1){
//            url=Server.URL + "tambahClient.php";
//        }
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

//                        Log.e("berhasil cuk", jObj.toString());

                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

//                        txt_username.setText("");
//                        txt_password.setText("");
//                        txt_confirm_password.setText("");

//                        intent = new Intent(EditEventTebaru.this, EditEventTebaru.class);
//                        finish();
//                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(),
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
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("no_hp", String.valueOf(no_hp_event));
                params.put("status_event", String.valueOf(statusEvent));
                params.put("max_orang", String.valueOf(maxOrang));
                params.put("nama_event", namaEvent);
                params.put("tanggal_event",String.valueOf(tanggal_event));
                params.put("lokasi",lokasi);
                params.put("desc_event",deskripsiEvent);
                params.put("image",getStringImage(decoded));
                params.put("id_user",String.valueOf(id123));
                params.put("id_news",String.valueOf(id_news));
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //mengambil fambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 512));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        thumb_image.setImageBitmap(decoded);
    }

    // fungsi resize image
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
    public void onBackPressed() {
        intent = new Intent(EditEventTebaru.this, Login.class);
        finish();
        startActivity(intent);
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
                    String max_orang      = obj.getString(TAG_MAXORANG);
                    String lokasi_event     = obj.getString("lokasi");
                    String no_hp1 = obj.getString("no_hp");

                    txt_namaEvent.setText(Judul);
                    txt_tanggal_event.setText(Tgl);
                    txt_deskripsiEvent.setText(Html.fromHtml(Isi));
                    txt_maxOrang.setText(max_orang);
                    txt_lokasiEvent.setText(lokasi_event);
                    txt_noHp_event.setText(no_hp1);

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
                Toast.makeText(EditEventTebaru.this,
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
    public void onRefresh() {
        callDetailNews(id_news);
    }
}