package ravi_sharma.makemynotes.Activities;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ravi_sharma.makemynotes.R;

public class ViewNotesActivity extends AppCompatActivity {

    TextView note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);

        Bundle b = getIntent().getExtras();
        String title = b.getString("title");
        String notes = b.getString("note");

        setTitle(title);
        note = (TextView)findViewById(R.id.note);

        note.setText(notes);
    }
}
