package ravi_sharma.makemynotes.Activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ravi_sharma.makemynotes.Broadcast.AlarmReceiver;
import ravi_sharma.makemynotes.Database.DataBase;
import ravi_sharma.makemynotes.R;
import ravi_sharma.makemynotes.Model.notesfile;

public class notes extends AppCompatActivity {

    EditText e1;
    EditText e2;
    Button b;
    ImageButton rec;

    DataBase db;

    String id, title, note;
    Bundle i;
    Calendar calendar = Calendar.getInstance();
    TimePickerDialog timePickerDialog;
    Calendar calSet;
    int y, m, d;

    boolean check = false;

    boolean data;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        e1 = (EditText) findViewById(R.id.editText1);
        e2 = (EditText) findViewById(R.id.editText2);
        b = (Button) findViewById(R.id.savebutton);
        rec = (ImageButton) findViewById(R.id.record);

        db = new DataBase(this);

        try {

            i = getIntent().getExtras();

            if (!i.isEmpty()) {
                id = i.getString("id");
                title = i.getString("title");
                note = i.getString("note");
                e1.setText(title);
                e2.setText(note);
            }

            data = true;
        } catch (Exception e) {
            data = false;
        }

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check) {
                    openDatePickerDialog();
                } else if (!check) {
                    addNotes();
                }
            }
        });

        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.switchmenu, menu);
        MenuItem item = menu.findItem(R.id.myswitch);
        item.setActionView(R.layout.switch_layout);

        Switch mySwitch = item.getActionView().findViewById(R.id.switchForActionBar);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check = true;
                } else if (!isChecked) {
                    check = false;
                }
            }
        });
        return true;
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
                    e2.setText(result.get(0));
                }
                break;
            }

        }
    }

    private void addNotes() {

        String s1 = e1.getText().toString();
        String s2 = e2.getText().toString();

        if (!TextUtils.isEmpty(s1) || !TextUtils.isEmpty(s2)) {

            if (!data) {


                String time = String.valueOf(System.currentTimeMillis());

                db.addNote(s1, s2, time);

                e1.setText("");
                e2.setText("");

                Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
                finish();

            } else if (data) {
                String time = String.valueOf(System.currentTimeMillis());

                db.updateNote(id, s1, s2, time);

                e1.setText("");
                e2.setText("");

                Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (TextUtils.isEmpty(s1)) {
            Toast.makeText(this, "Title field cannot be empty", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Please enter your note to save", Toast.LENGTH_LONG).show();
        }
    }

    private void openDatePickerDialog() {
        DatePickerDialog dialog = new DatePickerDialog(
                notes.this,
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
                notes.this,
                onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                is24r);
        timePickerDialog.setTitle("Set Alarm Time");

        timePickerDialog.show();

    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

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

            String msg = (String.valueOf(calSet.getTime()));

            AlertDialog.Builder builder = new AlertDialog.Builder(notes.this);
            builder.setCancelable(false);
            builder.setTitle("Set Reminder");
            builder.setMessage("Do you want to set this note as a reminder on\n\n"+ msg);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setAlarm(calSet);
                    dialog.cancel();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog d = builder.create();
            d.show();
        }
    };

    private void setAlarm(Calendar targetCal) {
        int num = 1000;
        Cursor c = db.viewReminder();
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                num = c.getInt(c.getColumnIndex(DataBase.ID));
            }
        } else {
            num = 1000;
        }

        String s = e2.getText().toString();
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