package com.example.dnb.version_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dnb.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

public class Version_1_MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Bitmap> imageList = new ArrayList<>();

    TextView textView;

    Timer timer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version1_main);
        textView = findViewById(R.id.marquee);
        textView.setSelected(true);


        recyclerView = findViewById(R.id.imageRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
        imageAdapter =new ImageAdapter(imageList);
        recyclerView.setAdapter(imageAdapter);


        final int delay = 3000; // Delay between scrolls (in milliseconds)
        final int period = 5000; // Time between the start of each scroll (in milliseconds)
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int position = 0;

            @Override
            public void run() {
                position++;
                if (position == imageAdapter.getItemCount()) {
                    position = 0;
                }
                recyclerView.smoothScrollToPosition(position);
            }
        }, delay, period);



        fetchImages();

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
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private void fetchImages() {
        String url = "https://creativecollege.in/DNB/retrive.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String[] imageStrings = response.split("\\r?\\n");
//                        for (String imageString : imageStrings) {
//                            byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
//                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//                            imageList.add(bitmap);
//                        }

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