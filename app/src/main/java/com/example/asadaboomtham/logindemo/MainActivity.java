package com.example.asadaboomtham.logindemo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

        private CircleImageView photoImageView;
        private TextView nameTextView;
        private TextView emailTextView;
//        private TextView idTextView;
    //    private TextView Fname;

        private ProfileTracker profileTracker;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            photoImageView = (CircleImageView) findViewById(R.id.photoImageView);
            nameTextView = (TextView) findViewById(R.id.nameTextView);
            emailTextView = (TextView) findViewById(R.id.emailTextView);
//            idTextView = (TextView) findViewById(R.id.idTextView);

            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                    if (currentProfile != null) {
                        displayProfileInfo(currentProfile);
                    }
                }
            };

            if (AccessToken.getCurrentAccessToken() == null) {
                goLoginScreen();
            } else {
                requestEmail(AccessToken.getCurrentAccessToken());

                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    displayProfileInfo(profile);
                } else {
                    Profile.fetchProfileForCurrentAccessToken();
                }
            }
        }

        private void requestEmail(AccessToken currentAccessToken) {
            GraphRequest request = GraphRequest.newMeRequest(currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    if (response.getError() != null) {
                        Toast.makeText(getApplicationContext(), response.getError().getErrorMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        String email = object.getString("email");
                        setEmail(email);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
            request.setParameters(parameters);
            request.executeAsync();
        }

        private void setEmail(String email) {
            emailTextView.setText(email);
        }

        private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

        public void logout(View view) {
            LoginManager.getInstance().logOut();
            goLoginScreen();
        }

        private void displayProfileInfo(Profile profile) {
            String id = profile.getId();
            String name = profile.getName();
            String photoUrl = profile.getProfilePictureUri(600, 480).toString();

            nameTextView.setText(name);
//            idTextView.setText(id);

            Glide.with(getApplicationContext())
                    .load(photoUrl)
                    .into(photoImageView);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();

            profileTracker.stopTracking();
        }
    }