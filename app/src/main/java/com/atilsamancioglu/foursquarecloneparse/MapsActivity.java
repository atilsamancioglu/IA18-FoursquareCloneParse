package com.atilsamancioglu.foursquarecloneparse;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;

    String latitudeString;
    String longitudeString;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_place,menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save_place) {
            upload();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //get location

                SharedPreferences sharedPreferences = MapsActivity.this.getSharedPreferences("com.atilsamancioglu.foursquarecloneparse",MODE_PRIVATE);
                boolean firstTimeCheck = sharedPreferences.getBoolean("notFirstTime",false);

                if (!firstTimeCheck) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
                    sharedPreferences.edit().putBoolean("notFirstTime",true).apply();
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }

        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
        } else {

             //get location

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            mMap.clear();

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation != null) {
                LatLng lastUserLocation = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 ) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //get location

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                    mMap.clear();

                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (lastKnownLocation != null) {
                        LatLng lastUserLocation = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));

                    }

                }
            }
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Double latitude = latLng.latitude;
        Double longitude = latLng.longitude;

        latitudeString = latitude.toString();
        longitudeString = longitude.toString();

        mMap.addMarker(new MarkerOptions().title("New Place").position(latLng));

        Toast.makeText(this, "Click On Save", Toast.LENGTH_SHORT).show();

    }


    public void upload() {
        //parse upload

        PlacesClass placesClass = PlacesClass.getInstance();

        String placeName = placesClass.getName();
        String placeType = placesClass.getType();
        String placeAtmosphere = placesClass.getAtmosphere();
        Bitmap placeImage = placesClass.getImage();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        placeImage.compress(Bitmap.CompressFormat.PNG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        ParseFile parseFile = new ParseFile("image.png",bytes);

        ParseObject object = new ParseObject("Places");
        object.put("image",parseFile);
        object.put("name", placeName);
        object.put("type",placeType);
        object.put("username", ParseUser.getCurrentUser().getUsername());
        object.put("atmosphere", placeAtmosphere);
        object.put("latitude",latitudeString);
        object.put("longitude",longitudeString);

        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if ( e != null ) {
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), LocationsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
