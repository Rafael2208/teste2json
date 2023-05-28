package com.example.teste2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText inputNumber;
    private Button addButton;
    private TextView sortedNumbers;
    private Button saveButton;
    private List<Integer> numbersList;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputNumber = findViewById(R.id.input_number);
        addButton = findViewById(R.id.add_button);
        sortedNumbers = findViewById(R.id.sorted_numbers);
        saveButton = findViewById(R.id.save_button);

        numbersList = new ArrayList<>();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumberToList();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNumbersToJson();
            }
        });
    }

    private void addNumberToList() {
        String input = inputNumber.getText().toString().trim();

        if (!input.isEmpty()) {
            int number = Integer.parseInt(input);
            numbersList.add(number);
            Collections.sort(numbersList);

            displaySortedNumbers();
            inputNumber.setText("");
        }
    }

    private void displaySortedNumbers() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int number : numbersList) {
            stringBuilder.append(number).append(", ");
        }

        sortedNumbers.setText(stringBuilder.toString());
    }

    private void saveNumbersToJson() {
        if (isWritePermissionGranted()) {
            String json = new Gson().toJson(numbersList);

            try {
                File file = new File(Environment.getExternalStorageDirectory(), "sorted_numbers.json");
                FileWriter writer = new FileWriter(file);
                writer.write(json);
                writer.flush();
                writer.close();

                Toast.makeText(this, "Números salvos com sucesso!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao salvar números.", Toast.LENGTH_SHORT).show();
            }
        } else {
            requestWritePermission();
        }
    }

    private boolean isWritePermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestWritePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveNumbersToJson();
            } else {
                Toast.makeText(this, "A permissão de gravação foi negada.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}