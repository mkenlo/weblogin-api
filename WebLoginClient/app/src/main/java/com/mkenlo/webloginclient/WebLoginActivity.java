package com.mkenlo.webloginclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebLoginActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private TextView mTextView;
    private ZXingScannerView mScannerView;
    private String mAuth_token;
    static final String SERVER_URL = "http://localhost:8080";
    static final String ENDPOINT = "/weblogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_login);
        mTextView = findViewById(R.id.tv_info);

        ViewGroup cameraFrame = findViewById(R.id.cameraView);
        mScannerView = new ZXingScannerView(this);
        cameraFrame.addView(mScannerView);

        if(getIntent().getExtras() != null){
            try{
                Log.v("INTENT EXTRA", getIntent().getStringExtra("login"));

                Toast.makeText(this, "Intent extra received : "+getIntent().getStringExtra("login"), Toast.LENGTH_LONG).show();
                JSONObject success_login = new JSONObject(getIntent().getStringExtra("login"));
                mAuth_token = success_login.getString("auth_token");
            }catch(JSONException ex){
                ex.printStackTrace();
            }

        }

        //mScannerView.setResultHandler(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        Toast.makeText(this, result.getText(), Toast.LENGTH_LONG).show();
        // mScannerView.resumeCameraPreview(this);
        mScannerView.stopCamera();
        new WebLoginTask().execute(result.getText());


    }


    String doQRloginPost(String qrcode){

        try{
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.get("application/json; charset=utf-8");

            JSONObject requestData = new JSONObject();
            requestData.put("image", qrcode);

            RequestBody body = RequestBody.create(JSON, requestData.toString());
            Request request = new Request.Builder()
                    .header("Authorization", "basic "+mAuth_token)
                    .url(SERVER_URL+ENDPOINT)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();

        }catch(Exception ex){ex.printStackTrace();}

        return "Invalid response. Server not found!";
    }

    class WebLoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
            intent.putExtra("login", result);
            startActivity(intent);
        }

        @Override
        protected String doInBackground(String... input) {
            return doQRloginPost(input[0]);
        }
    }
}
