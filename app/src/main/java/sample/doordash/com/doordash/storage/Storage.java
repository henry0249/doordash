package sample.doordash.com.doordash.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import sample.doordash.com.doordash.domain.Restaurant;

/**
 * Created by Hakeem on 1/21/17.
 */

public class Storage {

    private DoorDashDBHelper mHelper;

    public Storage(Context context) {
        mHelper = new DoorDashDBHelper(context);
    }

    public Observable<List<Restaurant>> getBookmarks() {
        return makeObservable(new Callable<List<Restaurant>>() {
            @Override
            public List<Restaurant> call() throws Exception {
                return getBookmarksInt();
            }
        });
    }

    public Observable<Long> addBookmark(final Restaurant restaurant) {
        return makeObservable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return addBookmarkInt(restaurant);
            }
        });
    }

    public Observable<Boolean> removeBookmark(final Restaurant restaurant){
        return makeObservable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return removeBookmarkInt(restaurant);
            }
        });
    }

    public Observable<Boolean> isFavourite(final Restaurant restaurant){
        return makeObservable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return isFavouriteInt(restaurant);
            }
        });
    }

    private long addBookmarkInt(final Restaurant restaurant) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long newRowId = db.insert(StorageDefinitions.Bookmarks.TABLE_NAME, null, restaurant.toContentValues());
        return newRowId;
    }

    private boolean removeBookmarkInt(final Restaurant restaurant) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String selection = StorageDefinitions.Bookmarks.COLUMN_REMOTE_ID + "=" + restaurant.mId;
        return db.delete(StorageDefinitions.Bookmarks.TABLE_NAME, selection, null) > 0;
    }

    private List<Restaurant> getBookmarksInt() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "
                + StorageDefinitions.Bookmarks.TABLE_NAME, null);

        try {
            List<Restaurant> values = new ArrayList<>(cursor.getCount());

            while (cursor.moveToNext()) {
                Restaurant r = Restaurant.fromCursor(cursor);
                if (r != null) {
                    values.add(r);
                }
            }
            return values;
        } finally {
            cursor.close();
        }
    }

    private boolean isFavouriteInt(Restaurant restaurant){
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + StorageDefinitions.Bookmarks.TABLE_NAME
                + " WHERE "
                + StorageDefinitions.Bookmarks.COLUMN_REMOTE_ID + "=" + restaurant.mId, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private static <T> Observable<T> makeObservable(final Callable<T> func) {
        return Observable.create(
                new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        try {
                            subscriber.onNext(func.call());
                        } catch (Exception ex) {
                            subscriber.onError(ex);
                            Log.e("", "Error reading from the database", ex);
                        }
                    }
                });
    }
}