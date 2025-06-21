package com.example.donorblood;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;


public class bloodgroup extends Activity {

    private String selectedType = "";
    private String selectedRh = "";

    private TextView textSelectedBloodType;

    private Button[] typeButtons;
    private Button[] rhButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodgroup);

        textSelectedBloodType = findViewById(R.id.textSelectedBloodType);

        // Initialize blood type buttons
        typeButtons = new Button[]{
                findViewById(R.id.btnA),
                findViewById(R.id.btnB),
                findViewById(R.id.btnO),
                findViewById(R.id.btnAB)
        };

        rhButtons = new Button[]{
                findViewById(R.id.btnPositive),
                findViewById(R.id.btnNegative)
        };

        for (Button btn : typeButtons) {
            btn.setOnClickListener(v -> {
                resetTypeButtons();
                selectedType = btn.getText().toString();
                highlightButton(btn);
                updateSelectedText();
            });
        }

        for (Button btn : rhButtons) {
            btn.setOnClickListener(v -> {
                resetRhButtons();
                selectedRh = btn.getText().toString();
                highlightButton(btn);
                updateSelectedText();
            });
        }

        findViewById(R.id.btnFinish).setOnClickListener(v -> {
            if (selectedType.isEmpty() || selectedRh.isEmpty()) {
                Toast.makeText(this, "Please select both blood type and Rh factor", Toast.LENGTH_SHORT).show();
            } else {
                String bloodGroup = selectedType + selectedRh;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedBloodGroup", bloodGroup);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void resetTypeButtons() {
        for (Button btn : typeButtons) {
            unhighlightButton(btn);
        }
    }

    private void resetRhButtons() {
        for (Button btn : rhButtons) {
            unhighlightButton(btn);
        }
    }

    private void highlightButton(Button button) {
        button.setBackground(ContextCompat.getDrawable(this, R.drawable.blood_group_selected));
    }

    private void unhighlightButton(Button button) {
        button.setBackground(ContextCompat.getDrawable(this, R.drawable.blood_group_selector));
    }

    private void updateSelectedText() {
        if (!selectedType.isEmpty() && !selectedRh.isEmpty()) {
            textSelectedBloodType.setText("Selected Blood Type: " + selectedType + selectedRh);
        }
    }
}
