package com.project.lb3labs.ecashier;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeNavigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String url_laporan = ServerAll.URL;
    private Bundle bundle;
    private WebView webView;
    private String username;
    private String owner;
    private String business;
    private TextView usernameText;
    private final String TAG = "HomeNavigation";
    private ProgressDialog pDialog;

    private final String TAG_PRODUCT_ID = "procode";
    private final String TAG_PRODUCT_NAME = "product_name";
    private final String TAG_PRICE = "prod_price";

    private final String tag_json_obj = "json_obj_req";

    private int procode, price;
    private String prodname;

    private final String url_2 = Server.URL + "get_selectedproduct.php";
    private final String TAG_LOOP = "loop";
    private int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        webView = findViewById(R.id.activity_webview);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        owner = intent.getStringExtra("owner");
        business = intent.getStringExtra("business").toLowerCase();
        Log.e("BISNIS", business);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);
        usernameText = hView.findViewById(R.id.username_display);
        usernameText.setText(owner);

        displayView(R.id.nav_kasir);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (webView != null) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            //Toast.makeText(this, "Hai!", Toast.LENGTH_SHORT).show();
            paymentOptions();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void paymentOptions() {
        // Setting edittext
        final EditText input = new EditText(HomeNavigation.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setLayoutParams(lp);

        final AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(HomeNavigation.this);
        //alertdialogbuilder.setMessage("Silakan pilih metode pembayaran di bawah ini");
        alertdialogbuilder.setTitle("Cari barang");
        alertdialogbuilder.setView(input);
        //alertdialogbuilder.setMessage("Cari");
        alertdialogbuilder.setPositiveButton("Cari", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                final AlertDialog alertDialog = alertdialogbuilder.create();
                StringRequest strReqs = new StringRequest(Request.Method.POST, url_2, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //Log.d("RESPONSE",response);

                        try {
                            JSONObject jObj = new JSONObject(response);
                            JSONArray result = jObj.getJSONArray("result");
                            Log.e("RESULT", String.valueOf(result.length()));
                            ArrayList<String> product_lists = new ArrayList<>();

                            for(int x=0;x<result.length();x++) {
                                JSONObject jsonObj = result.getJSONObject(x);
                                procode = jsonObj.getInt("procode");
                                prodname = jsonObj.getString("product_name");
                                price = jsonObj.getInt("prod_price");
                                Log.e("proname", prodname);

                                /*
                                Log.e("product code", String.valueOf(procode));
                                Log.e("product name", prodname);
                                Log.e("product price", String.valueOf(price));
                                */

                                ArrayList<String> product_array = new ArrayList<>();
                                product_array.add(0, String.valueOf(procode));
                                product_array.add(1, prodname);
                                product_array.add(2, String.valueOf(price));

                                product_lists.add(String.valueOf(product_array));


                                // getting inner array Ingredients (IF THERE IS ARRAY IN AN ARRAY)
                                /*
                                JSONArray ja = jsonObj.getJSONArray("Ingredients");
                                int len = ja.length();

                                ArrayList<String> Ingredients_names = new ArrayList<>();
                                for(int j=0; j<len; j++) {
                                    JSONObject json = ja.getJSONObject(j);
                                    Ingredients_names.add(json.getString("name"));
                                }
                                */

                            }
                            Log.i("PRODUCT LISTING", String.valueOf(product_lists));

                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(HomeNavigation.this, android.R.layout.select_dialog_singlechoice, product_lists);

                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(HomeNavigation.this);
                            builderSingle.setTitle("Daftar Produk");
                            if(product_lists.isEmpty()){
                                builderSingle.setMessage("Produk tidak ditemukan");
                            } else {
                                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        /*
                                        String strName = arrayAdapter.getItem(which);
                                        AlertDialog.Builder builderInner = new AlertDialog.Builder(HomeNavigation.this);
                                        builderInner.setMessage(strName);
                                        builderInner.setTitle("Your Selected Item is");
                                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builderInner.show();
                                        */
                                    }
                                });
                            }
                            builderSingle.show();

                            /*
                            for(int x=result.length()-1;x<result.length();x++) {

                                JSONObject collegeDays = result.getJSONObject(x);
                                //For Fetching Second type json
                            }
                            */

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(HomeNavigation.this, "404", Toast.LENGTH_LONG).show();
                        //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<>();
                        params.put("prodname", input.getText().toString());

                        //params.put("pay", input.getText().toString());

                        return params;
                    }
                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReqs, tag_json_obj);
            }
        });

        AlertDialog alertDialog = alertdialogbuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displayView(item.getItemId());
        return true;
    }

    public void displayView(int viewId) {

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.nav_kasir:
                fragment = new NewMainActivity();
                title  = "e-Cashier";

                break;
            case R.id.nav_laporan:
                /*
                fragment = new NewMainActivity();
                title = "Events";
                */

                Intent intent = new Intent(HomeNavigation.this, BrowserActivity.class);
                startActivity(intent);
                intent.putExtra("link", url_laporan + "?database=" + business);
                intent.putExtra("print",0);
                startActivity(intent);

                break;
            case R.id.nav_input_id:
                bundle = new Bundle();
                bundle.putString("url_page", "in_id_activity.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Input ID Activity";

                break;
            case R.id.nav_input_name:
                bundle = new Bundle();
                bundle.putString("url_page", "in_product.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Input Nama Barang";

                break;
            case R.id.nav_input_brand:
                bundle = new Bundle();
                bundle.putString("url_page", "in_brand.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Input Jenis Barang";

                break;
            case R.id.nav_input_category:
                bundle = new Bundle();
                bundle.putString("url_page", "in_category.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Input Kategori Barang";

                break;
            case R.id.nav_input_supplier:
                bundle = new Bundle();
                bundle.putString("url_page", "in_supplier.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Input Nama Supplier";

                break;
            case R.id.nav_check_id:
                bundle = new Bundle();
                bundle.putString("url_page", "cek_id_activity.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Cek ID Activity";

                break;
            case R.id.nav_check_name:
                bundle = new Bundle();
                bundle.putString("url_page", "cek_product.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Cek Nama Barang";

                break;
            case R.id.nav_check_brand:
                bundle = new Bundle();
                bundle.putString("url_page", "cek_brand.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Cek Jenis Barang";

                break;
            case R.id.nav_check_category:
                bundle = new Bundle();
                bundle.putString("url_page", "cek_category.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Cek Kategori Barang";

                break;
            case R.id.nav_check_supplier:
                bundle = new Bundle();
                bundle.putString("url_page", "cek_supplier.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Cek Supplier";

                break;
            case R.id.nav_kas_harian:
                bundle = new Bundle();
                bundle.putString("url_page", "laporan_kas_harian.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Laporan Kas Harian";

                break;
            case R.id.nav_kas_bulanan:
                bundle = new Bundle();
                bundle.putString("url_page", "laporan_kas_bulanan.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Laporan Kas Bulanan";

                break;
            case R.id.nav_kas_tahunan:
                bundle = new Bundle();
                bundle.putString("url_page", "laporan_kas_tahunan.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Laporan Kas Tahunan";

                break;
            case R.id.nav_jual_harian:
                bundle = new Bundle();
                bundle.putString("url_page", "cek_supplier.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Cek Supplier";

                break;
            case R.id.nav_jual_bulanan:
                bundle = new Bundle();
                bundle.putString("url_page", "cek_supplier.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Cek Supplier";

                break;
            case R.id.nav_jual_tahunan:
                bundle = new Bundle();
                bundle.putString("url_page", "cek_supplier.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Cek Supplier";

                break;
            case R.id.nav_beli_harian:
                bundle = new Bundle();
                bundle.putString("url_page", "cek_supplier.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Cek Supplier";

                break;
            case R.id.nav_beli_bulanan:
                bundle = new Bundle();
                bundle.putString("url_page", "cek_supplier.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Cek Supplier";

                break;
            case R.id.nav_beli_tahunan:
                bundle = new Bundle();
                bundle.putString("url_page", "cek_supplier.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Cek Supplier";

                break;
            case R.id.nav_laba_harian:
                bundle = new Bundle();
                bundle.putString("url_page", "cek_supplier.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Cek Supplier";

                break;
            case R.id.nav_laba_bulanan:
                bundle = new Bundle();
                bundle.putString("url_page", "cek_supplier.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Cek Supplier";

                break;
            case R.id.nav_laba_tahunan:
                bundle = new Bundle();
                bundle.putString("url_page", "cek_supplier.php?database=" + business);
                fragment = new BrowserFragment();
                fragment.setArguments(bundle);
                title  = "Cek Supplier";

                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);

            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    // SHOWING ProgressDialog
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
