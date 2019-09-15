package com.example.codex;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class GST extends AppCompatActivity {

    EditText gstNumber;
    Button verify;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String LOG = "GST";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gst);

        gstNumber = findViewById(R.id.gst);
        verify = findViewById(R.id.verify_gst);

        sharedPreferences = getSharedPreferences("gstInfo", MODE_PRIVATE);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = gstNumber.getText().toString();
                String url = "https://gstinflask.herokuapp.com/" + n;

                Log.v(LOG, url);

                getGstInfo(url);
               }
        });
    }

    public void getGstInfo(String url){
        RequestQueue queue = Volley.newRequestQueue(GST.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(LOG, "Success");
                        Toast.makeText(GST.this, "Verified!!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.v(LOG, error.toString());
                        Toast.makeText(GST.this, "Failed!", Toast.LENGTH_SHORT).show();

                    }
                });

        queue.add(jsonObjectRequest);

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(7000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
