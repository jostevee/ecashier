package com.project.lb3labs.ecashier;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

//import net.objecthunter.exp4j.Expression;
//import net.objecthunter.exp4j.ExpressionBuilder;

public class NewMainActivity extends Fragment implements AdapterView.OnItemSelectedListener{
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    private final String TAG = "NewMainActivity";

    // TextView used to display the output
    @SuppressLint("StaticFieldLeak")
    private static TextView guide, txtScreen, change;
    private static EditText editTxt4;
    private TextView laporan;

    private EditText editTxt, editTxt2;
    private ListView list;
    private ShoppingAdapter shopAdapter;

    public static int total = 0;
    private Button add, pay, cancel;
    private ConnectivityManager conMgr;

    // Set the server
    private final String database_text = "?database=";
    private final String url = Server.URL + "list_product.php" + database_text;
    private final String url_2 = Server.URL + "insert_shopping.php" + database_text;
    private final String url_3 = Server.URL + "select_last.php" + database_text;
    private final String url_print = Server.URL + "print_sales.php" + database_text;
    private final String url_laporan = "http://192.168.43.35/ecashier/";

    // Define setProduct class
    private setProduct setProduct;
    private setChange setChange;

    private int success, priceOne, price;
    private int change_nom = 0;

    private Intent intent;

    private String nama, unit, kodebarang, message, username;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_NAMA = "nama";
    private static final String TAG_HARGA = "harga";
    private static final String TAG_UNIT = "unit";
    private final String TAG_MESSAGE = "message";
    private static final String TAG_LAST_ID = "last_id";

    String tag_json_obj = "json_obj_req";
    private ProgressDialog pDialog;
    private String value;
    private static String text = "0"; // untuk menentukan kembalian
    private int last_id;
    private String typePayment;

    public static void getTotal(int totalMin) {
        total = total - totalMin;
        txtScreen.setText(currencyFormat(total));
        //txtScreen.setText("Rp"+String.valueOf(total)+",00");
        Log.i("Total semua", String.valueOf(total));

        change.setText(R.string.nominal_awal);
        editTxt4.setText(text);

        if(total == 0){
            guide.setVisibility(View.VISIBLE);
        }
    }

    public static String currencyFormat(int amount) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
        format.setMaximumFractionDigits(2);
        format.setCurrency(Currency.getInstance("IDR"));

        return format.format(amount)+",00";
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.activity_main, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        // SETTING THE TOOLBAR
        //android.support.v7.widget.Toolbar toolbar = view.findViewById(R.id.toolbarCustom);
        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //TextView textView = toolbar.findViewById(R.id.toolbarTextView);
        //textView.setText("String");
        //Objects.requireNonNull(((AppCompatActivity)getActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(true);

        // Display prices
        txtScreen = view.findViewById(R.id.txtScreen);
        txtScreen.setText(R.string.nominal_awal);

        // EditText
        editTxt = view.findViewById(R.id.kodeBarang);
        editTxt2 = view.findViewById(R.id.jumlahBarang);
        editTxt4 = view.findViewById(R.id.jumlahBayar);
        change = view.findViewById(R.id.jumlahKembalian);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Log.d("HAI","HAI");

    //        ((EditText) findViewById(R.id.et_find)).requestFocus();
    //
    //        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    //        imm.showSoftInput(yourEditText, InputMethodManager.SHOW_IMPLICIT);

