package sample.doordash.com.doordash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hakeem on 1/15/17.
 */

public class LoginActivity extends Activity {

    private RequestQueue mReqQueue;
    private Button mLogin;
    private Button mGuest;
    private EditText mUser;
    private EditText mPass;
    private Preferences mPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mReqQueue = Volley.newRequestQueue(this);
        mPrefs = new Preferences(this);

        mLogin = (Button) findViewById(R.id.login);
        mGuest = (Button) findViewById(R.id.guest_user);

        mUser = (EditText) findViewById(R.id.username);
        mPass = (EditText) findViewById(R.id.password);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    doLogin();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RestaurantsListActivity.class);
                startActivity(i);
            }
        });
    }

    private synchronized void doLogin() throws JSONException {
        setInProgress(true);
        JSONObject creds = new JSONObject();

        creds.put(Constants.KEY_EMAIL, mUser.getText());
        creds.put(Constants.KEY_PASSWORD, mPass.getText());

        JsonObjectRequest jReq = new JsonObjectRequest(Constants.API_AUTH_TOKEN, creds,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setInProgress(false);
                        try {
                            String token = response.getString(Constants.KEY_TOKEN);
                            onTokenRetrieved(token);
                            Toast.makeText(getApplicationContext(), "Login Success: " + token, Toast.LENGTH_LONG).show();
                            mPrefs.addToken(token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Login failure: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        setInProgress(false);
                    }
                });

        mReqQueue.add(jReq);
    }

    private void onTokenRetrieved(final String token) throws JSONException {
        setInProgress(true);

        JsonObjectRequest jReq = new JsonObjectRequest(Constants.API_ABOUT_ME,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setInProgress(false);
                        try {
                            Toast.makeText(getApplicationContext(), "Login Success: " + response.getString("first_name"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Me failure: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        setInProgress(false);
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put(Constants.KEY_AUTH_HEADER,"JWT "+ token);

                return params;
            }
        };

        mReqQueue.add(jReq);
    }

    private void setInProgress(boolean prog){
        mLogin.setEnabled(!prog);
        mGuest.setEnabled(!prog);
        mUser.setEnabled(!prog);
        mPass.setEnabled(!prog);
    }
}
