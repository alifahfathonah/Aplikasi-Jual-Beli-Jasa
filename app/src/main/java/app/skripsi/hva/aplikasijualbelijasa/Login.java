package app.skripsi.hva.aplikasijualbelijasa;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import app.skripsi.hva.aplikasijualbelijasa.R;

import app.skripsi.hva.aplikasijualbelijasa.app.AppController;
import app.skripsi.hva.aplikasijualbelijasa.util.Server;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    ProgressDialog pDialog; // FB Login
    CallbackManager callbackManager; // FB Login

    public GoogleApiClient googleApiClient;

    Button btn_phone, btnEmail, btn_login;
    /*SignInButton btnEmail;*/
    EditText txt_username, txt_password;

    private static final int REQUEST_CODE = 999;
    private static final int REQ_CODE = 909;

    int success;
    ConnectivityManager conMgr;

    private String url = Server.URL + "Login.php";
    private String urlInputLoginFB = Server.URL + "inputLoginFB.php";

    private static final String TAG = Login.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public final static String TAG_USERNAME = "username";
    public final static String TAG_NAMA_AKUN = "nama_akun";
    public final static String TAG_ID = "id_akun";
    public final static String TAG_USERIMAGE = "userimage";
    public final static String TAG_NO_HP = "no_hp";
    public final static String TAG_EMAIL = "email";

    String tag_json_obj = "json_obj_req";

    SharedPreferences sharedpreferences;
    Boolean session = false;
    String id, username, nama_akun, userimage, no_hp, email, fbusername, fb_id, fbuserimage, fbemail;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet",
                        Toast.LENGTH_LONG).show();
            }
        }

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_phone = (Button) findViewById(R.id.btn_register);
        txt_username = (EditText) findViewById(R.id.txt_username);
        txt_password = (EditText) findViewById(R.id.txt_password);
        btnEmail = (Button) findViewById(R.id.btnEmail);
        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button); //FB Login
        loginButton.setReadPermissions(Arrays.asList("public_profile")); //FB Login

        // Cek session login jika TRUE maka langsung buka MainActivity
        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        id = sharedpreferences.getString(TAG_ID, null);
        username = sharedpreferences.getString(TAG_USERNAME, null);
        nama_akun = sharedpreferences.getString(TAG_NAMA_AKUN, null);
        userimage = sharedpreferences.getString(TAG_USERIMAGE, null);
        no_hp = sharedpreferences.getString(TAG_NO_HP, null);
        email = sharedpreferences.getString(TAG_EMAIL, null);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();
        /*setGooglePlusButtonText(btnEmail, "Membuat Akun dengan Email");*/

        if (session) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.putExtra(TAG_ID, id);
            intent.putExtra(TAG_USERNAME, username);
            intent.putExtra(TAG_USERIMAGE, userimage);
            intent.putExtra(TAG_NAMA_AKUN, nama_akun);
            intent.putExtra(TAG_NO_HP, no_hp);
            intent.putExtra(TAG_EMAIL, email);
            startActivity(intent);
            finish();
        }


        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String username = txt_username.getText().toString();
                String password = txt_password.getText().toString();

                // mengecek kolom yang kosong
                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        checkLogin(username, password);
                    } else {
                        Toast.makeText(getApplicationContext() ,"Tidak ada koneksi internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext() ,"Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_phone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startLoginPage(LoginType.PHONE);
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*startLoginPage(LoginType.EMAIL);*/
                signIn();
            }
        });

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                pDialog = new ProgressDialog(Login.this);
                pDialog.setMessage("Mengambil data...");
                pDialog.show();

                String accesstoken = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        pDialog.dismiss();
                        Log.d("response",response.toString());

                        getData(object);
                    }
                });

                //Request Graph API
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,email,name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        //If already Login
        if (AccessToken.getCurrentAccessToken() != null)
        {
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Mengambil data...");
            pDialog.show();

            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    pDialog.dismiss();
                    Log.d("response",response.toString());

                    getData(object);
                }
            });

            //Request Graph API
            Bundle parameters = new Bundle();
            parameters.putString("fields","id,email,birthday,friends, name");
            request.setParameters(parameters);
            request.executeAsync();
        }

    }

    private void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);
    }

    private void handleResult(GoogleSignInResult result){
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            String name = account.getDisplayName();
            String email = account.getEmail();
            Bundle gmail = new Bundle();
            gmail.putString("name", name);
            gmail.putString("email", email);

            Intent register = new Intent(this, Register.class);
            register.putExtras(gmail);
            startActivity(register);
        }

    }

    private void getData(final JSONObject object){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlInputLoginFB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    String id_akun = jObj.getString("id_akun");
                    String username = jObj.getString("username");

                    if (success == 1) {

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        Intent main = new Intent(Login.this, MainActivity.class);
                        main.putExtra("name", fbusername);
                        main.putExtra("email", fbemail);
                        main.putExtra("id", fb_id);
                        main.putExtra("id_akun", id_akun);
                        main.putExtra("username", username);
                        startActivity(main);
                        finish();

                    } else {

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        Intent main = new Intent(Login.this, MainActivity.class);
                        main.putExtra("name", fbusername);
                        main.putExtra("email", fbemail);
                        main.putExtra("id", fb_id);
                        main.putExtra("id_akun", id_akun);
                        main.putExtra("username", username);
                        startActivity(main);
                        finish();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                try {
                    fb_id = object.getString("id");
                    fbuserimage = "https://graph.facebook.com/"+ fb_id + "/picture?width=100&height=100";
                    fbusername = object.getString("name");
                    fbemail = object.getString("email");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("userimage", fbuserimage);
                params.put("username", fbusername);
                params.put("email", fbemail);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private void startLoginPage(LoginType loginType) {
        if (loginType == LoginType.PHONE) {
            Intent intent = new Intent(this, AccountKitActivity.class);
            AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                    new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                            AccountKitActivity.ResponseType.TOKEN); //Use token when 'Enable Client Access Token Flow' is YES
            intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE)
        {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (result.getError() != null) {
                Toast.makeText(this, ""+result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            else if (result.wasCancelled())
            {
                AccountKit.logOut();
                Toast.makeText(this, "Dibatalkan", Toast.LENGTH_SHORT).show();
                return;
            }
            else
            {
                if (result.getAccessToken() != null)
                    Toast.makeText(this, "Sukses! %s"+result.getAccessToken().getAccountId(),Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this, "Sukses! %s"+result.getAuthorizationCode().substring(0,10),Toast.LENGTH_LONG).show();

                startActivity(new Intent(this,Register.class));
            }
        } else if (requestCode==REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }


        else {
            callbackManager.onActivityResult(requestCode,resultCode,data);
        }
    }


    private void checkLogin(final String username, final String password) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Masuk akun, mohon tunggu ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
                        String username = jObj.getString(TAG_USERNAME);
                        String nama_akun = jObj.getString(TAG_NAMA_AKUN);
                        String id_akun = jObj.getString(TAG_ID);
                        String userimage = jObj.getString(TAG_USERIMAGE);
                        String no_hp = jObj.getString(TAG_NO_HP);
                        String email = jObj.getString(TAG_EMAIL);

                        Log.e("Berhasil Masuk!", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        // menyimpan login ke session
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_ID, id_akun);
                        editor.putString(TAG_USERNAME, username);
                        editor.putString(TAG_NAMA_AKUN, nama_akun);
                        editor.putString(TAG_USERIMAGE, userimage);
                        editor.putString(TAG_NO_HP, no_hp);
                        editor.putString(TAG_EMAIL, email);
                        editor.commit();

                        // Memanggil main activity
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra(TAG_ID, id_akun);
                        intent.putExtra(TAG_USERNAME, username);
                        intent.putExtra(TAG_USERIMAGE, userimage);
                        intent.putExtra(TAG_NAMA_AKUN, nama_akun);
                        intent.putExtra(TAG_NO_HP, no_hp);
                        intent.putExtra(TAG_EMAIL, email);
                        finish();
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Tekan sekali lagi untuk keluar.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}