                // For focused edittext
                //editTxt4.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
                //editTxt4.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));
            }
        }, 200);

        editTxt4.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable mEdit)
            {
                if(!mEdit.toString().equals("") && !mEdit.toString().equals("0")){
                    text = mEdit.toString();
                    change_nom = Integer.parseInt(text) - total;

                    // Display changes
                    change.setText(currencyFormat(change_nom));
                } else if (mEdit.toString().equals("0")) {
                    change.setText(currencyFormat(0));

                    /*
                    text = "0";
                    change_nom = Integer.parseInt(text) - total;
                    Log.e("texttt", text);
                    editTxt4.setText(text);
                    */
                } else {
                    change_nom = 0;
                    change.setText(currencyFormat(change_nom));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        add = view.findViewById(R.id.addBtn);
        pay = view.findViewById(R.id.payButton);
        cancel = view.findViewById(R.id.cancelButton);

        guide = view.findViewById(R.id.guide);

        //laporan = view.findViewById(R.id.toolbarTextView2);

        intent = requireActivity().getIntent();
        username = intent.getStringExtra("username");

        ImageButton btnscan = view.findViewById(R.id.buttonScan);
        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanow();
            }
        });
        list = view.findViewById(R.id.listView);


        // Spinner element
        Spinner spinner = view.findViewById(R.id.spinner_tipe);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add("Tunai");
        categories.add("Kartu Kredit");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        /* For VISIBILITY
        editTxt.setVisibility(View.INVISIBLE);
        editTxt2.setVisibility(View.INVISIBLE);
        add.setVisibility(View.INVISIBLE);
        pay.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        guide.setVisibility(View.INVISIBLE);
        payLayout.setVisibility(View.INVISIBLE);
        list.setVisibility(View.INVISIBLE);
        txtScreen.setVisibility(View.INVISIBLE);
        */

        final ArrayList<Shops> shoppingList = new ArrayList<>();
        shopAdapter = new ShoppingAdapter(requireActivity(), shoppingList);
        Log.d("ListString:", shoppingList.toString());

        // Here, you set the data in your ListView
        list.setAdapter(shopAdapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                pDialog = new ProgressDialog(getActivity());
                pDialog.setCancelable(false);
                pDialog.setMessage("Please wait...");
                showDialog();

                kodebarang = editTxt.getText().toString();
                String jumlahbarang = editTxt2.getText().toString();
                if (TextUtils.isEmpty(kodebarang)) {
                    hideDialog();
                    Snackbar.make(view, "Mohon di isi terlebih dahulu field kode barang", Snackbar.LENGTH_LONG).show();
                    editTxt.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(jumlahbarang)) {
                    hideDialog();
                    Snackbar.make(view, "Mohon di isi terlebih dahulu field jumlah barang", Snackbar.LENGTH_LONG).show();
                    editTxt2.requestFocus();
                    return;
                }

                conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                assert conMgr != null;
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {

                    // Set guide view to INVISIBLE, keyboard
                    guide.setVisibility(View.INVISIBLE);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);

                    // Run function
                    Log.d("KODE", kodebarang);
                    StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e(TAG, "Checking Response: " + response);

                            try {
                                JSONObject jObj = new JSONObject(response);
                                success = jObj.getInt(TAG_SUCCESS);

                                // Check for error node in json
                                if (success == 1) {
                                    nama = jObj.getString(TAG_NAMA);
                                    unit = jObj.getString(TAG_UNIT);
                                    Integer kodebarangnum = Integer.valueOf(kodebarang);
                                    hideDialog();

                                    // Setting constructors
                                    setProduct = new setProduct(nama, jObj.getInt(TAG_HARGA), kodebarangnum);

                                    // Gettting constructors
                                    priceOne = setProduct.priceOne;

                                    // get quantity
                                    value = editTxt2.getText().toString();
                                    Log.d("VALUE", value);

                                    // Change total price of one product to (double)
                                    Double doublePrice = Double.parseDouble(value) * priceOne;
                                    price = doublePrice.intValue();

                                    // Calculate
                                    total = total + price;
                                    Log.d("TOTAL HARGA SATU PRODUK", String.valueOf(priceOne));

                                    // Showing results
                                    txtScreen.setText(currencyFormat(total));
                                    //txtScreen.setText("Rp" + String.valueOf(total) + ",00");

                                    // Add to list
                                    shoppingList.add(new Shops(nama, unit, Double.parseDouble(value), priceOne, price, kodebarangnum));

                                    // Update everything and tidy up
                                    editTxt.getText().clear();
                                    editTxt2.getText().clear();
                                    shopAdapter.notifyDataSetChanged();

                                    //Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                                } else {
                                    guide.setVisibility(View.VISIBLE);
                                    hideDialog();
                                    message = jObj.getString(TAG_MESSAGE);
                                    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();

                                    //Toast.makeText(getApplicationContext(), jObj.getString(TAG_SUCCESS), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                // JSON error
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Error: " + error.getMessage());
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "404", Toast.LENGTH_LONG).show();
                            //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                            hideDialog();
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            // Posting parameters to login url
                            Map<String, String> params = new HashMap<>();
                            params.put("kodebarang", kodebarang);

                            return params;
                        }
                    };

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
                } else {
                    hideDialog();
                    Toast.makeText(getActivity().getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (shopAdapter.getCount() == 0) {
                    Snackbar.make(view, "Mohon melakukan transaksi terlebih dahulu", Snackbar.LENGTH_SHORT).show();
                } else {
                    pDialog = new ProgressDialog(getActivity());
                    pDialog.setCancelable(false);
                    pDialog.setMessage("Processing ...");
                    showDialog();
                    paymentOptions();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                total = 0;
                Snackbar.make(view, "Membatalkan transaksi terakhir", Snackbar.LENGTH_LONG).show();
                shopAdapter.clear();

                // Showing results
                txtScreen.setText(currencyFormat(total));
                //txtScreen.setText("Rp" + String.valueOf(total) + ",00");
                guide.setVisibility(View.VISIBLE);
            }
        });

        /*
        laporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BrowserActivity.class);
                intent.putExtra("link", url_laporan);
                intent.putExtra("print",0);
                startActivity(intent);

                // Make Snackbar
                Snackbar.make(view, "Laporan Function", Snackbar.LENGTH_LONG).show();
            }
        });
        */
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

        if(item.equals("Tunai")){
            typePayment = "1";
        } else {
            typePayment = "2";
        }
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void paymentOptions() {
        String[] a = new String[shopAdapter.getCount()];
        for (int i = 0; i < a.length; i++) {
            final int finalI = i;
            Log.d("FINALI", String.valueOf(finalI));

            StringRequest strReqs = new StringRequest(Request.Method.POST, url_2, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    /*
                    try {
                        JSONObject jObj = new JSONObject(response);
                        last_id = jObj.getInt(TAG_LAST_ID);
                        message = jObj.getString(TAG_MESSAGE);
                        Log.w("LAST ID", String.valueOf(last_id));
                        Log.d("count", String.valueOf(finalI));
                        Log.d("product_id", String.valueOf(Objects.requireNonNull(shopAdapter.getItem(finalI)).getProductId()));
                        Log.d("username", username);
                        Log.d("totalAll", String.valueOf(total));
                        Log.d("total", String.valueOf(Objects.requireNonNull(shopAdapter.getItem(finalI)).getTotalPriceOne()));
                        Log.d("sale_price", String.valueOf(Objects.requireNonNull(shopAdapter.getItem(finalI)).getPriceOne()));
                        Log.d("type", "1");
                        Log.d("qty", String.valueOf(Objects.requireNonNull(shopAdapter.getItem(finalI)).getQty()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                            Log.w("Mari berhitung", String.valueOf(shopAdapter.getCount()));
                            Log.w("LISTINGku (NULL)", String.valueOf(Objects.requireNonNull(Objects.requireNonNull(shopAdapter.getItem(finalI)).getProductId())));
                            Log.w("LISTINGku (NULL)", String.valueOf(Objects.requireNonNull(Objects.requireNonNull(shopAdapter.getItem(finalI)).getPriceOne())));
                            Log.w("LISTINGku (NULL)", String.valueOf(Objects.requireNonNull(Objects.requireNonNull(shopAdapter.getItem(finalI)).getQty())));
                            Log.w("LISTINGku (NULL)", String.valueOf(Objects.requireNonNull(Objects.requireNonNull(shopAdapter.getItem(finalI)).getTitle())));
                            Log.w("LISTINGku", String.valueOf(Objects.requireNonNull(Objects.requireNonNull(shopAdapter.getItem(finalI)))));
                            */

                    //Log.d("pay", spinner.getText().toString());
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error: " + error.getMessage());
                    Toast.makeText(requireActivity().getApplicationContext(),
                            "404", Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                    hideDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<>();
                    params.put("count", String.valueOf(finalI));
                    params.put("product_id", String.valueOf(Objects.requireNonNull(shopAdapter.getItem(finalI)).getProductId()));
                    params.put("username", username);
                    params.put("totalAll", String.valueOf(total));
                    params.put("total", String.valueOf(Objects.requireNonNull(shopAdapter.getItem(finalI)).getTotalPriceOne()));
                    params.put("salePrice", String.valueOf(Objects.requireNonNull(shopAdapter.getItem(finalI)).getPriceOne()));
                    params.put("pay", editTxt4.getText().toString());
                    params.put("type", typePayment);
                    params.put("qty", String.valueOf(Objects.requireNonNull(shopAdapter.getItem(finalI)).getQty()));
                    //params.put("pay", input.getText().toString());

                    return params;
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReqs, tag_json_obj);

            if (finalI == a.length - 1){
                StringRequest strReq = new StringRequest(Request.Method.POST, url_3, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("CARI", response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            last_id = jObj.getInt(TAG_LAST_ID);
                            printFunction(last_id, username);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                        Toast.makeText(requireActivity().getApplicationContext(),
                                "404", Toast.LENGTH_LONG).show();

                        hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);

                        return params;
                    }
                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
            }
        }
    }

    private void printFunction(int lastid, final String username) {
        total = 0;
        Intent intent = new Intent(getActivity(), BrowserActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("link", url_print);
        intent.putExtra("last_id", lastid);
        intent.putExtra("print",1);

        // Cleearing everything
        change.setText(currencyFormat(0));
        editTxt4.setText("0");
        shopAdapter.clear();
        hideDialog();

        startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null){
            if (result.getContents() == null){
                Toast.makeText(getActivity(),"Result Not Found", Toast.LENGTH_SHORT).show();
            } else {
                editTxt.setText(result.getContents());
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void scanow(){
        com.google.zxing.integration.android.IntentIntegrator integrator = new com.google.zxing.integration.android.IntentIntegrator(getActivity());
        integrator.setCaptureActivity(Portrait.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.TARGET_BARCODE_SCANNER_ONLY);
        integrator.setCameraId(-1);
        integrator.setPrompt("Scan Your Barcode");
        integrator.initiateScan();
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
