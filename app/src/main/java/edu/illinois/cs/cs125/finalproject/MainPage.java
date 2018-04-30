package edu.illinois.cs.cs125.finalproject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import static java.lang.Double.parseDouble;


public class MainPage extends AppCompatActivity {
    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "Lab11:Main";

    private static final int[] lastPrice = {40,45};

    private static final int[] openPrice = {22,27};

    /** Request queue for our network requests. */
    private static RequestQueue requestQueue;

    /** today's price */
    private final int todayPrice = 60;

    /** projection for tomorrow */
    private final int tomorrowProjection = 55;

    /** projection for 3 days */
    private final int threeDayProjection = 65;

    /** projection for next week */
    private final int nextWeekProjection = 60;



    /**
     * Run when our activity comes into view.
     *
     * @param savedInstanceState state that was saved by the activity last time it was paused
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up a queue for our Volley requests
        requestQueue = Volley.newRequestQueue(this);

        // Load the main layout for our activity
        setContentView(R.layout.activity_main_page);

        // Attach the handler to our UI button
        final ImageButton startAPICall = findViewById(R.id.refreshIconButton);
        startAPICall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Start API button clicked");
                startAPICall(5);
            }
        });

        final ImageButton tmrIcon = findViewById(R.id.tmrIcon);

        final ImageButton threeDaysIcon = findViewById(R.id.threeDaysIcon);

        final ImageButton  nextWeekIcon = findViewById(R.id.nextWeekIcon);

        if (tomorrowProjection < todayPrice) {
            tmrIcon.setBackgroundColor(Color.GREEN);
        } else {
            tmrIcon.setBackgroundColor(Color.RED);
        }

        if (threeDayProjection < todayPrice) {
            threeDaysIcon.setBackgroundColor(Color.GREEN);
        } else {
            threeDaysIcon.setBackgroundColor(Color.RED);
        }

        if (nextWeekProjection < todayPrice) {
            nextWeekIcon.setBackgroundColor(Color.GREEN);
        } else {
            nextWeekIcon.setBackgroundColor(Color.RED);
        }

    /**
     * Make an API call.
     */
    void startAPICall(final int day) {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://www.quandl.com/api/v3/datasets/CHRIS/CME_CL"+day+".json?" +
                            "api_key=K6H-k-nqK72xdr8ZgXwS" +
                            "",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            // Log.d(TAG, response.toString());
                            parseData(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.w(TAG, error.toString());
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Double parseData(final JSONObject response) {
        String fullData = response.toString();
        //System.out.println(fullData);
        int locationOfData = fullData.indexOf("\"data\":[[");
        //System.out.println(fullData.substring(locationOfData+lastPrice[0],locationOfData+lastPrice[1]));
        return parseDouble(fullData.substring(locationOfData+lastPrice[0],locationOfData+lastPrice[1]));
    }

}