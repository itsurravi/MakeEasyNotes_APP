package ravi_sharma.makemynotes.Activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import ravi_sharma.makemynotes.Broadcast.AlarmReceiver;
import ravi_sharma.makemynotes.Database.DataBase;
import ravi_sharma.makemynotes.R;

public class ReminderActivity extends AppCompatActivity {

    EditText text;
    Button choose_time, set_reminder;
    ImageButton rec;
    TextView rm_time;
    TimePickerDialog timePickerDialog;

    Calendar calendar = Calendar.getInstance();
    Calendar calSet;
    int y, m, d;
    DataBase db;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        text = findViewById(R.id.todo);
        choose_time = findViewById(R.id.setTime);
        set_reminder = findViewById(R.id.submit);
        rec = (ImageButton) findViewById(R.id.record);
        rm_time = (TextView)findViewById(R.id.reminder_time);

        db = new DataBase(this);

        choose_time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog();
            }
        });

        set_reminder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm(calSet);
            }
        });

        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record();
            }
        });
    }

    private void record() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    text.setText(result.get(0));
                }
                break;
            }

        }
    }

    private void openDatePickerDialog() {
        DatePickerDialog dialog = new DatePickerDialog(
                ReminderActivity.this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            y = year;
            m = month;
            d = dayOfMonth;
            openTimePickerDialog(false);
        }
    };

    private void openTimePickerDialog(boolean is24r) {

        timePickerDialog = new TimePickerDialog(
                ReminderActivity.this,
                onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                is24r);
        timePickerDialog.setTitle("Set Alarm Time");

        timePickerDialog.show();

    }

    OnTimeSetListener onTimeSetListener = new OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            calSet = (Calendar) calNow.clone();
            calSet.set(Calendar.YEAR, y);
            calSet.set(Calendar.MONTH, m);
            calSet.set(Calendar.DAY_OF_MONTH, d);
            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {

                calSet.add(Calendar.DATE, 1);
            }

            set_reminder.setBackgroundResource(R.color.yellow_back);

            set_reminder.setEnabled(true);

            rm_time.setText(String.valueOf(calSet.getTime()));
        }
    };

    private void setAlarm(Calendar targetCal) {
        int num = 1;
        Cursor c = db.viewReminder();
        if (c != null) {
            while (c.moveToNext()) {
                num = c.getInt(c.getColumnIndex(DataBase.ID));
            }
        } else {
            num = 1;
        }

        String s = text.getText().toString();
        if (!s.isEmpty()) {
            Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
            intent.putExtra("value", s);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), num, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "Reminder is set", Toast.LENGTH_SHORT).show();
            num++;
            db.addReminder(num);
            finish();
        }
        else {
            Toast.makeText(this, "Enter Reminder", Toast.LENGTH_SHORT).show();
        }
    }
}
