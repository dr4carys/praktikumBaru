package com.example.lordbramasta.praktikum.adapterOff;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.lordbramasta.praktikum.AppDatabase;
import com.example.lordbramasta.praktikum.R;
import com.example.lordbramasta.praktikum.app.AppController;
import com.example.lordbramasta.praktikum.dataEvent;

import java.util.ArrayList;

import androidx.room.Room;

public class NewsAdapterOff extends BaseAdapter {
    //Deklarasi Variable
    private ArrayList<dataEvent> daftarMahasiswa;
    private AppDatabase appDatabase;
    private Context context;
    private Activity activity;
    private LayoutInflater inflater;
    ImageLoader imageLoader;

    public NewsAdapterOff(ArrayList<dataEvent> daftarMahasiswa) {

        //Inisialisasi data yang akan digunakan
        this.daftarMahasiswa = daftarMahasiswa;
        this.context = context;
        appDatabase = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class, "dbMahasiswa").allowMainThreadQueries().build();
    }


    @Override
    public int getCount() {
        return daftarMahasiswa.size();
    }

    @Override
    public Object getItem(int location) {
        return daftarMahasiswa.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_news, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.news_gambar);
        TextView judul = (TextView) convertView.findViewById(R.id.news_judul);
        TextView timestamp = (TextView) convertView.findViewById(R.id.news_timestamp);
        TextView isi = (TextView) convertView.findViewById(R.id.news_isi);
        TextView max =(TextView) convertView.findViewById(R.id.max_orang);
        TextView noHp=(TextView) convertView.findViewById(R.id.noHp);
        String getIsi = daftarMahasiswa.get(position).getDescEvent();
        String getJudul = daftarMahasiswa.get(position).getNamaEvent();
        String getTanggal = daftarMahasiswa.get(position).getTanggalEvent();

//        thumbNail.setImageUrl(news.getGambar(), imageLoader);
        judul.setText(getJudul);
        timestamp.setText(getTanggal);
        isi.setText(getIsi);
//        noHp.setText(news.getNoHp());
//        max.setText(news.getMax());

        return convertView;
    }
}
