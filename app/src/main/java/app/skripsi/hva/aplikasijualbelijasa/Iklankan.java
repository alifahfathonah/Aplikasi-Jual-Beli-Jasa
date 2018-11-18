package app.skripsi.hva.aplikasijualbelijasa;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import app.skripsi.hva.aplikasijualbelijasa.R;

import app.skripsi.hva.aplikasijualbelijasa.adapter.AdapterKategori;
import app.skripsi.hva.aplikasijualbelijasa.app.AppController;
import app.skripsi.hva.aplikasijualbelijasa.data.DataKategori;
import app.skripsi.hva.aplikasijualbelijasa.util.Server;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Iklankan extends Fragment implements View.OnClickListener{

    Button buttonAlamat;
    TextView textNamaAlamat, textDetail, txt_latitude, txt_longitude;

    private static final int PLACE_PICKER_REQUEST = 1000;
    private GoogleApiClient mClient;

    LocationManager locationManager;

    private Button buttonInsert;

    private ImageView imageView;

    private EditText editTextJasa, editTextHarga, editTextDeskripsi, editTextWA;

    String akun;

    private TextView txt_kategori;

    private Bitmap bitmap;

    private int PICK_IMAGE_REQUEST = 1;

    private String UPLOAD_URL = Server.URL + "Upload.php";

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "nama_jasa";
    private String KEY_HARGA = "harga_jasa";
    private String KEY_DESKRIPSI = "deskripsi_jasa";
    private String KEY_ALAMAT = "alamat";
    private String KEY_LATITUDE = "latitude";
    private String KEY_LONGITUDE = "longitude";
    private String KEY_KATEGORI = "kategori";
    private String KEY_ID_AKUN = "id_akun";
    private String KEY_HP_JASA = "hp_jasa";

    Spinner spinnerKategori;

    ProgressDialog pDialog;

    AdapterKategori adapterKategori;

    List<DataKategori> listKategori = new ArrayList<DataKategori>();

    public static final String urlKategori = Server.URL + "Kategori.php";

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String TAG_ID_KATEGORI = "id_kategori";
    public static final String TAG_KATEGORI = "nama_kategori";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //list_iklan untuk fragment
        FrameLayout iklankanView = (FrameLayout) inflater.inflate(R.layout.fragment_iklankan, container, false);

        textNamaAlamat = (TextView) iklankanView.findViewById(R.id.textNamaAlamat);
        textDetail = (TextView) iklankanView.findViewById(R.id.textDetail);
        txt_latitude = (TextView) iklankanView.findViewById(R.id.txt_latitude);
        txt_longitude = (TextView) iklankanView.findViewById(R.id.txt_longitude);

        mClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        Bundle bundle = getArguments(); // mengambil data akun yg sedang login
        assert bundle != null;
        akun = bundle.getString("id_akun"); // mengambil data akun yg sedang login

        buttonAlamat = (Button) iklankanView.findViewById(R.id.buttonAlamat);
        buttonAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

        buttonInsert = (Button) iklankanView.findViewById(R.id.buttonInsert);

        editTextJasa = (EditText) iklankanView.findViewById(R.id.editTextJasa);
        editTextHarga = (EditText) iklankanView.findViewById(R.id.editTextHarga);
        editTextDeskripsi = (EditText) iklankanView.findViewById(R.id.editTextDeskripsi);
        txt_kategori = (TextView) iklankanView.findViewById(R.id.txt_kategori);
        editTextWA = (EditText) iklankanView.findViewById(R.id.HPjasa);

        imageView  = (ImageView) iklankanView.findViewById(R.id.imageJasa);

        imageView.setOnClickListener(this);
        buttonInsert.setOnClickListener(this);

        //Spinner Data Load
        spinnerKategori = (Spinner) iklankanView.findViewById(R.id.spinnerKategori);

        adapterKategori = new AdapterKategori(getActivity(), listKategori);
        spinnerKategori.setAdapter(adapterKategori);

        spinnerKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (spinnerKategori.getSelectedItem() == "Pilih Kategori"){

                } else {
                    txt_kategori.setText(listKategori.get(position).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        callDataKategori();

        return iklankanView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //mengubah judul pada toolbar
        getActivity().setTitle("Iklankan");
    }

    private void callDataKategori() {
        listKategori.clear();

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        pDialog.setMessage("Memuat Kategori");
        showDialog();

        // Creating volley request obj
        JsonArrayRequest jArr = new JsonArrayRequest(urlKategori,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(TAG, response.toString());

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);

                                DataKategori item = new DataKategori();

                                item.setId(obj.getString(TAG_ID_KATEGORI));
                                item.setKategori(obj.getString(TAG_KATEGORI));

                                listKategori.add(item);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // notifying list adapterProv about data changes
                        // so that it renders the list view with updated data
                        adapterKategori.notifyDataSetChanged();

                        hideDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jArr);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Foto"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onStop() {
        mClient.disconnect();
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Place place = PlacePicker.getPlace(data, getActivity());
                String placename = String.format("%s", place.getName());
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                String address = String.format("%s", place.getAddress());
                textNamaAlamat.setText(placename);
                textDetail.setText(address);
                txt_latitude.setText(latitude);
                txt_longitude.setText(longitude);
            }
        }
    }

    private void Iklankan(){
        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Mengiklankan...","Mohon Tunggu...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        loading.dismiss();
                        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        loading.dismiss();
                        Toast.makeText(getActivity(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = getStringImage(bitmap);
                String nama_jasa = editTextJasa.getText().toString().trim();
                String harga_jasa = editTextHarga.getText().toString().trim();
                String deskripsi_jasa = editTextDeskripsi.getText().toString().trim();
                String alamat = textDetail.getText().toString().trim();
                String latitude = txt_latitude.getText().toString().trim();
                String longitude = txt_longitude.getText().toString().trim();
                String kategori = txt_kategori.getText().toString().trim();
                String HPjasa = editTextWA.getText().toString().trim();


                Map<String,String> params = new Hashtable<String, String>();
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, nama_jasa);
                params.put(KEY_HARGA, harga_jasa);
                params.put(KEY_DESKRIPSI, deskripsi_jasa);
                params.put(KEY_ALAMAT, alamat);
                params.put(KEY_LATITUDE, latitude);
                params.put(KEY_LONGITUDE, longitude);
                params.put(KEY_KATEGORI, kategori);
                params.put(KEY_ID_AKUN, akun);
                params.put(KEY_HP_JASA, HPjasa);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        requestQueue.add(stringRequest);

    }

    @Override
    public void onClick(View v) {

        if(v == imageView){
            showFileChooser();
        }

        if(v == buttonInsert){
            Iklankan();
        }
    }

    public Context getApplicationContext() {

        Context applicationContext = MainActivity.getContextOfApplication();
        applicationContext.getContentResolver();

        return applicationContext;
    }

    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Apakah anda ingin membatalkan pengiklanan?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                DetailIklanProfil detailIklanProfil = new DetailIklanProfil();
                fragmentTransaction.replace(R.id.content_main, detailIklanProfil);
                fragmentTransaction.commit();
                dialog.dismiss();
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
