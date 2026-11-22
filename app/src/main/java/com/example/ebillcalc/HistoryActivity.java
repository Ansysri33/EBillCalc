package com.example.ebillcalc;

import android.database.Cursor;
import android.os.Bundle;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    ListView listHistory;
    TextView txtEmpty;
    Button btnBack;

    DatabaseHelper dbHelper;

    ArrayList<String> billList;
    ArrayList<Integer> billIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listHistory = findViewById(R.id.listHistory);
        txtEmpty = findViewById(R.id.txtEmpty);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new DatabaseHelper(this);
        billList = new ArrayList<>();
        billIds = new ArrayList<>();

        btnBack.setOnClickListener(v -> finish());
        loadHistory();
    }

    private void loadHistory() {

        billList.clear();
        billIds.clear();

        Cursor cursor = dbHelper.getAllBills();

        if (cursor != null) {

            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_ID));

                String month = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_MONTH));

                double finalCost = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_FINAL));

                billIds.add(id);

                String display = month + "\n" +
                        String.format(
                                Locale.getDefault(),
                                "RM %.2f",
                                finalCost
                        );

                billList.add(display);
            }

            cursor.close();
        }

        if (billList.isEmpty()) {
            txtEmpty.setVisibility(TextView.VISIBLE);
            listHistory.setVisibility(ListView.GONE);
        } else {
            txtEmpty.setVisibility(TextView.GONE);
            listHistory.setVisibility(ListView.VISIBLE);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                billList
        );

        listHistory.setAdapter(adapter);
        listHistory.setOnItemClickListener((parent, view, position, id) -> {

            int selectedBillId = billIds.get(position);

            Intent intent = new Intent(
                    HistoryActivity.this,
                    DetailActivity.class
            );

            intent.putExtra("BILL_ID", selectedBillId);

            startActivity(intent);

        });
    }
}
