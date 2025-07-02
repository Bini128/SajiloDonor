package com.example.donorblood.profile;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.donorblood.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Edit_profile extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;

    private ImageView editProfileImage;
    private EditText editName, editDOB;
    private RadioGroup genderGroup;
    private Spinner spinnerBloodGroup;
    private Button btnChangeImage, btnSave;
    private String selectedGender = "", encodedImage = "";
    private String userEmail = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        userEmail = prefs.getString("email", "");

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(getContext(), "User email not found! Please login again.", Toast.LENGTH_SHORT).show();
        }

        editProfileImage = view.findViewById(R.id.editProfileImage);
        editName = view.findViewById(R.id.editName);
        editDOB = view.findViewById(R.id.editDOB);
        genderGroup = view.findViewById(R.id.genderGroup);
        spinnerBloodGroup = view.findViewById(R.id.spinnerBloodGroup);
        btnChangeImage = view.findViewById(R.id.btnChangeImage);
        btnSave = view.findViewById(R.id.btnSaveProfile);

        // Setup spinner
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"});
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodGroup.setAdapter(bloodAdapter);

        // Date Picker for DOB
        editDOB.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                String formattedDOB = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                editDOB.setText(formattedDOB);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });


        btnChangeImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            } else {
                openImagePicker();
            }
        });

        btnSave.setOnClickListener(v -> updateProfile());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri == null) return;

            try {
                Bitmap bitmap = loadBitmapFromUri(imageUri);
                int orientation = getExifOrientation(imageUri);
                Bitmap rotatedBitmap = rotateBitmapIfNeeded(bitmap, orientation);
                editProfileImage.setImageBitmap(rotatedBitmap);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] imageBytes = baos.toByteArray();
                encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap loadBitmapFromUri(Uri uri) throws IOException {
        InputStream input = requireContext().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        if (input != null) input.close();
        return bitmap;
    }

    private int getExifOrientation(Uri uri) throws IOException {
        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
        ExifInterface exif = new ExifInterface(inputStream);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        if (inputStream != null) inputStream.close();
        return orientation;
    }

    private Bitmap rotateBitmapIfNeeded(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile() {
        String name = editName.getText().toString().trim();
        String dob = editDOB.getText().toString().trim();
        String bloodGroup = spinnerBloodGroup.getSelectedItem().toString();

        int genderId = genderGroup.getCheckedRadioButtonId();
        if (genderId != -1) {
            selectedGender = ((RadioButton) getView().findViewById(genderId)).getText().toString();
        }

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(getContext(), "User email not found! Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.18.17/Sajilodonor/edit_profile.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_SHORT).show();

                        if (json.getBoolean("success")) {
                            NavController navController = Navigation.findNavController(requireActivity(), R.id.main_fragment);
                            navController.navigate(R.id.profile); // Make sure 'profileFragment' matches your nav graph
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Parse error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Network Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("email", userEmail);
                map.put("name", name);
                map.put("gender", selectedGender);
                map.put("dob", dob);
                map.put("bloodgroup", bloodGroup);
                map.put("profile_image", encodedImage != null ? encodedImage : "");
                return map;
            }
        };

        Volley.newRequestQueue(getContext()).add(request);
    }
}
