package com.atilsamancioglu.foursquarecloneparse;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

public class CreatePlaceActivity extends AppCompatActivity {

    EditText nameText, typeText, atmosphereText;
    ImageView imageView;
    Bitmap chosenImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_place);

        nameText = findViewById(R.id.name_text_create_place_activity);
        typeText = findViewById(R.id.type_text_create_place_activity);
        atmosphereText = findViewById(R.id.atmosphere_text_create_place_activity);
        imageView = findViewById(R.id.imageview_create_place_activity);

    }

    public void selectPicture(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},2);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,1);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 2 ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode ==1 && resultCode == RESULT_OK && data != null) {

            Uri uri = data.getData();

            try {
                chosenImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                imageView.setImageBitmap(chosenImage);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public void next(View view) {

        PlacesClass placesClass = PlacesClass.getInstance();

        String placeName = nameText.getText().toString();
        String placeType = typeText.getText().toString();
        String placeAtmosphere = atmosphereText.getText().toString();

        placesClass.setName(placeName);
        placesClass.setType(placeType);
        placesClass.setAtmosphere(placeAtmosphere);
        placesClass.setImage(chosenImage);


        //intent

        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);

    }
}
