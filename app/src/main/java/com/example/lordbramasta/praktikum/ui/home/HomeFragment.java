package com.example.lordbramasta.praktikum.ui.home;

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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.lordbramasta.praktikum.Login;
import com.example.lordbramasta.praktikum.R;
import com.example.lordbramasta.praktikum.Server;
import com.example.lordbramasta.praktikum.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements View.OnClickListener {
    ProgressDialog pDialog;
    Bitmap bitmap, decoded;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private String url = Server.URL + "buatEvent.php";
    String tag_json_obj = "json_obj_req";
    private static final String TAG = HomeFragment.class.getSimpleName();
    int success;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60; // range 1 - 100
    private SwipeRefreshLayout swipeRefreshLayout;
    ConnectivityManager conMgr;
    String id, username;
    SharedPreferences sharedpreferences;
    String[] hehe;
    String haha;
    ImageView imageViewEvent;
    private String KEY_IMAGE = "image";
    Integer id3;
    Boolean session = false;
    Button btn_logout,btn_up_image_event;
    TextView txt_namaEvent,txt_lokasiEvent,txt_deskripsiEvent,txt_maxOrang,txt_noHp_event,txt_tanggal_event;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";
    public static final String TAG_ID = "id";
    public static final String TAG_USERNAME = "username";
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_home, container, false);
        conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        sharedpreferences = requireContext().getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        id = sharedpreferences.getString(TAG_ID, null);
        username = sharedpreferences.getString(TAG_USERNAME, null);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        txt_namaEvent = root.findViewById(R.id.txt_namaEvent);
        txt_lokasiEvent= root.findViewById(R.id.txt_lokasiEvent);
        txt_deskripsiEvent= root.findViewById(R.id.txt_deskripsiEvent);
        txt_maxOrang= root.findViewById(R.id.txt_maxOrang);
        txt_noHp_event= root.findViewById(R.id.txt_noHp_Event);
        txt_tanggal_event=root.findViewById(R.id.txt_tanggalEvent);
        imageViewEvent = (ImageView) root.findViewById(R.id.imageEvent);
        btn_up_image_event=(Button) root.findViewById(R.id.btn_up_image_event);
        Button btn_uploadEvenet= root.findViewById(R.id.btn_UploadEvent);
//        txt_id.setText("ID : " + id);
//        txt_username.setText("USERNAME : " + username);
        btn_up_image_event.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        btn_uploadEvenet.setOnClickListener(this);
        return root;
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

    @Override
    public void onClick(View v){
        String namaEvent = txt_namaEvent.getText().toString();
        String deskripsiEvent = txt_deskripsiEvent.getText().toString();
        int maxOrang = Integer.parseInt(txt_maxOrang.getText().toString());
        int tanggal_event= Integer.parseInt(txt_tanggal_event.getText().toString());
        int statusEvent=1;
        int no_hp_event = Integer.parseInt(txt_noHp_event.getText().toString());
        String lokasi = txt_lokasiEvent.getText().toString();
//        Toast.makeText(getActivity(), id, Toast.LENGTH_SHORT).show();
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            inputEvent(namaEvent,deskripsiEvent,no_hp_event,statusEvent,maxOrang,tanggal_event,lokasi);
        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void inputEvent(final String namaEvent,final String deskripsiEvent, final int no_hp_event,final int statusEvent, final int maxOrang,final int tanggal_event,final String lokasi ) {
//        JSONObject objec = new JSONObject();
//        obj.put("id1",id1)
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
//                Log.e(TAG, "Login Response: " + response.toString());
//                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
//                        SharedPreferences.Editor editor = sharedpreferences.edit();
//                        editor.putBoolean(Login.session_status, true);
//                        editor.putString(TAG_ID, jObj.getString(TAG_ID));
//                        editor.putString(TAG_USERNAME, jObj.getString(TAG_USERNAME));
//                        editor.commit();

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

//                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                HashMap<String, String> params = new HashMap<>();
                params.put("no_hp", String.valueOf(no_hp_event));
                params.put("status_event", String.valueOf(statusEvent));
                params.put("max_orang", String.valueOf(maxOrang));
                params.put("nama_event", namaEvent);
                params.put("tanggal_event",String.valueOf(tanggal_event));
                params.put("lokasi",lokasi);
                params.put("desc_event",deskripsiEvent);
                params.put("image",getStringImage(decoded));
                params.put("id_user",String.valueOf(id));

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //mengambil fambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePath);
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
        imageViewEvent.setImageBitmap(decoded);
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




}