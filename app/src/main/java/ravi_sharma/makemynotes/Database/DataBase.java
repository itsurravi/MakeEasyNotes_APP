package ravi_sharma.makemynotes.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {

    public static final String DB_NAME = "NotesDB";
    public static final String TABLE_NAME = "MyNotes";
    public static final String TABLE_NAME2 = "Reminder";
    public static final String ID = "id";
    public static final String note_title = "title";
    public static final String note = "note";
    public static final String time = "time";


    public DataBase(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME
                + "(" + ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + note_title + " VARCHAR, "
                + note + " TEXT, "
                + time + " VARCHAR);";
        db.execSQL(sql);
        sql = "CREATE TABLE " + TABLE_NAME2
                + "(" + ID +
                " INTEGER);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS " + TABLE_NAME2;
        db.execSQL(sql);
        onCreate(db);
    }


    public void addNote(String title, String note, String time)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(note_title, title);
        cv.put(this.note, note);
        cv.put(this.time, time);

        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

    public void updateNote(String id, String title, String note, String time)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(note_title, title);
        cv.put(this.note, note);
        cv.put(this.time, time);

        db.update(TABLE_NAME, cv, ID+"=?", new String[]{id});
        db.close();
    }

    public void deleteNote(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ID+"=?", new String[]{id});
        db.close();
    }

    public Cursor viewNote()
    {
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "select * from "+TABLE_NAME;
            Cursor c = db.rawQuery(sql, null);
            return c;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public void addReminder(int num)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(ID, num);

        db.insert(TABLE_NAME2, null, cv);
        db.close();
    }

    public Cursor viewReminder()
    {
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "select * from "+TABLE_NAME2;
            Cursor c = db.rawQuery(sql, null);
            return c;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
