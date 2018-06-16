package com.example.sameershekhar.intugine.Networking;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.sameershekhar.intugine.Activities.CurrentLocationOfUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VolleyGetRequestFromServer {
    private static int interval;


    public static void fetchIntervalFromServer(Context context, String url, final CurrentLocationOfUser.VolleyCallback callback){

        //fetching data from server of all parent categories using volley

        //RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest myReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("Reponse",response);

                        interval=parseInterval(response);
                        Log.v("Interval",interval+"");
                        callback.onSuccess(interval);
                        }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }



        };

        VolleySingletonClass.getInstance(context).addToRequestQue(myReq);

    }

    public static int parseInterval(String response)
    {
        JSONObject data= null;
        try {
            data = new JSONObject(response);
            return data.getInt("interval");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
