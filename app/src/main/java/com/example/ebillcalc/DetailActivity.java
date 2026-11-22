package com.example.ebillcalc;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    TextView txtDetailMonth, txtDetailUnits;
    TextView txtDetailTotal, txtDetailRebate, txtDetailFinal;

    Button btnEdit, btnDelete, btnDetailBack;

    DatabaseHelper dbHelper;
    int billId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        txtDetailMonth = findViewById(R.id.txtDetailMonth);
        txtDetailUnits = findViewById(R.id.txtDetailUnits);
        txtDetailTotal = findViewById(R.id.txtDetailTotal);
        txtDetailRebate = findViewById(R.id.txtDetailRebate);
        txtDetailFinal = findViewById(R.id.txtDetailFinal);

        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnDetailBack = findViewById(R.id.btnDetailBack);

        dbHelper = new DatabaseHelper(this);

        billId = getIntent().getIntExtra("BILL_ID", -1);

        loadBillDetails();

        btnDetailBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DetailActivity.this,
                    EditActivity.class
            );

            intent.putExtra("BILL_ID", billId);

            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {

            boolean deleted = dbHelper.deleteBill(billId);

            if (deleted) {
                Toast.makeText(this,
                        "Bill deleted successfully!",
                        Toast.LENGTH_SHORT).show();

                finish();

            } else {
                Toast.makeText(this,
                        "Failed to delete bill!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBillDetails() {

        Cursor cursor = dbHelper.getBillById(billId);

        if (cursor != null) {

            if (cursor.moveToFirst()) {

                String month = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_MONTH));

                int units = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_UNITS));

                double total = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_TOTAL));

                int rebate = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_REBATE));

                double finalCost = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_FINAL));

                txtDetailMonth.setText(month);
                txtDetailUnits.setText(units + " kWh");
                txtDetailTotal.setText(String.format(
                        Locale.getDefault(), "RM %.2f", total));

                txtDetailRebate.setText(rebate + "%");

                txtDetailFinal.setText(String.format(
                        Locale.getDefault(), "RM %.2f", finalCost));

            } else {
                Toast.makeText(this,
                        "Bill record not found!",
                        Toast.LENGTH_SHORT).show();
                finish();
            }

            cursor.close();
        }
    }
}
