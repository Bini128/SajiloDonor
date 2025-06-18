package com.example.donorblood.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.donorblood.R;

public class Edit_profile extends Fragment {

    private static final int PICK_IMAGE = 1;
    private ImageView imgProfile;
    private EditText etEmail, etPassword;
    private Spinner spinnerBloodType;
    private Button btnSave;
    private Uri selectedImageUri;

    public Edit_profile() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        imgProfile = view.findViewById(R.id.imgProfile);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        spinnerBloodType = view.findViewById(R.id.spinnerBloodType);
        btnSave = view.findViewById(R.id.btnSave);

        // Blood types
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.blood_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodType.setAdapter(adapter);

        imgProfile.setOnClickListener(v -> openGallery());

        btnSave.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String bloodType = spinnerBloodType.getSelectedItem().toString();

            // Save the data to SQLite or server
            Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgProfile.setImageURI(selectedImageUri);
        }
    }
}
