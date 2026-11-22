package com.example.ebillcalc;

import android.os.Bundle;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerMonth;
    EditText editUnits;
    SeekBar seekRebate;
    TextView txtRebate, txtTotal, txtFinal;
    Button btnCalculate;
    Button btnHistory;
    Button btnAbout;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        editUnits = findViewById(R.id.editUnits);
        seekRebate = findViewById(R.id.seekRebate);
        txtRebate = findViewById(R.id.txtRebate);
        txtTotal = findViewById(R.id.txtTotal);
        txtFinal = findViewById(R.id.txtFinal);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnAbout = findViewById(R.id.btnAbout);
        btnHistory = findViewById(R.id.btnHistory);

        String[] months = {
                "January", "February", "March",
                "April", "May", "June",
                "July", "August", "September",
                "October", "November", "December"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                months
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerMonth.setAdapter(adapter);

        seekRebate.setMax(5);
        seekRebate.setProgress(0);

        seekRebate.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar, int progress, boolean fromUser) {

                        txtRebate.setText(progress + "%");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );

        btnCalculate.setOnClickListener(v -> calculateBill());

        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AboutActivity.class
            );

            startActivity(intent);
        });
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,
                    HistoryActivity.class);
            startActivity(intent);
        });
    }

    private void calculateBill() {

        String unitsText = editUnits.getText().toString().trim();

        if (unitsText.isEmpty()) {
            editUnits.setError("Please enter electricity units");
            editUnits.requestFocus();
            return;
        }

        int units;

        try {
            units = Integer.parseInt(unitsText);
        } catch (NumberFormatException e) {
            editUnits.setError("Please enter a valid number");
            return;
        }

        if (units < 1 || units > 1000) {
            editUnits.setError("Enter units between 1 and 1000");
            return;
        }

        int rebate = seekRebate.getProgress();

        double totalCharges = 0;

        if (units <= 200) {

            totalCharges = units * 0.218;

        } else if (units <= 300) {

            totalCharges = (200 * 0.218)
                    + ((units - 200) * 0.334);

        } else if (units <= 600) {

            totalCharges = (200 * 0.218)
                    + (100 * 0.334)
                    + ((units - 300) * 0.516);

        } else {

            totalCharges = (200 * 0.218)
                    + (100 * 0.334)
                    + (300 * 0.516)
                    + ((units - 600) * 0.546);
        }

        double finalCost = totalCharges
                - (totalCharges * rebate / 100.0);

        txtTotal.setText(String.format(
                Locale.getDefault(),
                "RM %.2f",
                totalCharges
        ));

        txtFinal.setText(String.format(
                Locale.getDefault(),
                "RM %.2f",
                finalCost
        ));
        String selectedMonth = spinnerMonth.getSelectedItem().toString();

        boolean saved = dbHelper.insertBill(
                selectedMonth,
                units,
                totalCharges,
                rebate,
                finalCost
        );

        if (saved) {
            Toast.makeText(
                    this,
                    "Bill saved successfully!",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    this,
                    "Failed to save bill!",
                    Toast.LENGTH_SHORT
            ).show();
        }

    }
}
