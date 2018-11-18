package app.skripsi.hva.aplikasijualbelijasa;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import app.skripsi.hva.aplikasijualbelijasa.R;

import app.skripsi.hva.aplikasijualbelijasa.adapter.CustomListAdapter;
import app.skripsi.hva.aplikasijualbelijasa.app.AppController;
import app.skripsi.hva.aplikasijualbelijasa.data.GetIklan;
import app.skripsi.hva.aplikasijualbelijasa.util.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class Iklan extends Fragment implements LocationListener,
        SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    /*Searvh View*/
    ProgressDialog pDialog;

    public static final String url_cari = Server.URL + "caridata.php";

    public static final String TAG_RESULTS = "results";
    public static final String TAG_MESSAGE = "message";
    public static final String TAG_VALUE = "value";

    String tag_json_obj = "json_obj_req";
    /*Searvh View*/

    SwipeRefreshLayout swipe;
    Criteria criteria;
    Location location;
    LocationManager locationManager;
    List<GetIklan> itemList = new ArrayList<>();
    Double latitude, longitude;
    CustomListAdapter adapter;
    String provider;
    ListView listView;
    String Kategori, btnText, lttd, lngtd;
    JSONObject obj;
    TextView txtKategori;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String url = Server.URL + "LoadIklanHaversineKategori.php?lat=";

    private FragmentManager supportFragmentManager;

    private MenuInflater menuInflater;


    public Iklan() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the list_iklan for this fragment
        View rootView = inflater.inflate(R.layout.fragment_iklan, container, false);
        setHasOptionsMenu(true);

        final Bundle bundle = getArguments();
        btnText = bundle.getString("kategori");

        Kategori = btnText;

        txtKategori = (TextView) rootView.findViewById(R.id.txtKategori);
        txtKategori.setText("Iklan Jasa " + Kategori);

        listView = (ListView) rootView.findViewById(R.id.list_view);
        swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);

        // mengisi data dari adapter ke listview
        adapter = new CustomListAdapter(getActivity(), itemList);
        listView.setAdapter(adapter);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ID_jasa = itemList.get(position).getId_jasa();
                String nama_jasa = itemList.get(position).getNama_jasa();
                String harga = itemList.get(position).getHarga();
                String deskripsi = itemList.get(position).getDeskripsi();
                Double jarak = itemList.get(position).getJarak();
                String image = itemList.get(position).getImage();
                String alamat = itemList.get(position).getAlamat();
                String hp_jasa = itemList.get(position).getHp_jasa();
                String username = itemList.get(position).getUsername();
                String nama_akun = itemList.get(position).getNama_akun();
                Double latitude = itemList.get(position).getLatitude();
                Double longitude = itemList.get(position).getLongitude();
                String image_pengiklan = itemList.get(position).getUserimage();
                String tgl_iklan = itemList.get(position).getTgl_iklan();

                Bundle bundleIklan = new Bundle();
                bundleIklan.putString("ID", ID_jasa);
                bundleIklan.putString("nama_jasa", nama_jasa);
                bundleIklan.putString("harga", harga);
                bundleIklan.putString("deskripsi", deskripsi);
                bundleIklan.putDouble("jarak", jarak);
                bundleIklan.putString("image", image);
                bundleIklan.putString("alamat", alamat);
                bundleIklan.putString("hp_jasa", hp_jasa);
                bundleIklan.putDouble("latitude", latitude);
                bundleIklan.putDouble("longitude", longitude);
                bundleIklan.putString("username", username);
                bundleIklan.putString("nama_akun", nama_akun);
                bundleIklan.putString("userimage", image_pengiklan);
                bundleIklan.putString("tgl_iklan", tgl_iklan);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                DetailIklan detailIklan = new DetailIklan();
                detailIklan.setArguments(bundleIklan);

                fragmentTransaction.replace(R.id.content_main, detailIklan).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
                       @Override
                       public void run() {
                           lokasi();
                       }
                   }
        );

        return rootView;
    }

    // fungsi ngecek lokasi GPS device pengguna
    private void lokasi() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(provider);

        // permintaan update lokasi device dalam waktu per detik
        locationManager.requestLocationUpdates(provider, 10000, 1, this);

        if (location != null) {
            onLocationChanged(location);
        } else {
            /* latitude longitude default Tugu Jogja jika tidak ditemukan lokasi dari device pengguna */
            callDataIklan(-7.7828893, 110.3648875, Kategori);
            lttd = "-7.7828893";
            lngtd = "110.3648875";
        }
    }



    // untuk menentukan lokasi gps dari device pengguna
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lttd = latitude.toString();
        lngtd = longitude.toString();

        /*if (latitude != null){
            txtLati.setText("latitude:" + latitude);
            txtLongi.setText("Longitude" + longitude);
        }*/

        // untuk melihat latitude longitude posisi device pengguna pada logcat ditemukan atau tidak
        Log.e(TAG, "User location latitude:" + latitude + ", longitude:" + longitude);


        callDataIklan(latitude, longitude, Kategori);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        cariData(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint(getString(R.string.type_name));
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(this);
    }

    private void cariData(final String keyword) {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(true);
        pDialog.setMessage("Memuat pencarian...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, url_cari, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);

                    int value = jObj.getInt(TAG_VALUE);

                    if (value == 1) {
                        itemList.clear();
                        adapter.notifyDataSetChanged();

                        String getObject = jObj.getString(TAG_RESULTS);
                        JSONArray jsonArray = new JSONArray(getObject);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            obj = jsonArray.getJSONObject(i);

                            GetIklan getIklan = new GetIklan();

                            getIklan.setId_jasa(obj.getString("id_jasa"));
                            getIklan.setNama_jasa(obj.getString("nama_jasa"));
                            getIklan.setDeskripsi(obj.getString("deskripsi"));
                            getIklan.setHarga(obj.getString("harga"));
                            getIklan.setImage(obj.getString("image"));
                            getIklan.setAlamat(obj.getString("alamat"));
                            getIklan.setHp_jasa(obj.getString("hp_jasa"));
                            getIklan.setUsername(obj.getString("username"));
                            double latitude_iklan = Double.parseDouble(obj.getString("latitude"));
                            getIklan.setLatitude(latitude_iklan);
                            double longitude_iklan = Double.parseDouble(obj.getString("longitude"));
                            getIklan.setLongitude(longitude_iklan);
                            getIklan.setTgl_iklan(obj.getString("tgl_iklan"));
                            double jarak = Double.parseDouble(obj.getString("jarak"));
                            getIklan.setJarak(round(jarak, 2));
                            getIklan.setNama_akun(obj.getString("nama_akun"));
                            getIklan.setUserimage(obj.getString("userimage"));


                            itemList.add(getIklan);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameter ke url caridata
                Map<String, String> params = new HashMap<String, String>();
                params.put("keyword", keyword);
                params.put("kategori", Kategori);
                params.put("lat", lttd);
                params.put("lng", lngtd);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    @Override
    public void onRefresh() {
        lokasi();
    }

    // untuk menampilkan lokasi penjual jasa terdekat dari device pengguna
    private void callDataIklan(double lat, double lng, String kategori) {
        itemList.clear();
        adapter.notifyDataSetChanged();

        swipe.setRefreshing(true);

        JsonArrayRequest jArr = new JsonArrayRequest(url + lat + "&lng=" + lng + "&kategori=" + kategori,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(TAG, response.toString());

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                GetIklan getIklan = new GetIklan();

                                getIklan.setId_jasa(obj.getString("id_jasa"));

                                getIklan.setNama_jasa(obj.getString("nama_jasa"));

                                getIklan.setDeskripsi(obj.getString("deskripsi"));

                                getIklan.setHarga(obj.getString("harga"));

                                getIklan.setImage(obj.getString("image"));

                                getIklan.setAlamat(obj.getString("alamat"));

                                getIklan.setHp_jasa(obj.getString("hp_jasa"));

                                getIklan.setUsername(obj.getString("username"));

                                getIklan.setUserimage(obj.getString("userimage"));

                                getIklan.setTgl_iklan(obj.getString("tgl_iklan"));

                                getIklan.setNama_akun(obj.getString("nama_akun"));

                                double latitude_iklan = Double.parseDouble(obj.getString("latitude"));
                                getIklan.setLatitude(latitude_iklan);

                                double longitude_iklan = Double.parseDouble(obj.getString("longitude"));
                                getIklan.setLongitude(longitude_iklan);

                                double jarak = Double.parseDouble(obj.getString("jarak"));
                                getIklan.setJarak(round(jarak, 2));

                                itemList.add(getIklan);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        // memberitahu adapter jika ada perubahan data
                        adapter.notifyDataSetChanged();

                        swipe.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                swipe.setRefreshing(false);
            }
        });

        // menambah permintaan ke queue
        AppController.getInstance().addToRequestQueue(jArr);
    }

    // untuk menyederhanakan angka dibelakan koma jarak
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        /*Message msg = handler.obtainMessage();
        msg.arg1 = 1;
        handler.sendMessage(msg);*/
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //mengubah judul pada toolbar
        getActivity().setTitle("Daftar Iklan");
    }

    public FragmentManager getSupportFragmentManager() {
        return supportFragmentManager;
    }

    public MenuInflater getMenuInflater() {

        return menuInflater;
    }

    public void onBackPressed()
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
    }

}