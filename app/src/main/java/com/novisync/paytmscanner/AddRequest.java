package com.novisync.paytmscanner;

import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AddRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "https://rajyamelec99.000webhostapp.com/paytm.php";
   // private static final String REGISTER_REQUEST_URL = "http://192.168.0.112/Registerprovider.php";
    private Map<String, String> sample;

    public AddRequest(String begin, String version, String name, String org, String title, String email, String cell, String url, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        sample = new HashMap<>();
        sample.put("begin",begin);
        sample.put("version",version);
        sample.put("name",name);
        sample.put("org",org);
        sample.put("title",title);

        sample.put("email",email);

        sample.put("cell",cell);
        sample.put("url",url);


    }
    public Map<String, String> getParams() {
        return sample;
    }
}
