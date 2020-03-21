package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;

import com.example.myapplication.ui.main.PhotoAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.ui.main.SectionsPagerAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {

    private static final String tweetUrl = "https://twitter.com/intent/tweet?";
    private static final String photoUrl = "https://homework9-259522.appspot.com/photoSearch?";
    private String city;
    private String jsonStr;
    private TextView title;
    private ImageView twitter, backButton;
    private JSONObject weatherJson = null, photoJson = null;
    private long temperature;
    private LinearLayout progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle intent = this.getIntent().getExtras();
        city = intent.getString("city");
        jsonStr = intent.getString("json");
        try {
            weatherJson = new JSONObject(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        progress = this.findViewById(R.id.progressMasker);
        temperature = Math.round(weatherJson.optJSONObject("currently").optDouble("temperature"));
        title = this.findViewById(R.id.title_city);
        title.setText(city);

        twitter = this.findViewById(R.id.twitter);
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = tweetUrl;
                String query = "text=Check Out " + city +"'s Weather! It is " +
                        temperature + "℉! &hashtags=CSCI571WeatherSearch";
                url += query;

                // Open a new window in browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        backButton = this.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, 3);
        final ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        final TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.setTabTextColors(getResources().getColor(R.color.fade), getResources().getColor(R.color.pureWhite));

        // Simple setting for icons above the tab text
        tabs.getTabAt(0).setIcon(R.drawable.today);
        tabs.getTabAt(0).setText(R.string.tab_text_1);
        tabs.getTabAt(1).setIcon(R.drawable.weekly_fade);
        tabs.getTabAt(1).setText(R.string.tab_text_2);
        tabs.getTabAt(2).setIcon(R.drawable.photos_fade);
        tabs.getTabAt(2).setText(R.string.tab_text_3);

        // Switch icons in selected/unselected modes
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setTint(getResources().getColor(R.color.pureWhite));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setTint(getResources().getColor(R.color.fade));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                View v = sectionsPagerAdapter.getView(position);
                if (position == 0) {
                    LinearLayout progress = v.findViewById(R.id.progressMasker);

                    JSONObject current = weatherJson.optJSONObject("currently");
                    TextView windSpeed = v.findViewById(R.id.today_windSpeed);
                    windSpeed.setText(String.format("%.2f", current.optDouble("windSpeed")) + " mph");

                    TextView pressure = v.findViewById(R.id.today_press);
                    pressure.setText(String.format("%.2f", current.optDouble("pressure")) + " mb");

                    TextView rain = v.findViewById(R.id.today_rain);
                    rain.setText(String.format("%.2f", current.optDouble("precipIntensity")) + " mmph");

                    TextView temp = v.findViewById(R.id.today_temp);
                    temp.setText(Math.round(current.optDouble("temperature")) + "℉");

                    ImageView icon = v.findViewById(R.id.today_icon);
                    TextView tx = v.findViewById(R.id.today_iconTxt);
                    String iconTxt = current.optString("icon");
                    String res = MainActivity.iconTable.get(iconTxt);

                    // Set String format for the text and icon style for the image
                    iconTxt = iconTxt.replaceAll("[-]+", " ");
                    iconTxt = iconTxt.replaceAll("partly", " ");
                    iconTxt = iconTxt.trim();
                    tx.setText(iconTxt);
                    Context ctx = getBaseContext();
                    icon.setImageResource(ctx.getResources().getIdentifier(res, "drawable", ctx.getPackageName()));
                    if (res.equals("weather_sunny"))
                        icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.iconSunny)));

                    TextView humid = v.findViewById(R.id.today_humid);
                    humid.setText(Math.round(current.optDouble("humidity") * 100) + "%");

                    TextView visi = v.findViewById(R.id.today_visi);
                    visi.setText(String.format("%.2f", current.optDouble("visibility")) + " km");

                    TextView cloud = v.findViewById(R.id.today_cloud);
                    cloud.setText(Math.round(current.optDouble("cloudCover") * 100) + "%");

                    TextView ozone = v.findViewById(R.id.today_ozone);
                    ozone.setText(String.format("%.2f", current.optDouble("ozone")) + " DU");

                    progress.setVisibility(View.GONE);
                }
                else if (position == 1) {
                    LinearLayout progress = v.findViewById(R.id.progressMasker);

                    JSONObject week = weatherJson.optJSONObject("daily");
                    TextView sum = v.findViewById(R.id.week_sum);
                    sum.setText(week.optString("summary"));

                    ImageView icon = v.findViewById(R.id.week_icon);
                    Context ctx = getBaseContext();
                    String res = MainActivity.iconTable.get(week.optString("icon"));
                    icon.setImageResource(ctx.getResources().getIdentifier(res, "drawable", ctx.getPackageName()));
                    if (res.equals("weather_sunny"))
                        icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.iconSunny)));

                    LineChart chart = (LineChart)v.findViewById(R.id.chart);
                    Legend legend = chart.getLegend();

                    chart.getXAxis().setTextColor(getResources().getColor(R.color.pureWhite));
                    chart.getAxisLeft().setTextColor(getResources().getColor(R.color.pureWhite));
                    chart.getAxisRight().setTextColor(getResources().getColor(R.color.pureWhite));

                    chart.setNoDataText("");
                    legend.setTextColor(getResources().getColor(R.color.pureWhite));
                    legend.setTextSize(16.0F);

                    chart.getDescription().setEnabled(false);

                    XAxis xAxis = chart.getXAxis();
                    xAxis.setDrawGridLines(false);

                    JSONArray dailys = weatherJson.optJSONObject("daily").optJSONArray("data");
                    ArrayList<Entry> lowList = new ArrayList<>(), highList = new ArrayList<>();
                    for (int i = 0; i < dailys.length(); i++) {
                        int itemLow = 0, itemHigh = 0;
                        try {
                            itemLow = (int)Math.round(dailys.getJSONObject(i).optDouble("temperatureLow"));
                            itemHigh = (int)Math.round(dailys.getJSONObject(i).optDouble("temperatureHigh"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        lowList.add(new Entry(i, itemLow));
                        highList.add(new Entry(i, itemHigh));
                    }

                    LineDataSet lowSet = new LineDataSet(lowList, "Minimum Temperature");
                    LineDataSet highSet = new LineDataSet(highList, "Maximum Temperatue");

                    lowSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    lowSet.setDrawValues(false);

                    // Notice that you should add real RGB values here instead of the color id
                    lowSet.setColor(getResources().getColor(R.color.iconProperty));
                    lowSet.setHighlightEnabled(false);
                    lowSet.setLineWidth(2.0F);
                    lowSet.setCircleSize(3.2F);
                    lowSet.setCircleHoleRadius(1.5F);

                    highSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    highSet.setDrawValues(false);

                    // Notice that you should add real RGB values here instead of the color id
                    highSet.setColor(getResources().getColor(R.color.iconSunny));
                    highSet.setHighlightEnabled(false);
                    highSet.setLineWidth(2.0F);
                    highSet.setCircleSize(3.2F);
                    highSet.setCircleHoleRadius(1.5F);

                    LineData allData = new LineData(lowSet, highSet);
                    chart.setData(allData);

                    // Refresh the chart view for display
                    chart.invalidate();
                    progress.setVisibility(View.GONE);
                }
                else {
                    final RecyclerView photoShop = v.findViewById(R.id.photoShop);
                    photoShop.setLayoutManager(new LinearLayoutManager(v.getContext()));
                    final PhotoAdapter photoAdapter = new PhotoAdapter(urls, getBaseContext());
                    photoShop.setAdapter(photoAdapter);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        viewPager.addOnPageChangeListener(onPageChangeListener);
        // To make sure that onPageSelected is also called at the first page at the beginning
        viewPager.post(new Runnable(){
            @Override
            public void run() {
                onPageChangeListener.onPageSelected(viewPager.getCurrentItem());
            }
        });

        getPhotos(city);
    }

    private ArrayList<String> urls = new ArrayList<>();
    private Map<Integer, Boolean> isOkay = new HashMap<>();
    private void getPhotos(final String keyword) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = photoUrl + "q=" + keyword;
                url = url.replaceAll("\\s+", "%20");
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                try (Response response = client.newCall(request).execute()) {
                    photoJson = new JSONObject(response.body().string());

                    JSONArray items = photoJson.optJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        urls.add(items.getJSONObject(i).optString("link"));
                        final int k = i;

                        // Asynchronous method, pre-load the images and cache them
                        Picasso.get().load(urls.get(i)).fetch(new Callback() {
                            @Override
                            public void onSuccess() {
                                isOkay.put(k, true);
                            }

                            @Override
                            public void onError(Exception e) {
                                isOkay.put(k, false);
                            }
                        });
                    }

                    stateListener();
                    updateUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void stateListener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isOkay.size() == 8)
                        break;
                }
                updateUI();
            }
        }).start();
    }
    private void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.GONE);
            }
        });
    }

}