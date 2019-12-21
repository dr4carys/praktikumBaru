package com.example.lordbramasta.praktikum.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.lordbramasta.praktikum.AppDatabase;
import com.example.lordbramasta.praktikum.DetailNews;
import com.example.lordbramasta.praktikum.Login;
import com.example.lordbramasta.praktikum.R;
import com.example.lordbramasta.praktikum.Server;
import com.example.lordbramasta.praktikum.adapter.NewsAdapter;
import com.example.lordbramasta.praktikum.app.AppController;
import com.example.lordbramasta.praktikum.data.NewsData;
import com.example.lordbramasta.praktikum.dataEvent;
import com.example.lordbramasta.praktikum.detailPendaftar;
import com.example.lordbramasta.praktikum.eventDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class DashboardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private DashboardViewModel dashboardViewModel;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    ListView list;
    SwipeRefreshLayout swipe;
    List<NewsData> newsList = new ArrayList<NewsData>();
    ArrayList<dataEvent> datsEvent2 = new ArrayList<dataEvent>();
    ArrayList<String> datsEvent = new ArrayList<String>();
    private static final String TAG = DashboardFragment.class.getSimpleName();

    private static String url_list   = Server.URL + "news.php?offset=";

    private int offSet = 0;

    int no;

    NewsAdapter adapter;

    public static final String TAG_NO       = "no";
    public static final String TAG_ID       = "id";
    public static final String TAG_JUDUL    = "judul";
    public static final String TAG_TGL      = "tgl";
    public static final String TAG_ISI      = "isi";
    public static final String TAG_GAMBAR   = "gambar";
    public static final String TAG_MAX      = "max";
    public static final String TAG_NOHP     = "noHp";

    Handler handler;
    Runnable runnable;
    ConnectivityManager conMgr;
    String id_user, username,url_gabung;
    String tag_json_obj = "json_obj_req";
    int tombol1=0;
    private SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences sharedpreferences;
    Boolean session = false;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";
    private AppDatabase db;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        sharedpreferences = requireContext().getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        id_user = sharedpreferences.getString(TAG_ID, null);
//        username = sharedpreferences.getString(TAG_USERNAME, null);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Button btn_event_all= root.findViewById(R.id.btn_event_all);
        Button btn_event_id = root.findViewById(R.id.btn_event_id);
        swipe = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
        list = (ListView) root.findViewById(R.id.list_news);
        conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        newsList.clear();
        db = Room.databaseBuilder(getActivity(),
                AppDatabase.class, "eventDB").allowMainThreadQueries().build();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                if(tombol1==0){
                    Intent intent = new Intent(getActivity(), DetailNews.class);
                    intent.putExtra(TAG_ID, newsList.get(position).getId());
                    startActivity(intent);

                }else{
                    Intent intent = new Intent(getActivity(), detailPendaftar.class);
                    intent.putExtra(TAG_ID, newsList.get(position).getId());
                    startActivity(intent);
                }
            }
        });
        adapter = new NewsAdapter(getActivity(), newsList);
        list.setAdapter(adapter);
        btn_event_all.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tombol1=0;
                callNews(0,1);
                newsList.clear();
                adapter.notifyDataSetChanged();
            }
        });
        btn_event_id.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tombol1=1;
                newsList.clear();
                adapter.notifyDataSetChanged();
                callNews(0,0);

            }
        });

        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
                       @Override
                       public void run() {
                           swipe.setRefreshing(true);
                           newsList.clear();
                           adapter.notifyDataSetChanged();
//                           callNews(0);
                           if (conMgr.getActiveNetworkInfo() != null
                                   && conMgr.getActiveNetworkInfo().isAvailable()
                                   && conMgr.getActiveNetworkInfo().isConnected()) {
                               deleteData();
                               callNews(0,1);
                           } else {
                               callNewsOff();
                           }
                       }
                   }
        );
        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;
            }
            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {

                    swipe.setRefreshing(true);
                    handler = new Handler();

                    runnable = new Runnable() {
                        public void run() {
                            callNews(offSet,1);
                        }
                    };

                    handler.postDelayed(runnable, 3000);
                }
            }

        });
//        final TextView txt_id = root.findViewById(R.id.txt_id);
//        final TextView txt_username = root.findViewById(R.id.txt_username);
//        final Button btn_logout= root.findViewById(R.id.btn_logout);
//        txt_id.setText("ID : " + id);
//        txt_username.setText("USERNAME : " + username);
//        btn_logout.setOnClickListener(this);
//

