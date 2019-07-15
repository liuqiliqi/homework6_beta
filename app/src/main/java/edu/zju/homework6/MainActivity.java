package edu.zju.homework6;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

public class MainActivity extends AppCompatActivity {



    private static final int PAGE_CODE = 9999;
    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;
    private FloatingActionButton button_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("todolist");

        button_add = findViewById(R.id.floatingActionButton);

        button_add.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {;
                startActivityForResult( new Intent(MainActivity.this, NoteActivity.class),PAGE_CODE);
            }
            }

        );

        dbHelper = new TodoDbHelper(this);
        database = dbHelper.getWritableDatabase();

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        recyclerView.addItemDecoration( new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(note.Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(note.Note note) {
                MainActivity.this.updateNode(note);
            }

            @Override
            public void modifyNote(note.Note note) {
                MainActivity.this.modifyNote(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    private void updateNode(note.Note note) {
        int rows = database.delete(TodoContract.TodoNote.TABLE_NAME,
                TodoContract.TodoNote._ID + "=?",
                new String[]{String.valueOf(note.id)});
        if (rows > 0) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private void deleteNote(note.Note note) {
        int rows = database.delete(TodoContract.TodoNote.TABLE_NAME,
                TodoContract.TodoNote._ID + "=?",
                new String[]{String.valueOf(note.id)});
        if (rows > 0) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }

    }
    private void modifyNote(note.Note note) {

        startActivityForResult(new Intent(MainActivity.this, NoteActivity.class),PAGE_CODE);
        int rows = database.delete(TodoContract.TodoNote.TABLE_NAME,TodoContract.TodoNote._ID + "=?",new String[]{String.valueOf(note.id)});
        notesAdapter.refresh(loadNotesFromDatabase());

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAGE_CODE && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long nowDate = 0;
        long newDate2 = 0 ;
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                database.delete(TodoContract.TodoNote.TABLE_NAME,null,null);
                notesAdapter.refresh(loadNotesFromDatabase());
                break;
            case R.id.clearoneday:
                nowDate = System.currentTimeMillis();
                newDate2 = nowDate - (24 * 60 * 60 * 1000);
                database.delete(TodoContract.TodoNote.TABLE_NAME,TodoContract.TodoNote.COLUMN_DATE+"<?",new String[] {String.valueOf(newDate2)});
                notesAdapter.refresh(loadNotesFromDatabase());
                break;
            case R.id.clearoneminutes:
                nowDate = System.currentTimeMillis();
                newDate2 = nowDate - (60 * 1000);
                database.delete(TodoContract.TodoNote.TABLE_NAME,TodoContract.TodoNote.COLUMN_DATE+"<?",new String[] {String.valueOf(newDate2)});
                notesAdapter.refresh(loadNotesFromDatabase());
                break;
            case R.id.clearlow:
                database.delete(TodoContract.TodoNote.TABLE_NAME,TodoContract.TodoNote.COLUMN_PRIORITY+"=?",new String[] {String.valueOf(1)});
                notesAdapter.refresh(loadNotesFromDatabase());
                break;
            case R.id.clearmedium:
                database.delete(TodoContract.TodoNote.TABLE_NAME,TodoContract.TodoNote.COLUMN_PRIORITY+"=?",new String[] {String.valueOf(2)});
                notesAdapter.refresh(loadNotesFromDatabase());
                break;
            case R.id.clearhigh:
                database.delete(TodoContract.TodoNote.TABLE_NAME,TodoContract.TodoNote.COLUMN_PRIORITY+"=?",new String[] {String.valueOf(3)});
                notesAdapter.refresh(loadNotesFromDatabase());
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private List<note.Note> loadNotesFromDatabase() {
        if (database == null) {
            return Collections.emptyList();
        }
        List<note.Note> result = new LinkedList<>();
        Cursor cursor = null;
        try {




            cursor = database.query(TodoContract.TodoNote.TABLE_NAME, null,
                    null, null,
                    null, null,
                    TodoContract.TodoNote.COLUMN_PRIORITY + " DESC");




            while (cursor.moveToNext()) {




                long id = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoNote._ID));
                String content = cursor.getString(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_CONTENT));
                long dateMs = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_DATE));
                int intState = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_STATE));
                int intPriority = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoNote.COLUMN_PRIORITY));

                note.Note note = new note.Note(id);
                note.setContent(content);
                note.setDate(new Date(dateMs));
                note.setState(State.from(intState));
                note.setPriority(intPriority);

                result.add(note);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }



}

