package com.project.lb3labs.ecashier;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

//import net.objecthunter.exp4j.Expression;
//import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    //private WebView mWebView;
    //private ArrayAdapter<String> adapter;
    //private ArrayList<String> arrayList;

    // TextView used to display the output
    @SuppressLint("StaticFieldLeak")
    private static TextView guide, txtScreen;
    private TextView laporan;

    private EditText editTxt, editTxt2;
    private LinearLayout payLayout;
    private ListView list;
    private ShoppingAdapter shopAdapter;

    //private Context mContext;
    //private List<Shops> shoppingList;

    public static int total;
    private Button add, pay, cancel;
    private ConnectivityManager conMgr;

    // Set the server
    private final String url = Server.URL + "list_product.php";
    private final String url_2 = Server.URL + "insert_shopping.php";
    private final String url_3 = Server.URL + "select_last.php";
    private final String url_print = Server.URL + "print_sales.php";
    private final String url_laporan = "http://192.168.43.35/ecashier/";

    // Define setProduct class
    private setProduct setProduct;

    private int success;
    private int priceOne;
    private int price;

    private Intent intent;

    private String nama;
    private String kodebarang;
    private String message;
    private String username;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_NAMA = "nama";
    private static final String TAG_HARGA = "harga";
    private final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";
    private ProgressDialog pDialog;
    private String value;

    /*
    // IDs of all the numeric buttons
    private int[] numericButtons = {R.id.btnZero, R.id.btnOne, R.id.btnTwo, R.id.btnThree, R.id.btnFour, R.id.btnFive, R.id.btnSix, R.id.btnSeven, R.id.btnEight, R.id.btnNine};

    // IDs of all the operator buttons
    private int[] operatorButtons = {R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide};

    // Represent whether the lastly pressed key is numeric or not
    private boolean lastNumeric;

    // Represent that current state is in error or not
    private boolean stateError;

    // If true, do not allow to add another DOT
    private boolean lastDot;
    */

    public static void getTotal(int totalMin) {
        total = total - totalMin;
        txtScreen.setText(currencyFormat(total));
        //txtScreen.setText("Rp"+String.valueOf(total)+",00");
        Log.i("Total semua", String.valueOf(total));

        if(total==0){
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set total to 0
        total = 0;

        // SETTING THE TOOLBAR
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarCustom);
        setSupportActionBar(toolbar);

        //TextView textView = toolbar.findViewById(R.id.toolbarTextView);
        //textView.setText("String");

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Display prices
        txtScreen = findViewById(R.id.txtScreen);
        txtScreen.setText(R.string.nominal_awal);


        /*
        // CALCULATOR
        // Find the TextView
        txtScreen = findViewById(R.id.txtScreen);
        txtScreen.setText("Hai admin!");
        // Find and set OnClickListener to numeric buttons
        //setNumericOnClickListener();
        // Find and set OnClickListener to operator buttons, equal button and decimal point button
        setOperatorOnClickListener();
        */


        // LISTVIEW BERHASIL
        editTxt = findViewById(R.id.kodeBarang);
        editTxt2 = findViewById(R.id.jumlahBarang);
        add = findViewById(R.id.addBtn);
        pay = findViewById(R.id.payButton);
        cancel = findViewById(R.id.cancelButton);
        guide = findViewById(R.id.guide);
        payLayout = findViewById(R.id.pay_box);
        //laporan = findViewById(R.id.toolbarTextView2);

        intent = getIntent();
        username = intent.getStringExtra("username");

        ImageButton btnscan = findViewById(R.id.buttonScan);

        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanow();
            }
        });
        list = findViewById(R.id.listView);

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

        // Array List yang lama
        //arrayList = new ArrayList<>();

        final ArrayList<Shops> shoppingList = new ArrayList<>();
        shopAdapter = new ShoppingAdapter(this, shoppingList);
        Log.d("ListString:", shoppingList.toString());

        // Here, you set the data in your ListView
        list.setAdapter(shopAdapter);

        // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
        // and the array that contains the data
        //adapter = new ArrayAdapter<String>(this,R.layout.listview_display, R.id.aku, arrayList);

        // Here, you set the data in your ListView
        //list.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage("Please wait...");
                showDialog();
                /*
                // this line adds the data of your EditText and puts in your array
                arrayList.add(editTxt.getText().toString());
                adapter.add(editTxt.getText().toString());
                Log.d("Testing", arrayList.toString());

                // next thing you have to do is check if your adapter has changed
                adapter.notifyDataSetChanged();
                */

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

                conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                assert conMgr != null;
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {

                    // Set guide view to INVISIBLE, keyboard
                    guide.setVisibility(View.INVISIBLE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
                                    hideDialog();

                                    // Setting constructors
                                    setProduct = new setProduct(jObj.getString(TAG_NAMA), jObj.getInt(TAG_HARGA), Integer.valueOf(kodebarang));

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
                                    String unit = "";
                                    Log.d("TOTAL HARGA SATU PRODUK", String.valueOf(priceOne));

                                    // Showing results
                                    txtScreen.setText(currencyFormat(total));
                                    //txtScreen.setText("Rp" + String.valueOf(total) + ",00");
                                    shoppingList.add(new Shops(nama, unit, Double.parseDouble(value), priceOne, price, Integer.valueOf(kodebarang)));

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
                            Toast.makeText(getApplicationContext(),
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
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (shopAdapter.getCount() == 0) {
                    Snackbar.make(view, "Mohon melakukan transaksi terlebih dahulu", Snackbar.LENGTH_SHORT).show();
                } else {
                    //Snackbar.make(view,"Pay Function",Snackbar.LENGTH_SHORT).show();

                    final AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(MainActivity.this);
                    alertdialogbuilder.setMessage("Lanjutkan pembayaran?");
                    alertdialogbuilder.setTitle("Confirmation");
                    alertdialogbuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            paymentOptions();
                        }
                    });
                    alertdialogbuilder.setNegativeButton("Tidak, tunggu sebentar", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            closeDialog();
                        }

                        private void closeDialog() {
                            AlertDialog alertDialog = alertdialogbuilder.create();
                            alertDialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertdialogbuilder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            }

            private void paymentOptions() {
                final AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(MainActivity.this);
                //alertdialogbuilder.setMessage("Silakan pilih metode pembayaran di bawah ini");
                alertdialogbuilder.setTitle("Metode Pembayaran");

                // OPTION TUNAI
                alertdialogbuilder.setPositiveButton("Tunai", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.w("USERNAME", username);

                        // Setting edittext
                        final EditText input = new EditText(MainActivity.this);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        input.setLayoutParams(lp);

                        final AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(MainActivity.this);
                        //alertdialogbuilder.setMessage("Silakan pilih metode pembayaran di bawah ini");
                        alertdialogbuilder.setTitle("Bayar");
                        alertdialogbuilder.setView(input);
                        alertdialogbuilder.setMessage("Total : " + total);
                        alertdialogbuilder.setPositiveButton("PROSES", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (input.getText().toString() != null) {
                                            Log.d("INTEGER", String.valueOf(Integer.parseInt(input.getText().toString()) >= total));
                                            if (!(Integer.parseInt(input.getText().toString()) >= total)) {
                                                Toast.makeText(MainActivity.this, "Jumlah minimal tidak terpenuhi. Silakan coba lagi", Toast.LENGTH_LONG).show();
                                            } else {
                                                // Dismissing alertDialog
                                                AlertDialog alertDialog = alertdialogbuilder.create();
                                                alertDialog.dismiss();

                                                String[] a = new String[shopAdapter.getCount()];
                                                for (int i = 0; i < a.length; i++) {
                                                    final int finalI = i;
                                                    Log.d("FINALI", String.valueOf(finalI));

                                                    StringRequest strReq = new StringRequest(Request.Method.POST, url_2, new Response.Listener<String>() {

                                                        @Override
                                                        public void onResponse(String response) {

                                                            // Check response
                                                            /*
                                                            Log.e(TAG, "Checking Response: " + response);
                                                            try {

                                                                success = jObj.getInt(TAG_SUCCESS);
                                                                Log.e("HASIL", String.valueOf(message));
                                                                Log.e("HASIL(2)", String.valueOf(success));

                                                                if (finalI == shopAdapter.getCount() - 1) {
                                                                    printFunction(success, username);
                                                                }
                                                            } catch (JSONException e) {
                                                                // JSON error
                                                                e.printStackTrace();
                                                            }
                                                            */

                                                    /*
                                                    Log.w("Mari berhitung", String.valueOf(shopAdapter.getCount()));
                                                    Log.w("LISTINGku (NULL)", String.valueOf(Objects.requireNonNull(Objects.requireNonNull(shopAdapter.getItem(finalI)).getProductId())));
                                                    Log.w("LISTINGku (NULL)", String.valueOf(Objects.requireNonNull(Objects.requireNonNull(shopAdapter.getItem(finalI)).getPriceOne())));
                                                    Log.w("LISTINGku (NULL)", String.valueOf(Objects.requireNonNull(Objects.requireNonNull(shopAdapter.getItem(finalI)).getQty())));
                                                    Log.w("LISTINGku (NULL)", String.valueOf(Objects.requireNonNull(Objects.requireNonNull(shopAdapter.getItem(finalI)).getTitle())));
                                                    Log.w("LISTINGku", String.valueOf(Objects.requireNonNull(Objects.requireNonNull(shopAdapter.getItem(finalI)))));
                                                    */

                                                            Log.d("count", String.valueOf(finalI));
                                                            Log.d("product_id", String.valueOf(Objects.requireNonNull(shopAdapter.getItem(finalI)).getProductId()));
                                                            Log.d("username", username);
                                                            Log.d("totalAll", String.valueOf(total));
                                                            Log.d("total", String.valueOf(Objects.requireNonNull(shopAdapter.getItem(finalI)).getTotalPriceOne()));
                                                            Log.d("sale_price", String.valueOf(Objects.requireNonNull(shopAdapter.getItem(finalI)).getPriceOne()));
                                                            Log.d("type", "1");
                                                            Log.d("qty", String.valueOf(Objects.requireNonNull(shopAdapter.getItem(finalI)).getQty()));
                                                            Log.d("pay", input.getText().toString());
                                                        }
                                                    }, new Response.ErrorListener() {

                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            Log.e(TAG, "Error: " + error.getMessage());
                                                            Toast.makeText(getApplicationContext(),
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
                                                            params.put("type", "1");
                                                            params.put("qty", String.valueOf(Objects.requireNonNull(shopAdapter.getItem(finalI)).getQty()));
                                                            params.put("pay", input.getText().toString());

                                                            return params;
                                                        }
                                                    };

                                                    // Adding request to request queue
                                                    AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
                                                }

                                                StringRequest strReq = new StringRequest(Request.Method.POST, url_3, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    JSONObject jObj = null;
                                                    try {
                                                        jObj = new JSONObject(response);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    try {
                                                        assert jObj != null;
                                                        success = jObj.getInt(TAG_SUCCESS);
                                                        printFunction(success, username);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Log.e(TAG, "Error: " + error.getMessage());
                                                        Toast.makeText(getApplicationContext(),
                                                                "404", Toast.LENGTH_LONG).show();
                                                        //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                                                        hideDialog();
                                                    }
                                                }) {

                                                    @Override
                                                    protected Map<String, String> getParams() {
                                                        // Posting parameters to login url
                                                        Map<String, String> params = new HashMap<>();
                                                        params.put("username", username);
                                                        params.put("totalAll", String.valueOf(total));

                                                        return params;
                                                    }
                                                };

                                                // Adding request to request queue
                                                AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
                                            }
                                        }
                                    }
                                });

                        // Option for Cancellation
                        alertdialogbuilder.setNegativeButton("Batalkan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                closeDialog();
                            }

                            private void closeDialog() {
                                AlertDialog alertDialog = alertdialogbuilder.create();
                                alertDialog.dismiss();
                            }
                        });

                        AlertDialog alertDialog = alertdialogbuilder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    }
                });

                // OPTION KARTU KREDIT
                alertdialogbuilder.setNegativeButton("Kartu Kredit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeDialog();
                    }

                    private void closeDialog() {
                        AlertDialog alertDialog = alertdialogbuilder.create();
                        alertDialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertdialogbuilder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();


                // WEBVIEW
                /*
                WebView webView = (WebView) findViewById(R.id.activity_main_webview);
                webView.getSettings().setLoadsImagesAutomatically(true);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setDomStorageEnabled(true);

                // Tiga baris di bawah ini agar laman yang dimuat dapat
                // melakukan zoom.
                webView.getSettings().setSupportZoom(true);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setDisplayZoomControls(false);

                // Baris di bawah untuk menambahkan scrollbar di dalam WebView-nya
                webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                webView.setInitialScale(200);
                webView.setWebViewClient(new WebViewClient());
                webView.loadUrl("http://192.168.43.35/toko_buah/index_kasir.php");
                */
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

        laporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage("Loading ...");
                showDialog();

                Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
                intent.putExtra("link", url_laporan);
                intent.putExtra("print",0);
                startActivity(intent);

                hideDialog();

                // Make Snackbar
                Snackbar.make(view, "Laporan Function", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void doPrint() {
        /*
        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) getApplication()
                .getSystemService(Context.PRINT_SERVICE);

        // Set job name, which will be displayed in the print queue
        String jobName = getApplication().getString(R.string.app_name) + " Document";

        // Start a print job, passing in a PrintDocumentAdapter implementation
        // to handle the generation of a print document
        printManager.print(jobName, new MyPrintDocumentAdapter(getApplication()),
                null); //
        */
    }

    private void printFunction(final int id_trans, final String username) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Printing ...");
        showDialog();

        Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
        intent.putExtra("last_id", id_trans);
        intent.putExtra("username", username);
        intent.putExtra("link", url_print);
        intent.putExtra("print",1);
        shopAdapter.clear();
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null){
            if (result.getContents() == null){
                Toast.makeText(this,"Result Not Found", Toast.LENGTH_SHORT).show();
            } else {
                editTxt.setText(result.getContents());
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void scanow(){
        com.google.zxing.integration.android.IntentIntegrator integrator = new com.google.zxing.integration.android.IntentIntegrator(this);
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


    /*
     * Find and set OnClickListener to numeric buttons.
     *
    private void setNumericOnClickListener() {
        // Create a common OnClickListener
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Just append/set the text of clicked button
                Button button = (Button) v;
                if (stateError) {
                    // If current state is Error, replace the error message
                    txtScreen.setText(button.getText());
                    stateError = false;
                } else {
                    // If not, already there is a valid expression so append to it
                    txtScreen.append(button.getText());
                }
                // Set the flag
                lastNumeric = true;
            }
        };
        // Assign the listener to all the numeric buttons
        for (int id : numericButtons) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    /**
     * Find and set OnClickListener to operator buttons, equal button and decimal point button.
     *
    private void setOperatorOnClickListener() {
        // Create a common OnClickListener for operators
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the current state is Error do not append the operator
                // If the last input is number only, append the operator
                if (lastNumeric && !stateError) {
                    Button button = (Button) v;
                    txtScreen.append(button.getText());
                    lastNumeric = false;
                    lastDot = false;    // Reset the DOT flag
                }
            }
        };
        // Assign the listener to all the operator buttons
        for (int id : operatorButtons) {
            findViewById(id).setOnClickListener(listener);
        }
        // Decimal point
        findViewById(R.id.btnDot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastNumeric && !stateError && !lastDot) {
                    txtScreen.append(".");
                    lastNumeric = false;
                    lastDot = true;
                }
            }
        });
        // Clear button
        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtScreen.setText("");  // Clear the screen
                // Reset all the states and flags
                lastNumeric = false;
                stateError = false;
                lastDot = false;
            }
        });
        /*
        // Equal button
        findViewById(R.id.btnEqual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEqual();
            }
        });
    }

    /**
     * Logic to calculate the solution.
     *
    private void onEqual() {
        // If the current state is error, nothing to do.
        // If the last input is a number only, solution can be found.
        if (lastNumeric && !stateError) {
            // Read the expression
            String txt = txtScreen.getText().toString();
            // Create an Expression (A class from exp4j library)
            Expression expression = new ExpressionBuilder(txt).build();
            try {
                // Calculate the result and display
                double result = expression.evaluate();
                txtScreen.setText(Double.toString(result));
                lastDot = true; // Result contains a dot
            } catch (ArithmeticException ex) {
                // Display an error message
                txtScreen.setText("Error");
                stateError = true;
                lastNumeric = false;
            }
        }
    }
     */
}
