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
import com.example.donorblood.database.DBHelper;

public class Edit_profile extends Fragment {

    private static final int PICK_IMAGE = 1;
    private ImageView imgProfile;
    private EditText etEmail;
    private Button btnSave;
    private Uri selectedImageUri;

    public Edit_profile() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        imgProfile = view.findViewById(R.id.imgProfile);
        etEmail = view.findViewById(R.id.etEmail);
        btnSave = view.findViewById(R.id.btnSave);

        // Blood types
        imgProfile.setOnClickListener(v -> openGallery());

        btnSave.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String imageUri = (selectedImageUri != null) ? selectedImageUri.toString() : null;

            if (email.isEmpty() || imageUri == null) {
                Toast.makeText(getContext(), "Please select an image and enter email.", Toast.LENGTH_SHORT).show();
                return;
            }

            DBHelper dbHelper = new DBHelper(getContext());
            boolean success = dbHelper.saveOrUpdateProfile(email, imageUri);

            if (success) {
                Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show();
            }
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
