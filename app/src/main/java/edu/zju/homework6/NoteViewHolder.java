package edu.zju.homework6;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH);

    private final NoteOperator operator;

    private CheckBox checkBox;
    private TextView contentText;
    private TextView dateText;
    private View deleteBtn;
    private static final int PAGE_CODE2 = 9998;
    private ImageButton modify;
    public NoteViewHolder(@NonNull View itemView, NoteOperator operator) {
        super(itemView);
        this.operator = operator;
        checkBox = itemView.findViewById(R.id.checkBox);
        contentText = itemView.findViewById(R.id.text_main);
        dateText = itemView.findViewById(R.id.textView);
        deleteBtn = itemView.findViewById(R.id.btn_del);
        modify=itemView.findViewById(R.id.imageView);
    }

    public void bind(final note.Note note) {
        contentText.setText(note.getContent());
        dateText.setText(SIMPLE_DATE_FORMAT.format(note.getDate()));

       checkBox.setOnCheckedChangeListener(null);
       checkBox.setChecked(note.getState() == State.DONE);
       checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              note.setState(isChecked ? State.DONE : State.TODO);
                operator.updateNote(note);
            }
        });


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operator.deleteNote(note);
            }
        });

        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operator.modifyNote(note);
            }
        });





        if (note.getState() == State.DONE) {
            contentText.setTextColor(Color.GRAY);
            contentText.setPaintFlags(contentText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            contentText.setTextColor(Color.BLACK);
            contentText.setPaintFlags(contentText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        int color = 0 ;
        switch (note.getPriority()){
            case 1:
                color=Color.GREEN;
                break;
            case 2:
                color=Color.YELLOW;
                break;
            case 3:
                color=Color.RED;
        }

        itemView.setBackgroundColor(color);
    }
}
