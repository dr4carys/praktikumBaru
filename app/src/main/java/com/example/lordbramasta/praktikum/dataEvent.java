package com.example.lordbramasta.praktikum;


import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbEvent")
public class dataEvent implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int eventId;

    @ColumnInfo(name = "nama_event")
    public String namaEvent;

    @ColumnInfo(name = "tanggal_event")
    public String tanggalEvent;

    @ColumnInfo(name = "desc_event")
    public String descEvent;

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getNamaEvent() {
        return namaEvent;
    }

    public String getDescEvent() {
        return descEvent;
    }

    public void setNamaEvent(String namaEvent) {
        this.namaEvent = namaEvent;
    }

    public void setDescEvent(String descEvent) {
        this.descEvent = descEvent;
    }

    public String getTanggalEvent() {
        return tanggalEvent;
    }

    public void setTanggalEvent(String tanggalEvent) {
        this.tanggalEvent = tanggalEvent;
    }
}
