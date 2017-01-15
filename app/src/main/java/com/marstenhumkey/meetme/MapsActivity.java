package com.marstenhumkey.meetme;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    public static final String STORAGE_CONNECTION_STRING = "DefaultEndpointsProtocol=https;AccountName=pranabbaidya;AccountKey=n+8bf7OSPP2v42jWPH/5jGmoEQeAv6pWI0224AjSzsVKum+HNIHuDW4SuaJ83rXcBgLGufp1yyqffGmca4N8sw==;" +
            "AccountName=pranabbaidya;" +
            "AccountKey=n+8bf7OSPP2v42jWPH/5jGmoEQeAv6pWI0224AjSzsVKum+HNIHuDW4SuaJ83rXcBgLGufp1yyqffGmca4N8sw==";

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
     *
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        LatLng currentLoc = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(currentLoc).title("User location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));

        new pushToCloud().execute(location);
    }

    public class pushToCloud extends AsyncTask<Location, Integer, Integer>{
        @Override
        protected Integer doInBackground(Location... locations)
        {
            Location currentLocation = locations[0];
            CloudStorageAccount storageAccount = null;
            try
            {
                storageAccount = CloudStorageAccount.parse(STORAGE_CONNECTION_STRING);
                CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
                CloudBlobContainer container = blobClient.getContainerReference("test101");

                File location = new File(getFilesDir(), "myLocation");
                location.createNewFile();
                ObjectOutputStream oos = new ObjectOutputStream(new java.io.FileOutputStream(location));
                oos.writeObject(currentLocation);

                CloudBlob block = container.getBlockBlobReference("myLocation");
                block.upload(new java.io.FileInputStream(location), location.length());

            } catch (URISyntaxException e)
            {
                e.printStackTrace();
            } catch (InvalidKeyException e)
            {
                e.printStackTrace();
            } catch (StorageException e)
            {
                e.printStackTrace();
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }


}
