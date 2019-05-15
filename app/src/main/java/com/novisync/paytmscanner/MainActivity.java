package com.novisync.paytmscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    private IntentIntegrator qrscan;
    String scannedData;
    TextView textView;
    Button scanBtn, b1;
    String list,begin,version,name,org,title,email,cell,url;

    JSONObject jsonObject = null;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanBtn = (Button) findViewById(R.id.scan_btn);
        textView = (TextView) findViewById(R.id.textView);
        b1 = (Button) findViewById(R.id.bt);

        //intializing scan object
        qrscan = new IntentIntegrator(this);
        Intent i = getIntent();

        //final String =textView.getText().toString();

        //attaching onclick listener
        scanBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                // initiating the qr code scan

                qrscan.initiateScan();
                String b = list;
                Log.d(TAG, "index list" + b);
                final Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                Intent intent = new Intent(getApplicationContext(), AddRequest.class);
                                getApplicationContext().startActivity(intent);
                                Toast.makeText(MainActivity.this, "Hi", Toast.LENGTH_LONG).show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Register Failed").setNegativeButton("Retry", null).create().show();
                                Log.d(TAG, "builder");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                };
                AddRequest addRequest = new AddRequest(begin,version,name,org,title,email,cell,url, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(addRequest);
            }
        });
    }


    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String MobilePattern = "[0-9]{10}";
       String urlPattern=  "(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?";
       //String orgPattern="  ([A-Z])\\w+Inc";
        if (result != null) {
            //if qrcode has nothing in it
            //if qrcode has nothing in itc
            if (result.getContents() == null) {
                Toast.makeText(getApplicationContext(), "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to jsons

                    JSONObject obj = new JSONObject(result.getContents());

                    //setting values to textviews
                    textView.setText(obj.getString("QR SCAN"));
                    scanBtn.setText(obj.getString("SCAN"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast

                }
                Toast.makeText(getApplicationContext(), result.getContents(), Toast.LENGTH_LONG).show();
                list = result.getContents();
               String list1=list.toString();

               Log.d("TAG", "onActivityResult: " + list1);
               // textView.setText(list);
                SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
                SharedPreferences.Editor editor=sharedPreferences.edit();
             //   String[] split=list1.split("\\s+");
                String[] split=list1.split("\r\n");

                String begin=split[0];
                begin=begin.substring(begin.indexOf("begin:") +7,begin.length());
                Log.d(TAG, "onActivityResult999: "+begin);
                editor.putString( "begin",begin);
                String version=split[1];
                version=version.substring(version.indexOf("version:") +9,version.length());
                Log.d(TAG, "onActivityResult999: "+version);
                editor.putString( "version",version);
                String name=split[2];
                name=name.substring(name.indexOf("n:") +4,name.length());
                Log.d(TAG, "onActivityResult999:name " + name);
                editor.putString("name", name);

                String org=split[3];
                org=org.substring(org.indexOf("org:") +5,org.length());

//                String org1=org.substring(org.indexOf("Novisync ") +9,org.length());
//                Log.d(TAG, "onActivityResult: org1"  + org1);
                editor.putString("org", org);
//                if(org1=="Inc") {
//                    //Log.d(TAG, "onActivityResult999:org" + org1);
//                    editor.putString("org", org);
//                }else{
//                    Toast.makeText(getApplicationContext(), "Please Enter Valid org ", Toast.LENGTH_SHORT).show();
//                }
                String title=split[4];
                title=title.substring(title.indexOf("title:") +7,title.length());
                Log.d(TAG, "onActivityResult999: "+title);
                editor.putString( "title",title);
                String email = split[5];
                email = email.substring(email.indexOf("email:") + 7, email.length());
                if (email.toString().matches(emailPattern)) {

                    //Toast.makeText(getApplicationContext(),"valid email address",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onActivityResult999:amith " + email);
                    editor.putString("email", email);

                } else{

                    Toast.makeText(getApplicationContext(), "Please Enter Valid Email Address", Toast.LENGTH_SHORT).show();
                }


                String cell=split[6];
                cell=cell.substring(cell.indexOf("cell:") +19,cell.length());
                if(cell.toString().matches(MobilePattern)) {
                    Log.d(TAG, "onActivityResult999:phone " + cell);
                    editor.putString("cell", cell);
                }else{
                    Toast.makeText(getApplicationContext(), "Please Enter Valid cell number ", Toast.LENGTH_SHORT).show();
                }
                String url=split[7];
                url=url.substring(url.indexOf("url:") +5,url.length());
                if(url.toString().matches(urlPattern)) {
                    Log.d(TAG, "onActivityResult999:url " + url);
                    editor.putString("url", url);
                }else{
                    Toast.makeText(getApplicationContext(), "Please Enter valid url pattern ", Toast.LENGTH_SHORT).show();
                }
                editor.commit();


//                System.out.println("split String"+split);
//
//                String[] split1=list.split(":");
//                split1.toString();
//                Log.d(TAG, "onActivityResult1: "+split1);

            }
            //convert xml to string
//            try {
//                jsonObject = XML.toJSONObject(list);
//               textView.setText(jsonObject.toString());
//                textView.setText(list);
//               String s=textView.toString();
//               Log.d(TAG, "onActivity details:" +s);
//                String[] split=s.split(":");
//                Log.d(TAG, "onActivityResult json obj:" +split);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//
//            }

                super.onActivityResult(requestCode, resultCode, data);

        }


            b1.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {
                    final String b = list;

                    String list1=list.toString();
                    SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
                    String begin=  sharedPreferences.getString( "begin","" );
                    String version=  sharedPreferences.getString( "version","" );
                    String name=  sharedPreferences.getString( "name","" );
                    String org=  sharedPreferences.getString( "org","" );
                    String title=  sharedPreferences.getString( "title","" );
                    String email=sharedPreferences.getString( "email","" );
                    String cell=sharedPreferences.getString( "cell","" );
                    String url=sharedPreferences.getString( "url","" );

                    String [] str={begin,version,name,org,title,email,cell,url};
                    MyAsyncTask back=new MyAsyncTask();
                    back.execute( str );

//                    Log.d("TAG", "onActivityResult: " + list1);
//                     textView.setText(list);
//
//                    String[] split=list1.split(":");

                    Log.d(TAG, "index visiting card details" + b);
//                    String[] split=b.split(":");
//                    split.toString();
//                    Log.d(TAG, "onActivityResult11: "+split);
//                    String[] split=b.split(":,//s");
//                    String[][] outputContainsThis = new String[][]{split};
//                    Log.d("TAG", "onActivityResult123: " + outputContainsThis);

                    final Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    Intent intent = new Intent(MainActivity.this, AddClass.class);
                                    MainActivity.this.startActivity(intent);
                                    Toast.makeText(MainActivity.this, "Hi", Toast.LENGTH_LONG).show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setMessage("Register Failed").setNegativeButton("Retry", null).create().show();
                                    Log.d(TAG, "builder");

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    };
                    AddRequest addRequest = new AddRequest(begin,version,name,org,title,email,cell,url, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(addRequest);
                }
            });

        }
    }

class MyAsyncTask extends AsyncTask<String, Void, Void> {

    @Override

    protected Void doInBackground(String...params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://rajyamelec99.000webhostapp.com/paytm.php");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(0);
        nameValuePairs.add(new BasicNameValuePair("begin",params[0]));
        nameValuePairs.add(new BasicNameValuePair("version",params[1]));
        nameValuePairs.add(new BasicNameValuePair("name",params[2]));
        nameValuePairs.add(new BasicNameValuePair("org",params[3]));
        nameValuePairs.add(new BasicNameValuePair("title",params[4]));
        nameValuePairs.add(new BasicNameValuePair("email",params[5]));
        nameValuePairs.add(new BasicNameValuePair("cell",params[6]));
        nameValuePairs.add(new BasicNameValuePair("url",params[7]));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String response=null;
        try { Log.d( "TAG","doInBackground: qweer" );
            response = httpclient.execute(httppost, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d( "TAG","doInBackground: "+response );
        return null;

    }


}
// EO class
