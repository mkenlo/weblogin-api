package com.mkenlo.webloginclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Request;

public class LoginActivity extends AppCompatActivity {

    EditText mEmail;
    EditText mPasswd;
    Button mLogin;
    static final String SERVER_URL = "http://localhost:8080";
    static final String ENDPOINT = "/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.input_email);
        mPasswd = findViewById(R.id.input_password);
        mLogin = findViewById(R.id.btn_login);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject data =  new JSONObject();
                try{
                    data.put("email", mEmail.getText().toString());
                    data.put("password",mPasswd.getText().toString() );

                    new LoginTask().execute(data.toString());

                }catch(JSONException ex){
                    ex.printStackTrace();
                }

            }
        });


    }

    String dologinPost(String requestData){

        try{
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, requestData);
            String url = SERVER_URL+ENDPOINT;
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();

        }catch(IOException ex){ex.printStackTrace();}

        return "Invalid response. Server not found";
    }

    class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Intent intent = new Intent(getBaseContext(), WebLoginActivity.class);
            intent.putExtra("login", result);
            startActivity(intent);
        }

        @Override
        protected String doInBackground(String... input) {
            return dologinPost(input[0]);
        }
    }

}

