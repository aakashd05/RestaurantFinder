package beitproject.com.restrofinder.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import beitproject.com.restrofinder.adapters.RestaurantListAdapter;
import beitproject.com.restrofinder.models.RestaurantModel;

/**
 * Created by AkaashD on 12/23/2016.
 */
public class GetPlaces extends AsyncTask<String, Void, ArrayList<RestaurantModel>> {
    double lat, lang;
    RestaurantListAdapter restaurantListAdapter;

    public GetPlaces(RestaurantListAdapter restaurantListAdapter, double lat, double lang) {
        this.restaurantListAdapter = restaurantListAdapter;
        this.lat = lat;
        this.lang = lang;
    }

    @Override
    protected ArrayList<RestaurantModel> doInBackground(String... args) {
        ArrayList<RestaurantModel> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            sb.append("&location=").append(lat).append(",").append(lang);
            sb.append("&radius=").append(3000);
            sb.append("&types=").append("restaurant");
            sb.append("&key=").append(Constants.GOOGLE_API_KEY);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return resultList;
        } catch (IOException e) {
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");
            resultList = new ArrayList(predsJsonArray.length() + 1);
            for (int i = 0; i < predsJsonArray.length(); i++) {
                String icon = predsJsonArray.getJSONObject(i).getString("icon");
                String name = predsJsonArray.getJSONObject(i).getString("name");
                String rating = "0";
                String addrs = "0";
                if (predsJsonArray.getJSONObject(i).has("rating"))
                    rating = predsJsonArray.getJSONObject(i).getString("rating");
                if (predsJsonArray.getJSONObject(i).has("vicinity"))
                    addrs = predsJsonArray.getJSONObject(i).getString("vicinity");
                RestaurantModel restaurantModel = new RestaurantModel();
                restaurantModel.name = name;
                restaurantModel.iconUrl = icon;
                restaurantModel.rating = rating;
                restaurantModel.address = addrs;
                resultList.add(restaurantModel);
            }
        } catch (JSONException e) {
            Log.e(Constants.LOG_TAG, "Cannot process JSON results", e);
        }
        return resultList;
    }

    @Override
    protected void onPostExecute(ArrayList<RestaurantModel> result) {
        if (result == null || result.size() == 0) {
        } else
            restaurantListAdapter.setData(result);
    }
}