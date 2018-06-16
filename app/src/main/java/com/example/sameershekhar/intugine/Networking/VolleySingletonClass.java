package com.example.sameershekhar.intugine.Networking;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingletonClass {

    private static VolleySingletonClass mySingleTon;
    private RequestQueue requestQueue;
    private static Context mctx;
    private VolleySingletonClass(Context context){
        this.mctx=context;
        this.requestQueue=getRequestQueue();

    }
    public RequestQueue getRequestQueue(){
        if (requestQueue==null){
            requestQueue= Volley.newRequestQueue(mctx.getApplicationContext());
        }
        return requestQueue;
    }
    public static synchronized VolleySingletonClass getInstance(Context context){
        if (mySingleTon==null){
            mySingleTon=new VolleySingletonClass(context);
        }
        return mySingleTon;
    }
    public<T> void addToRequestQue(Request<T> request){
        requestQueue.add(request);

    }
}
