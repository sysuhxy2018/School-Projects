package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchResult extends AppCompatActivity {

    private TextView title;
    private ImageView backButton;

    private static final String weatherUrl = "https://homework9-259522.appspot.com/weatherSearch2?";

    private JSONObject weatherJson = null;
    private TextView cityText, tempText, sumText, humidText, windText, visiText, pressText, resHint;
    private ImageView iconImg, moreInfo;
    private String city;
    private HashMap<String, String> iconTable;

    private TextView[] dates = new TextView[8];
    private ImageView[] dayIcons = new ImageView[8];
    private TextView[] lowTemps = new TextView[8], highTemps = new TextView[8];

    private LinearLayout progress, error;
    private SharedPreferences sp;
    private static final String defaultValue = "Null";
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle intent = this.getIntent().getExtras();
        city = intent.getString("city");

        initialize();
        title.setText(city);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(it);
                finish();
            }
        });

        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(), DetailActivity.class);
                it.putExtra("city", cityText.getText());
                it.putExtra("json", weatherJson.toString());
                startActivity(it);
            }
        });

        getWeather();

        fab = findViewById(R.id.fab);
        fab.clearAnimation();
        fab.show();
        sp = getSharedPreferences("favorites", MODE_PRIVATE);
        if (sp.getString(city, defaultValue) == defaultValue) {
            fab.setImageResource(R.drawable.plus);
        }
        else {
            fab.setImageResource(R.drawable.minus);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sp.getString(city, defaultValue) == defaultValue) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(city, city);
                    editor.commit();
                    Toast.makeText(SearchResult.this, city + " was added to favorites", Toast.LENGTH_SHORT).show();
                    fab.setImageResource(R.drawable.minus);
                }
                else {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.remove(city);
                    editor.commit();
                    Toast.makeText(SearchResult.this, city + " was removed from favorites", Toast.LENGTH_SHORT).show();
                    fab.setImageResource(R.drawable.plus);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(it);
        finish();
    }

    private void initialize() {

        progress = this.findViewById(R.id.progressMasker);
        error = this.findViewById(R.id.errorMsg);
        resHint = this.findViewById(R.id.resultHint);

        title = this.findViewById(R.id.title_city);
        backButton = this.findViewById(R.id.back_button);

        iconTable = new HashMap<>();
        iconTable.put("clear-day", "weather_sunny");
        iconTable.put("clear-night", "weather_night");
        iconTable.put("rain", "weather_rainy");
        iconTable.put("sleet", "weather_snowy_rainy");
        iconTable.put("snow", "weather_snowy");
        iconTable.put("wind", "weather_windy_variant");
        iconTable.put("fog", "weather_fog");
        iconTable.put("cloudy", "weather_cloudy");
        iconTable.put("partly-cloudy-night", "weather_night_partly_cloudy");
        iconTable.put("partly-cloudy-day", "weather_partly_cloudy");

        iconImg = this.findViewById(R.id.card_top_1_icon);
        moreInfo = this.findViewById(R.id.card_top_1_info);
        cityText = this.findViewById(R.id.card_top_1_city);
        tempText = this.findViewById(R.id.card_top_1_temp);
        sumText = this.findViewById(R.id.card_top_1_sum);

        humidText = this.findViewById(R.id.card_mid_1_humid);
        windText = this.findViewById(R.id.card_mid_1_wind);
        visiText = this.findViewById(R.id.card_mid_1_visible);
        pressText = this.findViewById(R.id.card_mid_1_press);

        for (int i = 0; i < 8; i++) {
            Context ctx = getBaseContext();
            dates[i] = this.findViewById(ctx.getResources().
                    getIdentifier("card_bot_1_date_" + (i + 1), "id", ctx.getPackageName()));
            dayIcons[i] = this.findViewById(ctx.getResources().
                    getIdentifier("card_bot_1_icon_" + (i + 1), "id", ctx.getPackageName()));
            lowTemps[i] = this.findViewById(ctx.getResources().
                    getIdentifier("card_bot_1_tempLow_" + (i + 1), "id", ctx.getPackageName()));
            highTemps[i] = this.findViewById(ctx.getResources().
                    getIdentifier("card_bot_1_tempHigh_" + (i + 1), "id", ctx.getPackageName()));
        }
    }

    private void getWeather() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = weatherUrl;
                url += "addr=" + city;
                url = url.replaceAll("\\s+", "%20");
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                try (Response response = client.newCall(request).execute()) {
                    weatherJson = new JSONObject(response.body().string());
                    // Handle invalid input address
                    if (weatherJson.optString("status").equals("ZERO_RESULTS")) {
                        updateErr();
                    }
                    else {
                        updateUI();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateErr() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resHint.setVisibility(View.VISIBLE);
                error.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        });
    }

    private void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                double temp = weatherJson.optJSONObject("currently").optDouble("temperature");
                String sum = weatherJson.optJSONObject("currently").optString("summary");
                String iconRes = iconTable.get(weatherJson.optJSONObject("currently").optString("icon"));
                tempText.setText(Math.round(temp) + "℉");
                sumText.setText(sum);
                cityText.setText(city);
                Context ctx = getBaseContext();
                iconImg.setImageResource(ctx.getResources().getIdentifier(iconRes, "drawable", ctx.getPackageName()));
                if (iconRes.equals("weather_sunny"))
                    iconImg.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.iconSunny)));

                long humid = Math.round(weatherJson.optJSONObject("currently").optDouble("humidity") * 100);
                double wind = weatherJson.optJSONObject("currently").optDouble("windSpeed");
                double visi = weatherJson.optJSONObject("currently").optDouble("visibility");
                double press = weatherJson.optJSONObject("currently").optDouble("pressure");
                humidText.setText(humid + "%");
                windText.setText(String.format("%.2f", wind) + " mph");
                visiText.setText(String.format("%.2f", visi) + " km");
                pressText.setText(String.format("%.2f", press) + " mb");

                try {
                    JSONArray dailys = weatherJson.optJSONObject("daily").optJSONArray("data");
                    for (int i = 0; i < dailys.length(); i++) {
                        TimeZone.setDefault(TimeZone.getTimeZone(weatherJson.optString("timezone")));
                        long time = dailys.getJSONObject(i).optLong("time") * 1000;
                        SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy");
                        String date = sdf.format(new Date(time));
                        dates[i].setText(date);

                        iconRes = iconTable.get(dailys.getJSONObject(i).optString("icon"));
                        dayIcons[i].setImageResource(ctx.getResources().getIdentifier(iconRes, "drawable", ctx.getPackageName()));
                        if (iconRes.equals("weather_sunny"))
                            dayIcons[i].setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.iconSunny)));

                        double low = dailys.getJSONObject(i).optDouble("temperatureLow");
                        lowTemps[i].setText(Math.round(low) + "");

                        double high = dailys.getJSONObject(i).optDouble("temperatureHigh");
                        highTemps[i].setText(Math.round(high) + "");
                    }

                    resHint.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
