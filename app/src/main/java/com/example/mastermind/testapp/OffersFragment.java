package com.example.mastermind.testapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * Created by Kostas on 3/6/2018.
 */

public class OffersFragment extends Fragment {
    SharedPreferences settingsPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
    ArrayList<JobOffer> offers;

    String message = "";
    RequestQueue queue;
    String categoriesIds,areaIds;

    ImageButton imgBtn_ad;

    ListView lv;
    DateFormat format;
    String[] paths;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_offers, container, false);

        lv = rootView.findViewById(R.id.listView);
        offers = new ArrayList<>();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        imgBtn_ad = rootView.findViewById(R.id.imgBtn_ad);


        settingsPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        System.out.println(settingsPreferences.getInt("numberOfCategories", 0) == 0);
        System.out.println(settingsPreferences.getInt("numberOfCheckedCategories", 0) == 0);
        System.out.println(settingsPreferences.getInt("numberOfAreas", 0) == 0);
        System.out.println(settingsPreferences.getInt("numberOfCheckedAreas", 0) == 0);


        System.out.println(settingsPreferences.getBoolean("checkIsChanged", false));

        System.out.println(settingsPreferences.getInt("numberOfOffers", 0));
        for (int i = 0; i < settingsPreferences.getInt("numberOfOffers", 0); i++) {

            JobOffer jobOffer = new JobOffer();
            jobOffer.setId(settingsPreferences.getInt("offerId " + i, 0));
            jobOffer.setCatid(settingsPreferences.getInt("offerCatid " + i, 0));
            jobOffer.setCattitle(settingsPreferences.getString("offerCattitle " + i, ""));
            jobOffer.setAreaid(settingsPreferences.getInt("offerAreaid " + i, 0));
            jobOffer.setAreatitle(settingsPreferences.getString("offerAreatitle " + i, ""));
            jobOffer.setTitle(settingsPreferences.getString("offerTitle " + i, ""));
            jobOffer.setLink(settingsPreferences.getString("offerLink " + i, ""));
            jobOffer.setDesc(settingsPreferences.getString("offerDesc " + i, ""));
            jobOffer.setDate(new Date(settingsPreferences.getLong("offerDate " + i, 0)));
            jobOffer.setDownloaded(settingsPreferences.getString("offerDownloaded " + i, ""));
            System.out.println(jobOffer.getTitle()+" Is it empty");
            offers.add(jobOffer);

        }

        for (int i = 0; i < settingsPreferences.getInt("numberOfOffers", 0); i++) {
            System.out.println(offers.get(i).getTitle());
            System.out.println(offers.get(i).getId());
            System.out.println(offers.get(i).getCattitle());
            System.out.println(offers.get(i).getAreatitle());
            System.out.println(offers.get(i).getDate().toString());
        }
        JobOfferAdapter jobOfferAdapter = new JobOfferAdapter(MyApplication.getAppContext(), offers);
        lv.setAdapter(jobOfferAdapter);
        System.out.println(settingsPreferences.getLong("interval",0));





        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intentToDetail = new Intent(MyApplication.getAppContext(),DetailActivity.class);
                intentToDetail.putExtra("jobOffer", (Serializable) adapterView.getItemAtPosition(i));
                startActivity(intentToDetail);

            }
        });
        if(settingsPreferences.getInt("numberOfImages",0)>0) {
            paths = new String[settingsPreferences.getInt("numberOfImages", 0)];

            for (int i = 1; i <= paths.length; i++) {
                paths[i - 1] = settingsPreferences.getString("imageUri" + i, "");
            }
            loadImageFromStorage(paths);
        }

        imgBtn_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://datalabs.edu.gr/"));
                startActivity(browseIntent);
            }
        });



        return rootView;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if(settingsPreferences.getInt("numberOfImages",0)>0) {
//            paths = new String[settingsPreferences.getInt("numberOfImages", 0)];
//
//            for (int i = 1; i <= paths.length; i++) {
//                paths[i - 1] = settingsPreferences.getString("imageUri" + i, "");
//            }
//            loadImageFromStorage(paths);
//        }
//    }

    private void loadImageFromStorage(String[] paths)
    {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        for(String path : paths) {


            try {
                File d = new File(path);
                System.out.println("This is the path to upload: " + d.toString());
                bitmaps.add(BitmapFactory.decodeStream(new FileInputStream(d)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        Random r = new Random();

        int rnum =r.nextInt(paths.length);
        imgBtn_ad.setVisibility(View.VISIBLE);
        imgBtn_ad.setImageBitmap(bitmaps.get(rnum));

    }

    public void RefreshOperation() {

        SharedPreferences settingsPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(isConn()) {
            categoriesIds = "";
            areaIds = "";

            if (queue == null) {
                queue = Volley.newRequestQueue(MyApplication.getAppContext());
            }

            for (int v = 0; v < (settingsPreferences.getInt("numberOfCheckedCategories", 0)); v++) {
                if (v < settingsPreferences.getInt("numberOfCheckedCategories", 0) - 1) {
                    categoriesIds += settingsPreferences.getInt("checkedCategoryId " + v, 0) + ",";
                } else
                    categoriesIds += settingsPreferences.getInt("checkedCategoryId " + v, 0);
            }
            for (int v = 0; v < (settingsPreferences.getInt("numberOfCheckedAreas", 0)); v++) {
                if (v < settingsPreferences.getInt("numberOfCheckedAreas", 0) - 1) {
                    areaIds += settingsPreferences.getInt("checkedAreaId " + v, 0) + ",";
                } else
                    areaIds += settingsPreferences.getInt("checkedAreaId " + v, 0);
            }


            queue.add(volleySetCheckedCategories(categoriesIds, areaIds));

//            if (settingsPreferences.getInt("numberOfImages", 0) > 0) {
//                paths = new String[settingsPreferences.getInt("numberOfImages", 0)];
//
//                for (int i = 1; i <= paths.length; i++) {
//                    paths[i - 1] = settingsPreferences.getString("imageUri" + i, "");
//                }
//                loadImageFromStorage(paths);
//            }

        }else{
            Toast.makeText(MyApplication.getAppContext(),"Πρέπει να είστε συνδεδεμένος στο ίντερνετ για να κάνετε ανανέωση!",Toast.LENGTH_LONG).show();
        }


    }

    public StringRequest volleySetCheckedCategories(final String param, final String param2) {
        String url = Utils.getUrl()+"jobAdsArray.php?";


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<JobOffer> offersRefresh = new ArrayList<>();
                        ArrayList<JobOffer> asyncOffers = new ArrayList<>();
                        JobOfferAdapter jobOfferAdapter= new JobOfferAdapter(MyApplication.getAppContext(), offersRefresh);

                        // Display the first 500 characters of the response string.
                        System.out.println("Volley: " + message);
                        System.out.println(response);
                        try {
                            JSONObject jsonObjectAll = new JSONObject(response);
                            JSONArray jsonArray = jsonObjectAll.getJSONArray("offers");
                            int i = 0;

                            while (i < jsonArray.length() && i < 5) {


                                JSONObject jsonObjectCategory = jsonArray.getJSONObject(i);


                                JobOffer offer = new JobOffer();
                                offer.setId(Integer.valueOf(jsonObjectCategory.getString("jad_id")));
                                offer.setCatid(Integer.valueOf(jsonObjectCategory.getString("jad_catid")));
                                offer.setAreaid(Integer.valueOf(jsonObjectCategory.getString("jloc_id")));
                                offer.setTitle(jsonObjectCategory.getString("jad_title"));
                                offer.setCattitle(jsonObjectCategory.getString("jacat_title"));
                                offer.setAreatitle(jsonObjectCategory.getString("jloc_title"));
                                offer.setLink(jsonObjectCategory.getString("jad_link"));
                                offer.setDesc(jsonObjectCategory.getString("jad_desc"));
                                offer.setDate(format.parse(jsonObjectCategory.getString("jad_date")));
                                offer.setDownloaded(jsonObjectCategory.getString("jad_downloaded"));
                                System.out.println(offer.getTitle() + " first time");

                                asyncOffers.add(offer);

                                Collections.sort(asyncOffers, new Comparator<JobOffer>() {
                                    @Override
                                    public int compare(JobOffer jobOffer, JobOffer t1) {
                                        if (jobOffer.getDate().getTime() - t1.getDate().getTime() < 0)
                                            return 1;
                                        else if (jobOffer.getDate().getTime() - t1.getDate().getTime() == 0)
                                            return 0;
                                        else
                                            return -1;
                                    }
                                });
                                for (int x = 0; x < asyncOffers.size(); x++) {
                                    System.out.println(asyncOffers.get(x).getTitle());
                                }

                                i++;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(asyncOffers.size()>0) {
                            for (int j = 0; j < 5; j++) {
                                settingsPreferences.edit().remove("offerId " + j).apply();
                                settingsPreferences.edit().remove("offerCatid " + j).apply();
                                settingsPreferences.edit().remove("offerAreaid " + j).apply();
                                settingsPreferences.edit().remove("offerTitle " + j).apply();
                                settingsPreferences.edit().remove("offerCattitle " + j).apply();
                                settingsPreferences.edit().remove("offerAreatitle " + j).apply();
                                settingsPreferences.edit().remove("offerLink " + j).apply();
                                settingsPreferences.edit().remove("offerDesc " + j).apply();
                                settingsPreferences.edit().remove("offerDate " + j).apply();
                                settingsPreferences.edit().remove("offerDownloaded " + j).apply();
                            }

                            for (int i = 0; i < asyncOffers.size(); i++) {
                                System.out.println(asyncOffers.get(i).getTitle() + " in the Array that fills settings ");
                            }

                            for (int i = 0; i < asyncOffers.size(); i++) {
                                if (i < 5) {

                                    settingsPreferences.edit().putInt("offerId " + i, asyncOffers.get(i).getId()).apply();
                                    settingsPreferences.edit().putInt("offerCatid " + i, asyncOffers.get(i).getCatid()).apply();
                                    settingsPreferences.edit().putInt("offerAreaid " + i, asyncOffers.get(i).getAreaid()).apply();
                                    settingsPreferences.edit().putString("offerTitle " + i, asyncOffers.get(i).getTitle()).apply();
                                    settingsPreferences.edit().putString("offerCattitle " + i, asyncOffers.get(i).getCattitle()).apply();
                                    settingsPreferences.edit().putString("offerAreatitle " + i, asyncOffers.get(i).getAreatitle()).apply();
                                    settingsPreferences.edit().putString("offerLink " + i, asyncOffers.get(i).getLink()).apply();
                                    settingsPreferences.edit().putString("offerDesc " + i, asyncOffers.get(i).getDesc()).apply();
                                    settingsPreferences.edit().putLong("offerDate " + i, asyncOffers.get(i).getDate().getTime()).apply();
                                    settingsPreferences.edit().putString("offerDownloaded " + i, asyncOffers.get(i).getDownloaded()).apply();
                                    System.out.println(settingsPreferences.getLong("offerDate " + i, 0));
                                    System.out.println(settingsPreferences.getString("offerTitle " + i, ""));
                                    settingsPreferences.edit().putInt("numberOfOffers", asyncOffers.size()).apply();
                                } else
                                    settingsPreferences.edit().putInt("numberOfOffers", 5).apply();
                            }

                            settingsPreferences.edit().putLong("lastSeenDate", asyncOffers.get(0).getDate().getTime()).apply();
                            settingsPreferences.edit().putLong("lastNotDate", asyncOffers.get(0).getDate().getTime()).apply();

                            System.out.println(settingsPreferences.getLong("lastSeenDate", 0));

                        }



//                        for (int i = 0; i < settingsPreferences.getInt("numberOfOffers", 0); i++) {
//
//                            JobOffer jobOffer = new JobOffer();
//                            jobOffer.setId(settingsPreferences.getInt("offerId " + i, 0));
//                            jobOffer.setCatid(settingsPreferences.getInt("offerCatid " + i, 0));
//                            jobOffer.setAreaid(settingsPreferences.getInt("offerAreaid " + i, 0));
//                            jobOffer.setTitle(settingsPreferences.getString("offerTitle " + i, ""));
//                            jobOffer.setCattitle(settingsPreferences.getString("offerCattitle " + i, ""));
//                            jobOffer.setAreatitle(settingsPreferences.getString("offerAreatitle " + i, ""));
//                            jobOffer.setLink(settingsPreferences.getString("offerLink " + i, ""));
//                            jobOffer.setDesc(settingsPreferences.getString("offerDesc " + i, ""));
//                            jobOffer.setDate(new Date(settingsPreferences.getLong("offerDate " + i, 0)));
//                            jobOffer.setDownloaded(settingsPreferences.getString("offerDownloaded " + i, ""));
//                            offersRefresh.add(jobOffer);
//
//                        }
//
//
//                        lv.setAdapter(jobOfferAdapter);
//                        System.out.println(settingsPreferences.getLong("interval",0));
//
//
//                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                                Intent intentToDetail = new Intent(MyApplication.getAppContext(),DetailActivity.class);
//                                intentToDetail.putExtra("jobOffer", (Serializable) adapterView.getItemAtPosition(i));
//                                startActivity(intentToDetail);
//
//                            }
//                        });


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    message = "TimeOutError";
                    //This indicates that the reuest has either time out or there is no connection

                } else if (error instanceof AuthFailureError) {
                    message = "AuthFailureError";
                    // Error indicating that there was an Authentication Failure while performing the request

                } else if (error instanceof ServerError) {
                    message = "ServerError";
                    //Indicates that the server responded with a error response

                } else if (error instanceof NetworkError) {
                    message = "NetworkError";
                    //Indicates that there was network error while performing the request

                } else if (error instanceof ParseError) {
                    message = "ParseError";
                    // Indicates that the server response could not be parsed

                }
                System.out.println("Volley: " + message);
                if(!message.equals("")){
                    Toast.makeText(MyApplication.getAppContext(),Utils.getServerError(),Toast.LENGTH_LONG).show();
                    Intent intentError = new Intent(MyApplication.getAppContext(),MainActivity.class);
                    startActivity(intentError);
                }
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("jacat_id",param);
                params.put("jloc_id",param2);

                return params;
            }
        };
        return stringRequest;
    }

    public boolean isConn() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();
        Log.d("connection", "Wifi connected: " + isWifiConn);
        Log.d("connection", "Mobile connected: " + isMobileConn);
        return isWifiConn || isMobileConn;
    }

}