package ravi_sharma.makemynotes.Shared_Pref;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    private SharedPreferences sp;
    private SharedPreferences.Editor ed;

    private static String pref_name = "MakeMyNotes";
    private static String email = "eml";
    private static String name = "nm";

    public PrefManager(Context c) {
        sp = c.getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        ed = sp.edit();
    }

    public void setLogin(boolean b, String n) {
        ed.putString(name, n);
        ed.putBoolean(email, b);
        ed.commit();
    }

    public boolean isLoggedIn() {
        return sp.getBoolean(email, false);
    }

    public String getName() {
        return sp.getString(name, "");
    }
}
