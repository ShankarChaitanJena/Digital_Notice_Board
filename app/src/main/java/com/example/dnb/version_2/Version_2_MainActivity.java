package com.example.dnb.version_2;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dnb.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

public class Version_2_MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageAdapter_ver2 imageAdapter;
    private List<Bitmap> imageList = new ArrayList<>();

    private TextView textView;
    private Timer imageTimer;
    private Timer textContentTimer;

    private static final String TAG = "Version_2_MainActivity";
    private static final String API_URL = "https://creativecollege.in/DNB/name.php";
    private RecyclerView recyclerView2;
    private TextContentAdapter adapter;
    private ArrayList<String> textContentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version2_main);
        recyclerView = findViewById(R.id.imageRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter_ver2(imageList);
        recyclerView.setAdapter(imageAdapter);

        recyclerView2 = findViewById(R.id.recyclerView);
        recyclerView2.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        textContentList = new ArrayList<>();
        adapter = new TextContentAdapter(textContentList);
        recyclerView2.setAdapter(adapter);

        textView = findViewById(R.id.marquee);
        textView.setSelected(true);

        fetchImages();
        fetchUpComing();

        TextView dateTextView = findViewById(R.id.date_textview);
        TextView timeTextView = findViewById(R.id.time_textview);

        Thread thread = new Thread(){
            @Override
            public void run(){
                try{
                    while (!isInterrupted()){
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Calendar calendar = Calendar.getInstance();
                                Date currentDateTime = calendar.getTime();

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                String Date = dateFormat.format(currentDateTime);

                                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                                String Time = timeFormat.format(currentDateTime);

                                dateTextView.setText(Date);
                                timeTextView.setText(Time);
                            }
                        });
                    }
                } catch (Exception e) {
                    dateTextView.setText(R.string.app_name);
                    timeTextView.setText(R.string.app_name);
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startAutoScrolling();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAutoScrolling();
    }

    private void startAutoScrolling() {
        final int imageDelay = 3000; // Delay between scrolls (in milliseconds)
        final int imagePeriod = 5000; // Time between the start of each scroll (in milliseconds)
        final int textContentDelay = 3000; // Delay between scrolls (in milliseconds)
        final int textContentPeriod = 2000; // Time between the start of each scroll (in milliseconds)

        imageTimer = new Timer();
        imageTimer.scheduleAtFixedRate(new TimerTask() {
            int position = 0;

            @Override
            public void run() {
                position++;
                if (position == imageAdapter.getItemCount()) {
                    position = 0;
                }
                recyclerView.smoothScrollToPosition(position);
                recyclerView2.smoothScrollToPosition(position);
            }
        }, imageDelay, imagePeriod);

        textContentTimer = new Timer();
        textContentTimer.scheduleAtFixedRate(new TimerTask() {
            int position = 0;

            @Override
            public void run() {
                position++;
                if (position == adapter.getItemCount()) {
                    position = 0;
                }
                recyclerView2.smoothScrollToPosition(position);
            }
        }, textContentDelay, textContentPeriod);
    }


    private void stopAutoScrolling() {
        // Stop the auto-scrolling timers
        if (imageTimer != null) {
            imageTimer.cancel();
        }
        if (textContentTimer != null) {
            textContentTimer.cancel();
        }
    }

    private void fetchUpComing() {


        // Make a Volley request
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Process the JSON response here
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                String textContent = response.getString(i);
                                textContentList.add(textContent);
                            }
                            // Notify the adapter that data has changed
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Retrieved text content: " + textContentList.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle Volley error here
                        Toast.makeText(getApplicationContext(), "Error fetching Text", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }


    private void fetchImages() {
        String url = "https://creativecollege.in/DNB/retrive.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String[] imageStrings = response.split("\\r?\\n");
                        ListIterator<String> iterator = Arrays.asList(imageStrings).listIterator(imageStrings.length);

                        while (iterator.hasPrevious()) {
                            String imageString = iterator.previous();
                            byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            imageList.add(bitmap);
                        }

                        imageAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error fetching images", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
