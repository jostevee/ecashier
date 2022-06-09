package com.project.lb3labs.ecashier;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeRegisterActivity extends AppCompatActivity {

    Button btn_register;
    EditText txt_business_name, txt_owner_name, txt_username, txt_password;
    Intent intent;
    ProgressDialog pDialog;

    int database_available, user_available;
    ConnectivityManager conMgr;

    private final String url = Server.URL + "check_database.php";

    private static final String TAG = HomeRegisterActivity.class.getSimpleName();

    private static final String TAG_AVAILABLE_USER = "user_available";
    private static final String TAG_AVAILABLE_DATABASE = "database_available";
    //private static final String TAG_MESSAGE = "message";

    public final static String TAG_USERNAME = "username";
    public final static String TAG_BUSINESS_NAME = "businessname";

    String tag_json_obj = "json_obj_req";

    SharedPreferences sharedpreferences;
    Boolean session = false;
    String username;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_register);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            assert conMgr != null;
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        btn_register = findViewById(R.id.registerButton);
        txt_business_name = findViewById(R.id.businessName);
        txt_owner_name = findViewById(R.id.ownerName);
        txt_username = findViewById(R.id.username);
        txt_password = findViewById(R.id.password);

        /*
        // Cek session login jika TRUE maka langsung buka MainActivity
        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        username = sharedpreferences.getString(TAG_USERNAME, null);

        if (session) {
            intent = new Intent(HomeRegisterActivity.this, HomeNavigation.class);
            intent.putExtra(TAG_USERNAME, username);
            finish();
            startActivity(intent);
        }
        */

        btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String business_name = txt_business_name.getText().toString();
                String owner_name = txt_owner_name.getText().toString();
                String username = txt_username.getText().toString();
                String password = txt_password.getText().toString();

                // mengecek kolom yang kosong
                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        Register(business_name, owner_name, username, password);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(), "Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void Register(final String business_name, final String owner_name, final String username, final String password) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Registering ...");
        showDialog();

        // In URL section you can add some variables if it's GET method
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    database_available = jObj.getInt(TAG_AVAILABLE_DATABASE);
                    user_available = jObj.getInt(TAG_AVAILABLE_USER);

                    // Check for error node in json
                    if (database_available == 0 && user_available == 0) {
                        //String username = jObj.getString(TAG_USERNAME);
                        Log.e("Success!", jObj.toString());

                        Toast.makeText(getApplicationContext(), "Successfully registered!", Toast.LENGTH_LONG).show();

                        /*
                        // menyimpan login ke session
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_USERNAME, username);
                        editor.putString(TAG_BUSINESS_NAME, business_name);
                        editor.apply();
                        */

                        // Dismissing pDialog
                        pDialog.dismiss();

                        /*
                        // Memanggil main activity
                        Log.d("Username", username);
                        intent = new Intent(HomeRegisterActivity.this, HomeLoginActivity.class);
                        intent.putExtra(TAG_USERNAME, username);
                        startActivity(intent);
                        */

                        finish();
                    } else if (database_available == 0 && user_available == 1) {
                        Toast.makeText(getApplicationContext(),
                                "Username sudah tersedia, silakan pilih nama lain", Toast.LENGTH_LONG).show();

                    } else if (database_available == 1 && user_available == 0) {
                        Toast.makeText(getApplicationContext(),
                                "Nama Bisnis sudah tersedia, silakan pilih nama lain", Toast.LENGTH_LONG).show();

                    } else if (database_available == 1 && user_available == 1) {
                        Toast.makeText(getApplicationContext(),
                                "Username dan Nama Bisnis sudah tersedia, silakan pilih nama lain", Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                //hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to resgiter url
                Map<String, String> params = new HashMap<String, String>();
                params.put("nama_bisnis", business_name);
                params.put("pemilik_bisnis", owner_name);
                params.put("username", username);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
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