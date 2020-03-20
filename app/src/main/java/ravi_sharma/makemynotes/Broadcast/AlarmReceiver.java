package ravi_sharma.makemynotes.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ravi_sharma.makemynotes.Activities.Remider;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg1) {
        Intent i = new Intent(context, Remider.class);
        i.putExtra("reminder", arg1.getStringExtra("value"));
        context.startActivity(i);
    }

}