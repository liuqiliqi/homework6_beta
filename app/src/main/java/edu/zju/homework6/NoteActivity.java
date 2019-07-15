package edu.zju.homework6;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;
    private RadioGroup radioGroup;
    private RadioButton lowRadio;

    private TodoDbHelper dbHelper;
    private SQLiteDatabase database;
    private int Priority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_act);
        setTitle("take a note!");

        dbHelper = new TodoDbHelper(this);
        database = dbHelper.getWritableDatabase();




        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }
        radioGroup = findViewById(R.id.radio_group);
        lowRadio = findViewById(R.id.radioButton3);
        lowRadio.setChecked(true);

        addBtn = findViewById(R.id.btn_con);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim(),
                        getSelectedPriority());
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    private boolean saveNote2Database(String content, int intPriority) {
        if (database == null || TextUtils.isEmpty(content)) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(TodoContract.TodoNote.COLUMN_CONTENT, content);
        values.put(TodoContract.TodoNote.COLUMN_STATE, State.TODO.intValue);
        values.put(TodoContract.TodoNote.COLUMN_DATE, System.currentTimeMillis());
        values.put(TodoContract.TodoNote.COLUMN_PRIORITY, intPriority);
        long rowId = database.insert(TodoContract.TodoNote.TABLE_NAME, null, values);
        return rowId != -1;
    }

    private int getSelectedPriority() {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radioButton:
                return 3;
            case R.id.radioButton2:
                return 2;
            case R.id.radioButton3:
                return 1;
        }
        return 1;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
        database = null;
        dbHelper.close();
        dbHelper = null;
    }
}
