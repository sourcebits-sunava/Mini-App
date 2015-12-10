package com.sourcebits.footin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class LocationActivity extends Activity implements LocationListener,OnItemClickListener,GoogleMap.OnMapLongClickListener,GoogleMap.OnInfoWindowClickListener
{
    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyBogzNC5KMeBPU7aMWXM885GlXwlWOqNMs";


    private GoogleMap map;
    protected LocationManager locationManager;
    Marker marker;
    String address;
    private Toolbar toolbar;
    ImageView markerIcon;
    AutoCompleteTextView autoCompView;
    LinearLayout editTextLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationactivity);

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setLogo(R.drawable.ic_black_foot);
        setActionBar(toolbar);


        // Linear layout
        editTextLayout =(LinearLayout)findViewById(R.id.EditTextLayout);
        editTextLayout.setVisibility(View.INVISIBLE);


        //autocompleteview implmented
        autoCompView = (AutoCompleteTextView)findViewById(R.id.reference_places);
        //autoCompView.setVisibility(View.INVISIBLE);


        //replace GOOGLE MAP fragment in this Activity
        replaceMapFragment();

        //search for Network Provider and GPS Providers
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,60000,10,this);
        else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, this);
        else
            Toast.makeText(getApplicationContext(),"Enable Location",Toast.LENGTH_SHORT).show();


        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));

        autoCompView.setOnItemClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),FavouriteActivity.class);
                startActivityForResult(intent, RESULT_OK);
            }
        });

    }

    private void replaceMapFragment()
    {

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        // Enable Zoom
        map.getUiSettings().setZoomGesturesEnabled(true);

        //set Map TYPE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //enable Current location Button
        map.setMyLocationEnabled(true);

        //set "listener" for changing my location
        //map.setOnMyLocationChangeListener(myLocationChangeListener());

    }

    /*private GoogleMap.OnMyLocationChangeListener myLocationChangeListener() {
        return new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

            }
        };
    }*/


    public void callBackDataFromAsyncTask(String address)
    {
        this.address = address;
        if(address==null)
            Toast.makeText(getApplicationContext(),"Please connect to wifi or data connectivity",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        if(marker!=null)
            marker.remove();
        marker = map.addMarker(new MarkerOptions().position(loc));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
        Toast.makeText(getApplicationContext(), "You are at [" + longitude + " ; " + latitude + " ]", Toast.LENGTH_SHORT).show();
        map.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
        //get current address by invoke an AsyncTask object
        new GetAddressTask(LocationActivity.this).execute(String.valueOf(latitude), String.valueOf(longitude));
        if (map != null)
        {
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
            {
                @Override
                public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker)
                {
                    marker.showInfoWindow();
                    return true;
                }
            });
        }
        else
            Toast.makeText(getApplicationContext(), "Unable to create Maps", Toast.LENGTH_SHORT).show();




    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }





    @Override
    public void onMapLongClick(LatLng location) {

        double longitude = location.longitude;
        double latitude = location.latitude;
        if(marker!=null)
            marker.remove();
        marker = map.addMarker(new MarkerOptions().position(location));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
        Toast.makeText(getApplicationContext(), "You are at [" + longitude + " ; " + latitude + " ]", Toast.LENGTH_SHORT).show();
        map.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
        //get current address by invoke an AsyncTask object
        new GetAddressTask(LocationActivity.this).execute(String.valueOf(latitude), String.valueOf(longitude));
        if (map != null)
        {
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
            {
                @Override
                public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker)
                {
                    marker.showInfoWindow();
                    return true;
                }
            });
        }
        else
            Toast.makeText(getApplicationContext(), "Unable to create Maps", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }


    //MarkerInfo class


    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
    {

        public MarkerInfoWindowAdapter()
        {
        }

        @Override
        public View getInfoWindow(Marker marker)
        {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker)
        {
            View v  = getLayoutInflater().inflate(R.layout.infomarker, null);

            TextView markerLabel = (TextView)v.findViewById(R.id.marker_label);
            markerIcon = (ImageView)v.findViewById(R.id.marker_icon);

            markerLabel.setText(address);
            editTextLayout.setVisibility(View.INVISIBLE);


            return v;
        }


    }







    ////


    public void onItemClick(AdapterView adapterView, View view, int position, long id) {

        String str = (String) adapterView.getItemAtPosition(position);

        //getLatLongFromGivenAddress(str);
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

    }


    public void getLatLongFromGivenAddress(String youraddress)
    {
        String uri = "http://maps.google.com/maps/api/geocode/json?address=" + youraddress + "&sensor=false";
        HttpURLConnection conn = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL(uri);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            int b;
            while ((b = in.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());

            Double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            Double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            Log.d("latitude", String.valueOf(lat));
            Log.d("longitude", String.valueOf(lng));

            Toast.makeText(this, lat +"  " +lng , Toast.LENGTH_SHORT).show();
            LatLng loc = new LatLng(lat,lng);

            if(marker!=null)
                marker.remove();
            marker = map.addMarker(new MarkerOptions().position(loc));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            Toast.makeText(getApplicationContext(), "You are at [" + lng + " ; " + lat + " ]", Toast.LENGTH_SHORT).show();
            map.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
            //get current address by invoke an AsyncTask object
            new GetAddressTask(LocationActivity.this).execute(String.valueOf(lat), String.valueOf(lng));
            if (map != null)
            {
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
                {
                    @Override
                    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker)
                    {
                        marker.showInfoWindow();
                        return true;
                    }
                });
            }
            else
                Toast.makeText(getApplicationContext(), "Unable to create Maps", Toast.LENGTH_SHORT).show();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public static ArrayList autocomplete(String input) {

        ArrayList resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:in");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());


            // Load the results into a StringBuilder
            int read;

            while ((read = in.read())!= -1) {
                jsonResults.append((char) read);
            }

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "API", e);
            return resultList;
        } finally {

            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results

            resultList = new ArrayList(predsJsonArray.length());

            for (int i = 0; i < predsJsonArray.length(); i++)
            {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));

                System.out.println("============================================================");

                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));

            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);

        }
        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {

        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {

            super(context, textViewResourceId);

        }

        @Override

        public int getCount() {

            return resultList.size();

        }

        @Override

        public String getItem(int index) {

            return String.valueOf(resultList.get(index));

        }

        @Override

        public Filter getFilter() {

            Filter filter = new Filter() {

                @Override

                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();

                    if (constraint != null) {

                        // Retrieve the autocomplete results.

                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults

                        filterResults.values = resultList;

                        filterResults.count = resultList.size();

                    }
                    return filterResults;

                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    if (results != null && results.count > 0) {

                        notifyDataSetChanged();

                    } else {

                        notifyDataSetInvalidated();

                    }
                }

            };
            return filter;
        }
    }



    ////


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search)
        {
            editTextLayout.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            
        }


        return super.onOptionsItemSelected(item);
    }
}
