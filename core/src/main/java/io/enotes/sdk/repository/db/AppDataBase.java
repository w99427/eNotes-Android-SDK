package io.enotes.sdk.repository.db;

import android.app.Application;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import io.enotes.sdk.repository.db.converter.ValueConverter;
import io.enotes.sdk.repository.db.dao.CardDao;
import io.enotes.sdk.repository.db.dao.MfrDao;
import io.enotes.sdk.repository.db.entity.Card;
import io.enotes.sdk.repository.db.entity.Mfr;

@Database(entities = {Card.class, Mfr.class}, version = 1, exportSchema = false)
@TypeConverters({ValueConverter.class})
public abstract class AppDataBase extends RoomDatabase {
    abstract public CardDao getCardDao();

    abstract public MfrDao getMfrDao();

    public static AppDataBase init(Context application) {
        return Room.databaseBuilder(application, AppDataBase.class, "eNotes.db").build();
    }
}

