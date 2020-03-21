package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputType;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myapplication.ui.main.MainPagerAdapter;
import com.example.myapplication.ui.main.PhotoAdapter;
import com.example.myapplication.ui.main.SectionsPagerAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String ipUrl = "http://ip-api.com/json";
    private static final String weatherUrl = "https://homework9-259522.appspot.com/weatherSearch2?";
    private static final String autoUrl = "https://homework9-259522.appspot.com/autoSearch?";
    private JSONObject ipJson = null, weatherJson = null, predictJson = null;
    private TextView cityText, tempText, sumText, humidText, windText, visiText, pressText;
    private ImageView iconImg, moreInfo;
    private String city, state, country;
    public static HashMap<String, String> iconTable;

    private TextView[] dates = new TextView[8];
    private ImageView[] dayIcons = new ImageView[8];
    private TextView[] lowTemps = new TextView[8], highTemps = new TextView[8];

    private LinearLayout progress;
    private SharedPreferences sp;

    private MainPagerAdapter mainPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabs;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private FloatingActionButton fab;
    private static final String defaultValue = "Null";

    private ArrayList<ImageView> dots = new ArrayList<>();

    private void selectDotStyle(int pos) {
        for(int i = 0; i < dots.size(); i++) {
            if (i != pos)
                dots.get(i).setImageDrawable(getResources().getDrawable(R.drawable.circle_black));
            else
                dots.get(i).setImageDrawable(getResources().getDrawable(R.drawable.circle_white));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_boss);

        sp = getSharedPreferences("favorites", MODE_PRIVATE);

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

        progress = this.findViewById(R.id.progressMasker);

        Map<String, ?> spSet = sp.getAll();
        int cnt = 0;
        for (Map.Entry<String, ?> spItem : spSet.entrySet()) {
            cnt++;
        }
        cnt++;

        final LinearLayout dotsLayout = (LinearLayout)findViewById(R.id.dotIndicator);

        for(int i = 0; i < cnt; i++) {
            ImageView dot = new ImageView(this);

            dot.setImageDrawable(getResources().getDrawable(R.drawable.circle_black));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(5, 5, 5, 5);
            dotsLayout.addView(dot, params);

            dots.add(dot);
        }

        mainPagerAdapter = new MainPagerAdapter(this, cnt);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(mainPagerAdapter);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                final View v = mainPagerAdapter.getView(position);
                selectDotStyle(position);

                fab = v.findViewById(R.id.fab);
                if (position != 0) {
                    fab.clearAnimation();
                    fab.show();
                }
                iconImg = v.findViewById(R.id.card_top_1_icon);
                moreInfo = v.findViewById(R.id.card_top_1_info);
                cityText = v.findViewById(R.id.card_top_1_city);
                tempText = v.findViewById(R.id.card_top_1_temp);
                sumText = v.findViewById(R.id.card_top_1_sum);

                humidText = v.findViewById(R.id.card_mid_1_humid);
                windText = v.findViewById(R.id.card_mid_1_wind);
                visiText = v.findViewById(R.id.card_mid_1_visible);
                pressText = v.findViewById(R.id.card_mid_1_press);

                Context ctx = v.getContext();
                for (int i = 0; i < 8; i++) {
                    dates[i] = v.findViewById(ctx.getResources().
                            getIdentifier("card_bot_1_date_" + (i + 1), "id", ctx.getPackageName()));
                    dayIcons[i] = v.findViewById(ctx.getResources().
                            getIdentifier("card_bot_1_icon_" + (i + 1), "id", ctx.getPackageName()));
                    lowTemps[i] = v.findViewById(ctx.getResources().
                            getIdentifier("card_bot_1_tempLow_" + (i + 1), "id", ctx.getPackageName()));
                    highTemps[i] = v.findViewById(ctx.getResources().
                            getIdentifier("card_bot_1_tempHigh_" + (i + 1), "id", ctx.getPackageName()));
                }

                final String place = searchList.get(position);
                moreInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(getApplicationContext(), DetailActivity.class);
                        it.putExtra("city", place);
                        it.putExtra("json", jsons.get(place).toString());
                        startActivity(it);
                    }
                });

                weatherJson = jsons.get(place);
                double temp = weatherJson.optJSONObject("currently").optDouble("temperature");
                String sum = weatherJson.optJSONObject("currently").optString("summary");
                String iconRes = iconTable.get(weatherJson.optJSONObject("currently").optString("icon"));
                tempText.setText(Math.round(temp) + "℉");
                sumText.setText(sum);
                cityText.setText(place);

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
                } catch (Exception e) {
                    e.printStackTrace();
                }

                sp = getSharedPreferences("favorites", MODE_PRIVATE);
                if (sp.getString(place, defaultValue) == defaultValue) {
                    fab.setImageResource(R.drawable.plus);
                }
                else {
                    fab.setImageResource(R.drawable.minus);
                }
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (sp.getString(place, defaultValue) == defaultValue) {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(place, place);
                            editor.commit();
                            Toast.makeText(MainActivity.this, place + " was added to favorites", Toast.LENGTH_SHORT).show();
                            fab.setImageResource(R.drawable.minus);
                        }
                        else {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.remove(place);
                            editor.commit();
                            Toast.makeText(MainActivity.this, place + " was removed from favorites", Toast.LENGTH_SHORT).show();
                            fab.setImageResource(R.drawable.plus);
                            mainPagerAdapter.removeView(position);
                            dotsLayout.removeViewAt(position);
                            dots.remove(position);
                            searchList.remove(place);
                            isOkay.remove(place);
                            jsons.remove(place);

                            // Force select every time the view should be updated
                            viewPager.post(new Runnable(){
                                @Override
                                public void run() {
                                    onPageChangeListener.onPageSelected(viewPager.getCurrentItem());
                                }
                            });
                        }

                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        getIPPos();
    }

    private String[] candidates;
    private BanFilterAdapter newsAdapter;
    private SearchView.SearchAutoComplete searchAutoComplete;
    private SearchView searchView;
    private boolean selected;
    private MenuItem searchMenu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        searchMenu = menu.findItem(R.id.weatherSearch);
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenu);

        searchAutoComplete = searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setBackgroundColor(getResources().getColor(R.color.cardBackground));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.pureWhite));
        searchAutoComplete.setDropDownBackgroundResource(R.color.pureWhite);
        searchAutoComplete.setThreshold(1);

        candidates = new String[0];
        newsAdapter = new BanFilterAdapter(this, android.R.layout.simple_dropdown_item_1line, candidates);
        searchAutoComplete.setAdapter(newsAdapter);

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                selected = true;
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // Trigger when click the search button on the pop-up keyboard
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setIconified(true);
                searchMenu.collapseActionView();
                Intent it = new Intent(getApplicationContext(), SearchResult.class);
                it.putExtra("city", query);
                startActivity(it);
                finish();
                return false;
            }

            // It seems that the mechanism does not allow an empty string, but it is hard to be detected
            @Override
            public boolean onQueryTextChange(String newText) {
                if (selected) {
                    selected = false;
                    return false;
                }
                getPrediction(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void getIPPos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = ipUrl;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                try (Response response = client.newCall(request).execute()) {
                    ipJson = new JSONObject(response.body().string());
                    getWeather();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private ArrayList<String> searchList = new ArrayList<>();
    private Map<String, Boolean> isOkay = new HashMap<>();
    private Map<String, JSONObject> jsons = new HashMap<>();
    private void getWeather() {
        final double lat = ipJson.optDouble("lat");
        final double lon = ipJson.optDouble("lon");
        city = ipJson.optString("city");
        state = ipJson.optString("region");
        // country = ipJson.optString("country");
        country = "USA";
        String ipCity = city + ", " + state + ", " + country;
        searchList.add(ipCity);
        isOkay.put(ipCity, false);
        Map<String, ?> favors = sp.getAll();
        for (Map.Entry<String, ?> favor : favors.entrySet()) {
            searchList.add(favor.getKey());
            isOkay.put(favor.getKey(), false);
        }
        stateListener();
        for (String str : searchList) {
            searchThread(str);
        }
    }

    private void searchThread(final String keyWord) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = weatherUrl;
                url += "addr=" + keyWord;
                url = url.replaceAll("\\s+", "%20");
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                try (Response response = client.newCall(request).execute()) {
                    jsons.put(keyWord, new JSONObject(response.body().string()));
                    isOkay.put(keyWord, true);
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
                    boolean flag = true;
                    for (Map.Entry<String, Boolean> okay : isOkay.entrySet()) {
                        if (okay.getValue() == false)
                            flag = false;
                    }
                    if (flag)
                        break;
                }
                updateUI();
            }
        }).start();
    }

    private void getPrediction(final String keyword) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = autoUrl + "input=" + keyword;
                url = url.replaceAll("\\s+", "%20");
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                try (Response response = client.newCall(request).execute()) {
                    predictJson = new JSONObject(response.body().string());
                    updateAutoList(keyword);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateAutoList(final String keyword) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // If the number of result is zero, the json array also has zero size.
                    // So there is no need to handle it.
                    JSONArray list = predictJson.optJSONArray("predictions");
                    candidates = new String[list.length()];
                    for (int i = 0; i < list.length(); i++) {
                        candidates[i] = list.getJSONObject(i).optString("description");
                    }
                    newsAdapter = new BanFilterAdapter(getBaseContext(), android.R.layout.simple_dropdown_item_1line, candidates);
                    searchAutoComplete.setAdapter(newsAdapter);
                    if (candidates.length > 0 && !keyword.equals(""))
                        searchAutoComplete.showDropDown();
                    else
                        searchAutoComplete.dismissDropDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                progress.setVisibility(View.GONE);
                viewPager.addOnPageChangeListener(onPageChangeListener);
                viewPager.post(new Runnable(){
                    @Override
                    public void run() {
                        onPageChangeListener.onPageSelected(viewPager.getCurrentItem());
                    }
                });
                progress.setVisibility(View.GONE);
            }
        });
    }

}

// Disable auto filtering when displaying the candidate list
class BanFilterAdapter extends ArrayAdapter<String> {
    public BanFilterAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results){

            }
        };
    }
}
