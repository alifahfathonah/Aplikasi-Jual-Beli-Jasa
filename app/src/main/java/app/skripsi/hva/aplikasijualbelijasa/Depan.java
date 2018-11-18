package app.skripsi.hva.aplikasijualbelijasa;


import android.Manifest;
import android.content.Context;
import android.location.LocationListener;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import app.skripsi.hva.aplikasijualbelijasa.R;

import app.skripsi.hva.aplikasijualbelijasa.adapter.CustomListAdapter;
import app.skripsi.hva.aplikasijualbelijasa.app.AppController;
import app.skripsi.hva.aplikasijualbelijasa.data.GetIklan;
import app.skripsi.hva.aplikasijualbelijasa.util.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class Depan extends Fragment implements LocationListener,
        SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    CardView cvDesain, cvKursus, cvPerbaikan, cvPenyewaan;

    ListView list_view;
    SwipeRefreshLayout swipe;
    Criteria criteria;
    Location location;
    LocationManager locationManager;
    List<GetIklan> itemList = new ArrayList<>();
    Double latitude, longitude;
    CustomListAdapter adapter;
    String provider, id_akun, nama, name, nama_akun, image_akun, email, no_hp;
    ListView listView;
    TextView selengkapnya;
    FloatingActionButton fabDepan;
    Bundle bundleDepan;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String url = Server.URL + "LoadIklanHaversineJoin.php?lat=";

    private FragmentManager supportFragmentManager;

    private MenuInflater menuInflater;

    public Depan() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the list_iklan for this fragment
        View rootview = inflater.inflate(R.layout.fragment_depan, container, false);
        setHasOptionsMenu(true);

        bundleDepan = getArguments();
        nama_akun = bundleDepan.getString("nama_akun");
        name = bundleDepan.getString("name"); // jika login FB
        nama = bundleDepan.getString("username");
        id_akun = bundleDepan.getString("id_akun");
        image_akun = bundleDepan.getString("userimage");
        no_hp = bundleDepan.getString("no_hp");
        email = bundleDepan.getString("email");


        listView = (ListView) rootview.findViewById(R.id.list_view);
        swipe = (SwipeRefreshLayout) rootview.findViewById(R.id.swipe);

        // mengisi data dari adapter ke listview
        adapter = new CustomListAdapter(getActivity(), itemList);
        listView.setAdapter(adapter);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        cvDesain = (CardView) rootview.findViewById(R.id.cv_desain);
        cvKursus = (CardView) rootview.findViewById(R.id.cv_kursus);
        cvPenyewaan = (CardView) rootview.findViewById(R.id.cv_penyewaan);
        cvPerbaikan = (CardView) rootview.findViewById(R.id.cv_perbaikan);
        selengkapnya = (TextView) rootview.findViewById(R.id.txtKategoriDepan);
        fabDepan = (FloatingActionButton) rootview.findViewById(R.id.fabDepan);

        cvDesain.setOnClickListener(this);
        cvKursus.setOnClickListener(this);
        cvPenyewaan.setOnClickListener(this);
        cvPerbaikan.setOnClickListener(this);
        fabDepan.setOnClickListener(this);

        selengkapnya.setOnClickListener(this);

        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
                       @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                       @Override
                       public void run() {
                           lokasi();
                       }
                   }
        );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ID_jasa = itemList.get(position).getId_jasa();
                String nama_jasa = itemList.get(position).getNama_jasa();
                String harga = itemList.get(position).getHarga();
                String deskripsi = itemList.get(position).getDeskripsi();
                Double jarak = itemList.get(position).getJarak();
                String image = itemList.get(position).getImage();
                Double latitude = itemList.get(position).getLatitude();
                Double longitude = itemList.get(position).getLongitude();
                String alamat = itemList.get(position).getAlamat();
                String username = itemList.get(position).getUsername();
                String hp_jasa = itemList.get(position).getHp_jasa();
                String nama_akun = itemList.get(position).getNama_akun();
                String userimage = itemList.get(position).getUserimage();
                String tgl_iklan = itemList.get(position).getTgl_iklan();

                Bundle bundleDepan = new Bundle();
                bundleDepan.putString("ID", ID_jasa);
                bundleDepan.putString("nama_jasa", nama_jasa);
                bundleDepan.putString("harga", harga);
                bundleDepan.putString("deskripsi", deskripsi);
                bundleDepan.putDouble("jarak", jarak);
                bundleDepan.putString("alamat", alamat);
                bundleDepan.putString("hp_jasa", hp_jasa);
                bundleDepan.putDouble("latitude", latitude);
                bundleDepan.putDouble("longitude", longitude);
                bundleDepan.putString("image", image);
                bundleDepan.putString("username", username);
                bundleDepan.putString("nama_akun", nama_akun);
                bundleDepan.putString("userimage", userimage);
                bundleDepan.putString("tgl_iklan", tgl_iklan);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                DetailIklan detailIklan = new DetailIklan();
                detailIklan.setArguments(bundleDepan);

                fragmentTransaction.replace(R.id.content_main, detailIklan).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return rootview;
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRefresh() {
        lokasi();
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();

        if (v == cvDesain){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Iklan iklan = new Iklan();
            bundle.putString("kategori", "Desain");

            iklan.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_main, iklan).addToBackStack(null);
            fragmentTransaction.commit();
        } else if (v == cvKursus) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Iklan iklan = new Iklan();
            bundle.putString("kategori", "Kursus");

            iklan.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_main, iklan).addToBackStack(null);
            fragmentTransaction.commit();
        } else if (v == cvPenyewaan) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Iklan iklan = new Iklan();
            bundle.putString("kategori", "Penyewaan");

            iklan.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_main, iklan).addToBackStack(null);
            fragmentTransaction.commit();
        } else if (v == cvPerbaikan) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Iklan iklan = new Iklan();
            bundle.putString("kategori", "Perbaikan");

            iklan.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_main, iklan).addToBackStack(null);
            fragmentTransaction.commit();
        } else if (v == selengkapnya){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Kategori kategori = new Kategori();
            fragmentTransaction.replace(R.id.content_main, kategori).addToBackStack(null);
            fragmentTransaction.commit();
        } else if (v == fabDepan) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();;
            Profil profil = new Profil();
            bundleDepan.putString("id_akun", id_akun);
            bundleDepan.putString("userimage", image_akun);
            bundleDepan.putString("nama_akun",nama_akun);
            bundleDepan.putString("email", email);
            bundleDepan.putString("no_hp", no_hp);
            bundleDepan.putString("name", name);
            bundleDepan.putString("username", nama);
            profil.setArguments(bundleDepan);
            fragmentTransaction.replace(R.id.content_main, profil).addToBackStack(null);
            fragmentTransaction.commit();
        }

    }



    // fungsi ngecek lokasi GPS device pengguna
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void lokasi() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            dataIklanDepan(-7.7828893, 110.3648875);
        }
    }

    // untuk menampilkan lokasi penjual jasa terdekat dari device pengguna
    private void dataIklanDepan(double lat, double lng) {
        itemList.clear();
        adapter.notifyDataSetChanged();

        swipe.setRefreshing(true);

        JsonArrayRequest jArr = new JsonArrayRequest(url + lat + "&lng=" + lng,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(TAG, response.toString());

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                GetIklan getIklan = new GetIklan();
                                getIklan.setNama_jasa(obj.getString("nama_jasa"));

                                getIklan.setDeskripsi(obj.getString("deskripsi"));

                                getIklan.setHarga(obj.getString("harga"));

                                getIklan.setImage(obj.getString("image"));

                                getIklan.setAlamat(obj.getString("alamat"));

                                getIklan.setHp_jasa(obj.getString("hp_jasa"));

                                double latitude = Double.parseDouble(obj.getString("latitude"));
                                getIklan.setLatitude(latitude);

                                double longitude = Double.parseDouble(obj.getString("longitude"));
                                getIklan.setLongitude(longitude);

                                double jarak = Double.parseDouble(obj.getString("jarak"));

                                getIklan.setTgl_iklan(obj.getString("tgl_iklan"));

                                getIklan.setUsername(obj.getString("username"));

                                getIklan.setUserimage(obj.getString("userimage"));

                                getIklan.setNama_akun(obj.getString("nama_akun"));

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

    // untuk menentukan lokasi gps dari device pengguna
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        // untuk melihat latitude longitude posisi device pengguna pada logcat ditemukan atau tidak
        Log.e(TAG, "User location latitude:" + latitude + ", longitude:" + longitude);

        dataIklanDepan(latitude, longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //mengubah judul pada toolbar
        getActivity().setTitle("Halaman Depan");
    }

    public FragmentManager getSupportFragmentManager() {
        return supportFragmentManager;
    }

    public MenuInflater getMenuInflater() {

        return menuInflater;
    }


    public interface OnfragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void onBackPressed()
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
    }

}
