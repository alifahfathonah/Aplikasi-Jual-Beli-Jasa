package app.skripsi.hva.aplikasijualbelijasa;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import app.skripsi.hva.aplikasijualbelijasa.R;

import app.skripsi.hva.aplikasijualbelijasa.app.AppController;
import app.skripsi.hva.aplikasijualbelijasa.util.Server;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    ProgressDialog pDialog;
    Button btn_register, btn_login;
    TextView txtHp, txtEmail;
    EditText txt_username, txt_password, txtNama, txt_confirm_password;
    Intent intent = getIntent();
    private ImageView imageViewUser;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private int request_code = 1;
    private Bitmap bitmap_foto;
    private RoundedBitmapDrawable roundedBitmapDrawable;
    private GoogleApiClient googleApiClient;

    int success;
    ConnectivityManager conMgr;

    Bundle gmailIntent;

    private String url = Server.URL + "Register.php";

    private static final String TAG = Register.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";
    private String nama, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        gmailIntent = getIntent().getExtras();
        nama = gmailIntent.getString("name");
        email = gmailIntent.getString("email");

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(Register.this, this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

        imageViewUser = (ImageView) findViewById(R.id.userImage);
        bitmap_foto = BitmapFactory.decodeResource(getResources(),R.drawable.imageuser);
        roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap_foto);
        roundedBitmapDrawable.setCircular(true);
        imageViewUser.setImageDrawable(roundedBitmapDrawable);

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);
        txt_username = (EditText) findViewById(R.id.txt_username);
        txt_password = (EditText) findViewById(R.id.txt_password);
        txt_confirm_password = (EditText) findViewById(R.id.txt_confirm_password);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtHp = (TextView) findViewById(R.id.txtHp);
        txtNama = (EditText) findViewById(R.id.editTextNama);


        Intent intent = getIntent();
        if (intent.hasExtra("name") && intent.hasExtra("email")){
            txtEmail.setText(email);
            txtNama.setText(nama);
        }

        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent login = new Intent(Register.this, Login.class);
                finish();
                startActivity(login);
            }
        });

        imageViewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Pilih Foto"), PICK_IMAGE_REQUEST);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (bitmap == null) {
                    Toast.makeText(getApplicationContext(), "Silakan unggah foto", Toast.LENGTH_SHORT).show();
                } else {
                        String image = getStringImage(bitmap);
                        String username = txt_username.getText().toString();
                        String nama_akun = txtNama.getText().toString();
                        String password = txt_password.getText().toString();
                        String confirm_password = txt_confirm_password.getText().toString();
                        String hp = txtHp.getText().toString();
                        String email = txtEmail.getText().toString();

                        if (password.trim().length() < 8) {
                            Toast.makeText(Register.this, "Kata sandi yang anda masukkan kurang dari 8 karakter", Toast.LENGTH_LONG).show();
                        } else if (password.trim().length() >= 8){
                            if (conMgr.getActiveNetworkInfo() != null
                                    && conMgr.getActiveNetworkInfo().isAvailable()
                                    && conMgr.getActiveNetworkInfo().isConnected()) {
                                checkRegister(image, username, nama_akun, password, confirm_password, hp, email);
                            } else {
                                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        });

    }

    public void signOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(Account account) {
                if (account.getPhoneNumber() != null){
                    txtHp.setText(String.format("%s", account.getPhoneNumber() == null ? "":account.getPhoneNumber().toString()));
                } /*else if (account.getEmail() != null){
                    txtEmail.setText(String.format("%s", account.getEmail() == null ? "": account.getEmail()));
                }*/
            }

            @Override
            public void onError(AccountKitError accountKitError) {

            }
        });
    }

    private void checkRegister(final String userimage, final String username, final String nama_akun, final String password, final String confirm_password, final String hp, final String email) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Mendaftar ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        Log.e("Berhasil membuat akun!", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        txt_username.setText("");
                        txt_password.setText("");
                        txt_confirm_password.setText("");

                        if (googleApiClient != null) {
                            signOut();
                        }

                        Intent login = new Intent(Register.this, Login.class);
                        startActivity(login);
                        finish();

                        AccountKit.logOut();

                    } else if (success == 0){
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

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
                params.put("userimage", userimage);
                params.put("username", username);
                params.put("nama_akun", nama_akun);
                params.put("password", password);
                params.put("confirm_password", confirm_password);
                params.put("hp", hp);
                params.put("email", email);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                roundedBitmapDrawable.setCircular(true);
                imageViewUser.setImageDrawable(roundedBitmapDrawable);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {

        if (googleApiClient != null){
            signOut();

            Intent loginIntent = new Intent(Register.this, Login.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(loginIntent);
            Toast.makeText(getApplicationContext(),"Dibatalkan",Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Intent loginIntent = new Intent(Register.this, Login.class);
            startActivity(loginIntent);
            finish();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