//        dashboardViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//
//            }
//        });
        return root;
    }

    @Override
    public void onRefresh() {
        newsList.clear();
        adapter.notifyDataSetChanged();
//        callNews(0);
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
//            deleteData();
            callNews(0,1);
        } else {
            callNewsOff();
        }
    }

    private void callNews(int page,int jenis){
        if(jenis == 0){
            url_list= Server.URL + "ambilVolunteer.php?offset=";
            url_gabung = url_list + page + "&id="+id_user;
        }else{
            url_list=Server.URL + "news.php?offset=";
            url_gabung = url_list + page ;
        }
        swipe.setRefreshing(true);
        // Creating volley request obj
        JsonArrayRequest arrReq = new JsonArrayRequest( url_gabung,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        if (response.length() > 0) {
                            // Parsing json
                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    dataEvent b = new dataEvent();
                                    JSONObject obj = response.getJSONObject(i);
//                                    Log.e("anjing",""+response);
                                    NewsData news = new NewsData();

                                    no = obj.getInt(TAG_NO);

                                    news.setId(obj.getString(TAG_ID));
                                    news.setJudul(obj.getString(TAG_JUDUL));
                                    news.setNoHp(obj.getString(TAG_NOHP));
                                    news.setMax(obj.getString(TAG_MAX));

                                    b.setNamaEvent(obj.getString(TAG_JUDUL));
                                    b.setDescEvent(obj.getString(TAG_ISI));
                                    b.setEventId(Integer.parseInt(obj.getString(TAG_ID)));
                                    b.setTanggalEvent(obj.getString(TAG_TGL));
                                    insertData(b);
//                                    loadData();

                                    if (obj.getString(TAG_GAMBAR) != "") {
                                        news.setGambar(obj.getString(TAG_GAMBAR));
                                    }

                                    news.setDatetime(obj.getString(TAG_TGL));
                                    news.setIsi(obj.getString(TAG_ISI));

                                    // adding news to news array
                                    newsList.add(news);

                                    if (no > offSet)
                                        offSet = no;

                                    Log.d(TAG, "offSet " + offSet);

                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }

                                // notifying list adapter about data changes
                                // so that it renders the list view with updated data
                                adapter.notifyDataSetChanged();
                            }
                        }
                        swipe.setRefreshing(false);
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                swipe.setRefreshing(false);
            }
        });
                // Adding request to request queue
        AppController.getInstance().addToRequestQueue(arrReq);
    }
    private void callNewsOff(){

              eventDAO dataVent = db.EventDAO();
              List<dataEvent> items = dataVent.getAllItem();
//              Toast.makeText(getActivity(),items.get(2).getDescEvent(),Toast.LENGTH_SHORT).show();
              for(int i=0;i<items.size();i++){
                  String descEvent = items.get(i).getDescEvent();
                  String namaEvent = items.get(i).getNamaEvent();
                  String tanggalEvent =  items.get(i).getTanggalEvent();
                  String idEvent = Integer.toString(items.get(i).getEventId());
                  NewsData news = new NewsData();

                  news.setId(idEvent);
                  news.setJudul(namaEvent);
                  news.setDatetime(tanggalEvent);
                  news.setIsi(descEvent);
                  newsList.add(news);
                  adapter.notifyDataSetChanged();
              }
//              Log.d(TAG, "ini data items" + items);
//              System.out.println('haha');
//            datsEvent2.addAll(Arrays.asList(db.EventDAO().selectAllEvent()));


    }
//    private void loadData(){
//        new AsyncTask<Void, Void, String>(){
//            @Override
//            protected String doInBackground(Void... voids) {
//                datsEvent2.addAll(Arrays.asList(db.EventDAO().selectAllEvent()));
////                datsEvent.add(db.EventDAO().selectAllEvent());
//                String getNIM2 = datsEvent2.get(0).getDescEvent();
////                String getNIM1;
////                if (getNIM2 == true){
////                    getNIM1="true";
////                }else{
////                    getNIM1="false";
////                }
//                return getNIM2;
//            }
//            protected void onPostExecute(String getNIM2){
//                Toast.makeText(getActivity(),getNIM2,Toast.LENGTH_SHORT).show();
//            }
//        }.execute();
//    }
    private void insertData(final dataEvent eventData){

        new AsyncTask<Void, Void, Long>(){
            @Override
            protected Long doInBackground(Void... voids) {
                // melakukan proses insert data
                long status = db.EventDAO().insertEvent(eventData);
                return status;
            }

            @Override
            protected void onPostExecute(Long status) {
                Toast.makeText(getActivity(), "status row "+status, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void deleteData(){

        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... voids) {
                // melakukan proses insert data
                db.EventDAO().delete();
                String status="coba coba";
                return status;
            }

            @Override
            protected void onPostExecute(String status) {
                Toast.makeText(getActivity(), "status row "+status, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}


