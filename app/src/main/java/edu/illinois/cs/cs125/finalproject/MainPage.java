package edu.illinois.cs.cs125.finalproject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

import static java.lang.Double.parseDouble;


public class MainPage extends AppCompatActivity {
    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "ljm2wsheung2_FinalProject";

    private static final int[] lastPrice = {40,45};

    private static final int[] openPrice = {22,27};

    /** Request queue for our network requests. */
    private static RequestQueue requestQueue;

    /** today's price */
    private double todayPrice = 50.0;

    /** projection for tomorrow */
    private double tomorrowProjection = 60.0;

    /** projection for 3 days */
    private double threeDayProjection = 40.0;

    /** projection for next week */
    private double nextWeekProjection = 30.0;

    private String startDate = "";

    private String endDate = "";



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

        final Button tmrIcon = findViewById(R.id.tmrIcon);

        final Button threeDaysIcon = findViewById(R.id.threeDaysIcon);

        final Button nextWeekIcon = findViewById(R.id.nextWeekIcon);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String currentDateAndTime = sdf.format(new Date());

        final TextView refreshedDate = findViewById((R.id.date));

        refreshedDate.setText(currentDateAndTime);

        // Attach the handler to our UI button
        final ImageButton startAPICall = findViewById(R.id.refreshIconButton2);
        startAPICall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Start API button clicked");
                startDate = getDateString(14);
                endDate = getDateString(1);
                System.out.println(startDate + "    " + endDate);
                Log.d("Time Start", "start of fetching data");
                try{
                    Thread.sleep(100);
                    nextWeekProjection = startAPICall(5);
                }
                catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
                try{
                    Thread.sleep(100);
                    threeDayProjection = startAPICall(3);
                }
                catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
                try{
                    Thread.sleep(100);
                    tomorrowProjection = startAPICall(1);
                }
                catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
                todayPrice = startAPICall(2);
                Log.d("Time End", "ALL DATA FETCHED");
            }
        });
        System.out.println(tomorrowProjection);
        System.out.println(todayPrice);
        if (tomorrowProjection == 0) {
            tmrIcon.setBackgroundColor(Color.TRANSPARENT);
        } else if (tomorrowProjection < todayPrice) {
            tmrIcon.setBackgroundColor(Color.GREEN);
        } else {
            tmrIcon.setBackgroundColor(Color.RED);
        }

        if (threeDayProjection == 0) {
            threeDaysIcon.setBackgroundColor(Color.TRANSPARENT);
        } else if (threeDayProjection < todayPrice) {
            threeDaysIcon.setBackgroundColor(Color.GREEN);
        } else {
            threeDaysIcon.setBackgroundColor(Color.RED);
        }

        if (nextWeekProjection == 0) {
            nextWeekIcon.setBackgroundColor(Color.TRANSPARENT);
        } else if (nextWeekProjection < todayPrice) {
            nextWeekIcon.setBackgroundColor(Color.GREEN);
        } else {
            nextWeekIcon.setBackgroundColor(Color.RED);
        }
    }

    private double result = 0.0;

    /**
     * Make an API call.
     */
    Double startAPICall(final int day) {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://www.quandl.com/api/v3/datasets/CHRIS/CME_CL"+day+".json?" +
                            "start_date="+startDate+"&"+"end_date="+endDate+"&"+
                            "api_key=K6H-k-nqK72xdr8ZgXwS",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            result = parseData(response);
                            Log.d(TAG, response.toString());
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
        return result;
    }

    Double parseData(final JSONObject response) {
        String fullData = response.toString();
        //System.out.println(fullData);
        int locationOfData = fullData.indexOf("\"data\":[[");
        System.out.println(fullData.substring(locationOfData+lastPrice[0],locationOfData+lastPrice[1]));
        return parseDouble(fullData.substring(locationOfData+lastPrice[0],locationOfData+lastPrice[1]));
    }


    private Date getXDayPrior(final int daysPrior) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -daysPrior);
        return cal.getTime();
    }

    private String getDateString(final int daysPrior) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(getXDayPrior(daysPrior));
    }
}