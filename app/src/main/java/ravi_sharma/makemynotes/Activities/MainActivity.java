package ravi_sharma.makemynotes.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ravi_sharma.makemynotes.Adapter.Listadap;
import ravi_sharma.makemynotes.Database.DataBase;
import ravi_sharma.makemynotes.R;
import ravi_sharma.makemynotes.Model.notesfile;

public class MainActivity extends AppCompatActivity implements Listadap.OnItemLongClicked, Listadap.OnItemClicked {

    FloatingActionButton fl;
    RecyclerView listView;

    List<notesfile> noteobj;

    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DataBase(this);

        listView = (RecyclerView) findViewById(R.id.list);
        listView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(manager);
        listView.setItemAnimator(new DefaultItemAnimator());

        noteobj = new ArrayList<>();

        fl = (FloatingActionButton) findViewById(R.id.floating);
        fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, notes.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new notesFetch().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.item1:
                startActivity(new Intent(MainActivity.this, ReminderActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class notesFetch extends AsyncTask<Void, Void, ArrayList<notesfile>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<notesfile> doInBackground(Void... voids) {
            ArrayList<notesfile> data = new ArrayList<>();
            Cursor c = db.viewNote();
            if (c == null) {
                return null;
            } else {
                while (c.moveToNext()) {
                    String id, title, note, time;
                    id = c.getString(c.getColumnIndex(DataBase.ID));
                    title = c.getString(c.getColumnIndex(DataBase.note_title));
                    note = c.getString(c.getColumnIndex(DataBase.note));
                    time = c.getString(c.getColumnIndex(DataBase.time));
                    notesfile n = new notesfile(id, title, note, time);
                    data.add(n);
                }
                c.close();

                return data;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<notesfile> notesfiles) {
            super.onPostExecute(notesfiles);

            if (notesfiles != null) {
                noteobj = notesfiles;

                Collections.sort(noteobj, new Comparator<notesfile>() {
                    @Override
                    public int compare(notesfile o1, notesfile o2) {
                        return o2.getTime().compareToIgnoreCase(o1.getTime());
                    }
                });

                Listadap adapter = new Listadap(MainActivity.this, noteobj);
                adapter.setOnLongClick(MainActivity.this);
                adapter.setOnClick(MainActivity.this);
                listView.setAdapter(adapter);
            }

        }
    }

    private void showUpdateDeleteDialog(final String notesId, final String titlename, final String notesname, final String time) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdate);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDelete);

        dialogBuilder.setTitle("Change Note");
        final AlertDialog b = dialogBuilder.create();
        b.show();


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, notes.class);
                i.putExtra("id", notesId);
                i.putExtra("title", titlename);
                i.putExtra("note", notesname);
                i.putExtra("time", time);
                startActivity(i);
                b.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteNote(notesId);
                b.dismiss();
            }
        });


    }

    private boolean deleteNote(String id) {

        db.deleteNote(id);

        new notesFetch().execute();

        Toast.makeText(getApplicationContext(), "Note Deleted", Toast.LENGTH_LONG).show();

        return true;
    }

    @Override
    public void onItemClick(int position) {
        notesfile abc = noteobj.get(position);
        Intent i = new Intent(MainActivity.this, ViewNotesActivity.class);
        i.putExtra("title", abc.getTitle());
        i.putExtra("note", abc.getData());
        startActivity(i);
    }

    @Override
    public void onItemLongClick(int position) {
        notesfile abc = noteobj.get(position);
        showUpdateDeleteDialog(abc.getId(), abc.getTitle(), abc.getData(), abc.getTime());
    }
}


