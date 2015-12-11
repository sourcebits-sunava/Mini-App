package com.sourcebits.footin;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sunavar on 10/12/15.
 */
public class GetAddressFromAutoView extends AsyncTask<String, Void, String>
{

    private LocationActivity activity;
    Double lng;
    Double lat;


    public GetAddressFromAutoView(LocationActivity activity) {
        super();
        this.activity = activity;
    }

    public static JSONObject getLocationInfo(String placeID) {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);


            String uri = "https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4&key=AIzaSyBogzNC5KMeBPU7aMWXM885GlXwlWOqNMs";

            //StringBuilder uri = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json");
            //uri.append("?placeid="+placeID);
            //uri.append("&key=" + API_KEY);


            HttpURLConnection conn = null;
            String responseJson = null;
            try {
                URL url = new URL(uri.toString());
                conn = (HttpURLConnection) url.openConnection();
                InputStream inputStream = conn.getInputStream();
                responseJson = getHttpResponseStream(inputStream);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(responseJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }
        return null;
    }


    public static String getHttpResponseStream(InputStream responseStream) {
        if (responseStream != null) {
            InputStream responseInputStream = responseStream;
            StringBuilder stringBuilder = new StringBuilder();

            try {

                BufferedReader reader = new BufferedReader(new InputStreamReader(responseInputStream));

                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    stringBuilder.append(line + "\n");
                }

                responseInputStream.close();

            } catch (IOException e) {
            }

            return stringBuilder.toString();
        } else {
            return null;
        }
    }



    @Override
    protected String doInBackground(String... params)
    {

        JSONObject jsonObject = getLocationInfo(params[0]);
        Log.i("JSON string =>", jsonObject.toString());

        try {


            JSONObject results = jsonObject.getJSONObject("result");
            JSONObject geo = results.getJSONObject("geometry");
            JSONObject loc = geo.getJSONObject("location");



            lng = loc.getDouble("lat");

            lat = loc.getDouble("lng");

            }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;

        }
        return "1";

    }

    /**
     * When the task finishes, onPostExecute() call back data to Activity UI and
     * displays the address.
     *
     * @param address
     */
    @Override
    protected void onPostExecute(String address)
    {

        // Call back Data and Display the current address in the UI
        activity.callBackData(lat,lng);
    }

}
