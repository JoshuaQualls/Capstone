package com.example.tedge.barbuds;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DataParse {

    private HashMap<String, String> getPlace(JSONObject googleJSON)
    {
        HashMap<String, String> googlePlacesMap = new HashMap<>();
        String placeName = "";
        String vicinity = "";
        String latitude = "";
        String longitude = "";
        String reference = "";

        Log.d("DataParser","jsonobject ="+googleJSON.toString());
    try {
        if (!googleJSON.isNull("name")) {

            placeName = googleJSON.getString("name");

        }
        if (!googleJSON.isNull("vicinity")) {
            vicinity = googleJSON.getString("vicinity");
        }
        latitude = googleJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
        longitude = googleJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");



        googlePlacesMap.put("place_name", placeName);
        googlePlacesMap.put("vicinity", vicinity);
        googlePlacesMap.put("lat", latitude);
        googlePlacesMap.put("lng", longitude);
        Log.d("HashMap","Hashmap ="+googlePlacesMap.toString());
    }
    catch(JSONException e){
        e.printStackTrace();
    }
        return googlePlacesMap;
    }

    public List<HashMap<String, String>> getPlaces(JSONArray jsonArray){
        int count = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String, String> placeMap = null;
        for(int i = 0; i < count; i++)
        {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    public List<HashMap<String, String>> parse(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        Log.d("json data", jsonData);
        try {
            jsonObject = new JSONObject(jsonData);

            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("jsonArray", jsonArray.toString());
        return getPlaces(jsonArray);
    }
}
