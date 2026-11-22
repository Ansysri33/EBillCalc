package com.example.ebillcalc;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditActivity extends AppCompatActivity {

    Spinner editSpinnerMonth;
    EditText editUnitsValue;
    SeekBar seekEditRebate;
    TextView txtEditRebate;
    Button btnUpdateBill, btnCancelEdit;

    DatabaseHelper dbHelper;

    int billId;

    String[] months = {
            "January", "February", "March",
            "April", "May", "June",
            "July", "August", "September",
            "October", "November", "December"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editSpinnerMonth = findViewById(R.id.editSpinnerMonth);
        editUnitsValue = findViewById(R.id.editUnitsValue);
        seekEditRebate = findViewById(R.id.seekEditRebate);
        txtEditRebate = findViewById(R.id.txtEditRebate);
        btnUpdateBill = findViewById(R.id.btnUpdateBill);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);

        dbHelper = new DatabaseHelper(this);

        billId = getIntent().getIntExtra("BILL_ID", -1);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                months
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        editSpinnerMonth.setAdapter(adapter);
        seekEditRebate.setMax(5);
        seekEditRebate.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        txtEditRebate.setText(progress + "%");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );
        loadExistingBill();

        btnUpdateBill.setOnClickListener(v -> updateBill());
        btnCancelEdit.setOnClickListener(v -> finish());
    }

    private void loadExistingBill() {

        Cursor cursor = dbHelper.getBillById(billId);

        if (cursor != null) {

            if (cursor.moveToFirst()) {

                String month = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_MONTH));

                int units = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_UNITS));

                int rebate = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_REBATE));

                int monthPosition = 0;

                for (int i = 0; i < months.length; i++) {
                    if (months[i].equals(month)) {
                        monthPosition = i;
                        break;
                    }
                }

                editSpinnerMonth.setSelection(monthPosition);
                editUnitsValue.setText(String.valueOf(units));
                seekEditRebate.setProgress(rebate);
                txtEditRebate.setText(rebate + "%");

            } else {

                Toast.makeText(this,
                        "Bill record not found!",
                        Toast.LENGTH_SHORT).show();

                finish();
            }

            cursor.close();
        }
    }

    private void updateBill() {

        String unitsText = editUnitsValue.getText()
                .toString().trim();

        if (unitsText.isEmpty()) {
            editUnitsValue.setError(
                    "Please enter electricity units");
            editUnitsValue.requestFocus();
            return;
        }

        int units;

        try {
            units = Integer.parseInt(unitsText);
        } catch (NumberFormatException e) {
            editUnitsValue.setError(
                    "Please enter a valid number");
            return;
        }
        if (units < 1 || units > 1000) {
            editUnitsValue.setError(
                    "Enter units between 1 and 1000");
            return;
        }

        String month = editSpinnerMonth.getSelectedItem().toString();

        int rebate = seekEditRebate.getProgress();
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
        boolean updated = dbHelper.updateBill(
                billId,
                month,
                units,
                totalCharges,
                rebate,
                finalCost
        );

        if (updated) {

            Toast.makeText(this,
                    "Bill updated successfully!",
                    Toast.LENGTH_SHORT).show();

            finish();

        } else {

            Toast.makeText(this,
                    "Failed to update bill!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
