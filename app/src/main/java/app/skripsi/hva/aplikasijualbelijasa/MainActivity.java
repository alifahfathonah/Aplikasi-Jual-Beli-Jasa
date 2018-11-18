package app.skripsi.hva.aplikasijualbelijasa;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import app.skripsi.hva.aplikasijualbelijasa.R;

import app.skripsi.hva.aplikasijualbelijasa.app.AppController;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Depan.OnfragmentInteractionListener,
        Profil.OnfragmentInteractionListener,
        Iklankan.OnFragmentInteractionListener,
        Bantuan.OnfragmentInteractionListener,
        Tentang.OnfragmentInteractionListener{

    Button btn_logout;
    TextView txt_id, txt_username;
    String id, username, usernamefb, userimage, nama_akun, no_hp, email_user, urlImage;
    SharedPreferences sharedpreferences;
    ImageLoader imageLoader;

    CallbackManager callbackManager;
    /*URL profile_picture;*/

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    Bundle fbbundle;

    Bundle bundle;

    public static final String TAG_ID_USER = "id_akun";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_NAMA_AKUN = "nama_akun";
    public static final String TAG_USERIMAGE = "userimage";
    public static final String TAG_NO_HP = "no_hp";
    public static final String TAG_EMAIL = "email";

    public static Context contextOfApplication;

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contextOfApplication = getApplicationContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        txt_id = (TextView) header.findViewById(R.id.txt_id);
        txt_username = (TextView) header.findViewById(R.id.txt_username);
        btn_logout = (Button) header.findViewById(R.id.action_keluar);


        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);

        Intent intent = getIntent();
        /*=============FB Login=============*/
        fbbundle = new Bundle();
        fbbundle = getIntent().getExtras();
        final String name = fbbundle.getString("name");
        final String email = fbbundle.getString("email");
        final String id_fb = fbbundle.getString("id"); // untuk set url foto
        final String id_akun = fbbundle.getString("id_akun");

        callbackManager = CallbackManager.Factory.create();
        /*===================================*/

        if (intent.hasExtra("name")){ // Jika login akun FB maka memuat parameter name
            id = fbbundle.getString("id_akun");
            usernamefb = fbbundle.getString("name");
            username = fbbundle.getString("username");
            urlImage = fbbundle.getString("id");
            userimage = "https://graph.facebook.com/"+ id_fb + "/picture?width=100&height=100";
            email_user = getIntent().getStringExtra("email");
        } else {
            id = getIntent().getStringExtra(TAG_ID_USER);
            username = getIntent().getStringExtra(TAG_USERNAME);
            nama_akun = getIntent().getStringExtra(TAG_NAMA_AKUN);
            userimage = getIntent().getStringExtra(TAG_USERIMAGE);
            no_hp = getIntent().getStringExtra(TAG_NO_HP);
            email_user = getIntent().getStringExtra(TAG_EMAIL);
        }

        bundle = new Bundle();
        bundle.putString("id_akun", id);
        bundle.putString("username", username);
        bundle.putString("name", usernamefb);
        bundle.putString("nama_akun", nama_akun);
        bundle.putString("userimage", userimage);
        bundle.putString("no_hp", no_hp);
        bundle.putString("email", email_user); //Login akun biasa & FB
        bundle.putString("name", name); // Login FB


        if (intent.hasExtra("name")){ //Jika Login FB maka Intent memuat parameter "name"
            txt_username.setText(name);
            txt_id.setText(email);
        } else {
            if (email_user.trim().equals("")){
                txt_id.setText(no_hp);
                txt_username.setText(nama_akun);
            } else {
                txt_id.setText(email_user);
                txt_username.setText(nama_akun);
            }
        }

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) header
                .findViewById(R.id.imageJasa);

        if (intent.hasExtra("name")){
                urlImage = ("https://graph.facebook.com/"+ id_fb + "/picture?width=100&height=100");
                thumbNail.setImageUrl(urlImage, imageLoader);
        } else {
            thumbNail.setImageUrl(userimage, imageLoader);
        }


        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 101);

        }

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        printKeyHash(); //Menampilkan Hash Key

        navigationView.getMenu().getItem(0).setChecked(true); // untuk highlight item di navigation draw saat pertama launch app

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Depan depan = new Depan();
        depan.setArguments(bundle);
        fragmentTransaction.replace(R.id.content_main, depan);
        fragmentTransaction.commit();

        createLocationRequest();
    }

    public Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int width = bitmap.getWidth();
        if(bitmap.getWidth()>bitmap.getHeight())
            width = bitmap.getHeight();
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, width);
        final RectF rectF = new RectF(rect);
        final float roundPx = width / 2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }




    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    /*Print Hash Key*/
    private void printKeyHash(){
        try{
            PackageInfo info = getPackageManager().getPackageInfo("com.example.hva.aplikasijualbelijasa", PackageManager.GET_SIGNATURES);
            for (Signature signature:info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("Key hash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Apakah anda ingin keluar dari akun?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                if (AccessToken.getCurrentAccessToken() != null){
                    LoginManager.getInstance().logOut();
                /*Intent login = new Intent(MainActivity.this, Login.class);
                startActivity(login);*/
                    finish();
                } else {
                    // update login session ke FALSE dan mengosongkan nilai id dan username
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(Login.session_status, false);
                    editor.putString(TAG_ID_USER, null);
                    editor.putString(TAG_USERNAME, null);
                    editor.apply();

                    finish();
                    /*Intent intent = new Intent(MainActivity.this, Login.class);*/

                    /*startActivity(intent);*/
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_keluar) {
            createAndShowAlertDialog();
        }

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.support.v4.app.Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.nav_depan) {
            fragmentClass = Depan.class;
        } else if (id == R.id.nav_profil) {
            fragmentClass = Profil.class;
        } else if (id == R.id.nav_iklankan) {
            fragmentClass = Iklankan.class;
        } else if (id == R.id.nav_bantuan) {
            fragmentClass = Bantuan.class;
        } else if (id == R.id.nav_tentang) {
            fragmentClass = Tentang.class;
        }
        try {
            fragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        assert fragment != null;
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.content_main, fragment)
                .addToBackStack(null)
                .commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

}
