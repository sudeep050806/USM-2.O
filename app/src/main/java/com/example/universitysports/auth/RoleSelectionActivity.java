package com.example.universitysports.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitysports.R;
import com.google.android.material.card.MaterialCardView;

/**
 * RoleSelectionActivity - Choose role during signup
 */
public class RoleSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        MaterialCardView cardStudent = findViewById(R.id.cardStudent);
        MaterialCardView cardTeacher = findViewById(R.id.cardTeacher);

        cardStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignup("student");
            }
        });

        cardTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignup("teacher");
            }
        });
    }

    private void navigateToSignup(String role) {
        Intent intent = new Intent(RoleSelectionActivity.this, SignupActivity.class);
        intent.putExtra("role", role);
        startActivity(intent);
        finish();
    }
}