package app.skripsi.hva.aplikasijualbelijasa;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditIklan extends Fragment implements View.OnClickListener {

    String TagID_jasa, TagNama_jasa, TagHarga, TagDeskripsi, TagJarak, TagImage, TagAlamat,
            TagHp_jasa, TagUsername, TagAkun, TagImageAkun, name, email, no_hp;
    EditText editTextJasa, editTextHarga, editTextDeskripsi, editTextHP;
    private TextView txt_kategori;
    TextView txtAlamat, txt_latitude, txt_longitude, textNamaAlamat, textDetail ;
    Button buttonAlamat, buttonInsert, btnDelete;
    ImageView buttonChoose;
    private ImageView imageView;

    private static final int PLACE_PICKER_REQUEST = 1000;
    private GoogleApiClient mClient;

    AdapterKategori adapterKategori;

    List<DataKategori> listKategori = new ArrayList<DataKategori>();

    Spinner spinnerKategori;

    Bundle bundleEdit;

    public static final String urlKategori = Server.URL + "Kategori.php";
    private static final String url_delete = Server.URL + "DeleteIklanRev.php?id_jasa=";
    private String UPLOAD_URL = Server.URL + "EditIklan.php";


    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private static final String TAG = MainActivity.class.getSimpleName();

    private Bitmap bitmap;

    private int PICK_IMAGE_REQUEST = 1;

    String tag_json_obj = "json_obj_req";

    public static final String TAG_ID_KATEGORI = "id_kategori";
    public static final String TAG_KATEGORI = "nama_kategori";

    private String KEY_ID_JASA = "id_jasa";
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "nama_jasa";
    private String KEY_HARGA = "harga_jasa";
    private String KEY_DESKRIPSI = "deskripsi_jasa";
    private String KEY_ALAMAT = "alamat";
    private String KEY_HP_JASA = "hp_jasa";
    private String KEY_LATITUDE = "latitude";
    private String KEY_LONGITUDE = "longitude";
    private String KEY_KATEGORI = "kategori";
    private String KEY_ID_AKUN = "id_akun";

    ProgressDialog pDialog;

    public EditIklan() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_iklan, container, false);

        Bundle bundleIklanProfil = getArguments();

        TagID_jasa = bundleIklanProfil.getString("ID");
        TagNama_jasa = bundleIklanProfil.getString("nama");
        TagHarga = bundleIklanProfil.getString("harga");
        TagDeskripsi = bundleIklanProfil.getString("deskripsi");
        TagJarak = bundleIklanProfil.getString("jarak");
        TagImage = bundleIklanProfil.getString("image");
        TagAlamat = bundleIklanProfil.getString("alamat");
        TagHp_jasa = bundleIklanProfil.getString("hp_jasa");
        TagUsername = bundleIklanProfil.getString("username");
        TagAkun = bundleIklanProfil.getString("id_akun");
        TagImageAkun = bundleIklanProfil.getString("userimage");
        name = bundleIklanProfil.getString("name"); // jika login FB
        email = bundleIklanProfil.getString("email");
        no_hp = bundleIklanProfil.getString("no_hp");

        bundleEdit = new Bundle();
        bundleEdit.putString("id_akun", TagAkun);
        bundleEdit.putString("username", TagUsername);
        bundleEdit.putString("userimage", TagImageAkun);
        bundleEdit.putString("name", name); // jika login FB
        bundleEdit.putString("email", email);
        bundleEdit.putString("no_hp", no_hp);

        editTextJasa = (EditText) rootView.findViewById(R.id.editTextJasa);
        editTextHarga = (EditText) rootView.findViewById(R.id.editTextHarga);
        editTextDeskripsi = (EditText) rootView.findViewById(R.id.editTextDeskripsi);
        editTextHP = (EditText) rootView.findViewById(R.id.HPjasa);
        txtAlamat = (TextView) rootView.findViewById(R.id.textDetail);
        spinnerKategori = (Spinner) rootView.findViewById(R.id.spinnerKategori);
        txt_kategori = (TextView) rootView.findViewById(R.id.txt_kategori);
        txt_latitude = (TextView) rootView.findViewById(R.id.txt_latitude);
        txt_longitude = (TextView) rootView.findViewById(R.id.txt_longitude);
        textNamaAlamat = (TextView) rootView.findViewById(R.id.textNamaAlamat);
        textDetail = (TextView) rootView.findViewById(R.id.textDetail);
        btnDelete = (Button) rootView.findViewById(R.id.btnDelete);
        buttonChoose = (ImageView) rootView.findViewById(R.id.btnUnggahFoto);
        buttonInsert = (Button) rootView.findViewById(R.id.buttonInsert);

        mClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        buttonAlamat = (Button) rootView.findViewById(R.id.buttonAlamat);
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

        editTextJasa.setText(TagNama_jasa);
        editTextHarga.setText(TagHarga);
        editTextDeskripsi.setText(TagDeskripsi);
        editTextHP.setText(TagHp_jasa);
        txtAlamat.setText(TagAlamat);



        buttonChoose.setOnClickListener(this);
        buttonInsert.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        adapterKategori = new AdapterKategori(getActivity(), listKategori);
        spinnerKategori.setAdapter(adapterKategori);

        spinnerKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                txt_kategori.setText(listKategori.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        callDataKategori();

        return rootView;
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
                buttonChoose.setImageBitmap(bitmap);
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void Iklankan(){
        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Memperbarui...","Mohon Tunggu...",false,false);
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
                        Toast.makeText(getActivity(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id_jasa = TagID_jasa;
                String image = getStringImage(bitmap);
                String nama_jasa = editTextJasa.getText().toString().trim();
                String harga_jasa = editTextHarga.getText().toString().trim();
                String deskripsi_jasa = editTextDeskripsi.getText().toString().trim();
                String alamat = textDetail.getText().toString().trim();
                String hp_jasa = editTextHP.getText().toString().trim();
                String latitude = txt_latitude.getText().toString().trim();
                String longitude = txt_longitude.getText().toString().trim();
                String kategori = txt_kategori.getText().toString().trim();

                Map<String,String> params = new Hashtable<String, String>();
                params.put(KEY_ID_JASA, id_jasa);
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, nama_jasa);
                params.put(KEY_HARGA, harga_jasa);
                params.put(KEY_DESKRIPSI, deskripsi_jasa);
                params.put(KEY_ALAMAT, alamat);
                params.put(KEY_HP_JASA, hp_jasa);
                params.put(KEY_LATITUDE, latitude);
                params.put(KEY_LONGITUDE, longitude);
                params.put(KEY_KATEGORI, kategori);
                params.put(KEY_ID_AKUN, TagAkun);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        requestQueue.add(stringRequest);
    }

    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Apakah anda ingin menghapus iklan ini?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                delete(TagID_jasa);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Profil profil = new Profil();
                profil.setArguments(bundleEdit);
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main, profil).addToBackStack(null);
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

    @Override
    public void onClick(View v) {

        if(v == buttonChoose){
            showFileChooser();
        }

        if(v == buttonInsert){
            Iklankan();
        }

        if (v == btnDelete){
            createAndShowAlertDialog();
        }
    }



    private void delete(final String idJasa) {
        StringRequest strReq = new StringRequest(url_delete + idJasa, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {

                    } else {
                        Toast.makeText(getActivity(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_jasa", idJasa);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //mengubah judul pada toolbar
        getActivity().setTitle("Edit");
    }

}
