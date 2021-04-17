package com.example.makingnotes.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.makingnotes.MainActivity;
import com.example.makingnotes.data.Notes;
import com.example.makingnotes.R;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import com.example.makingnotes.observe.Publisher;

public class NoteBlankFragment extends Fragment {

    static final String ARG_NOTE = "currentNote";
    private Notes note;
    public static final String CURRENT_DATA = "currentData";
    private Publisher publisher; // Паблишер, с его помощью обмениваемся данными
    private TextInputEditText titleText;
    private TextInputEditText contentText;
    private TextView dateOfCreationText;
    private int color;
    private String dateOfCreation;
    private boolean isNewNote = false;

    // Фабричный метод создания фрагмента
    // Для редактирования данных
    public static NoteBlankFragment newInstance(Notes note) {
        NoteBlankFragment fragment = new NoteBlankFragment();  // создание

        // Передача параметра
        Bundle args = new Bundle();
        args.putParcelable(ARG_NOTE, note);
        fragment.setArguments(args);
        return fragment;
    }

    // Для добавления новых данных
    public static NoteBlankFragment newInstance() {
        return new NoteBlankFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            note = getArguments().getParcelable(ARG_NOTE);
        }
        if (note == null) {
            isNewNote = true;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity) context;
        publisher = activity.getPublisher();
    }

    @Override
    public void onDetach() {
        publisher = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Таким способом можно получить головной элемент из макета
        View view = inflater.inflate(R.layout.fragment_note_blank, container, false);
        // Установить название заметки, описание заметки, дата создания заметки

        initView(view);
        // если note пустая, то это добавление
        if (note != null) {
            color = note.getColor();
            dateOfCreation = note.getCreationDate();
            populateView(view);
        }
        if (isNewNote) {
            color = getColor();
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy",
                    Locale.getDefault());
            dateOfCreation = String.format("%s: %s", "Дата создания",
                    formatter.format(Calendar.getInstance().getTime()));
            populateView(view);
        }
        setHasOptionsMenu(true);
        return view;
    }

    // Здесь соберём данные из views
    @Override
    public void onStop() {
        super.onStop();
        note = collectNote();
    }
    // Здесь передадим данные в паблишер
    @Override
    public void onDestroy() {
        super.onDestroy();
        publisher.notifySingle(note);
    }

    private Notes collectNote() {
        String title = Objects.requireNonNull(this.titleText.getText()).toString();
        String content = Objects.requireNonNull(this.contentText.getText()).toString();
        if (isNewNote) {
            isNewNote = false;
        }
        return new Notes(title, content, dateOfCreation, color);
    }

    private void initView(View view) {
        titleText = view.findViewById(R.id.note_title);
        contentText = view.findViewById(R.id.note_content);
        dateOfCreationText = view.findViewById(R.id.note_date_of_creation);
    }

    private void populateView(View view) {
        if (isNewNote) {
            dateOfCreationText.setText(dateOfCreation);
            view.setBackgroundColor(color);
        } else {
            dateOfCreationText.setText(note.getCreationDate());
            titleText.setText(note.getTitle());
            contentText.setText(note.getContent());
            view.setBackgroundColor(note.getColor());
        }
    }

    public int getColor() {
        int[] colors = getResources().getIntArray(R.array.colors);
        Random random = new Random();
        return colors[random.nextInt(colors.length)];
    }
}