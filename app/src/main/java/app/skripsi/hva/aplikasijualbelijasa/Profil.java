package app.skripsi.hva.aplikasijualbelijasa;


import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;
import app.skripsi.hva.aplikasijualbelijasa.R;

import app.skripsi.hva.aplikasijualbelijasa.adapter.CustomListProfil;
import app.skripsi.hva.aplikasijualbelijasa.app.AppController;
import app.skripsi.hva.aplikasijualbelijasa.data.GetIklan;
import app.skripsi.hva.aplikasijualbelijasa.util.Server;
import com.facebook.FacebookSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */


public class Profil extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {

    TextView nama_profil, id_profil, nama_akun_profil;
    NetworkImageView image_profil;
    String nama, name, nama_akun, id_akun, no_hp, email, image_akun;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    FloatingActionButton fab;

    ProgressDialog pDialog;

    SwipeRefreshLayout swipe;
    Criteria criteria;
    LocationManager locationManager;
    List<GetIklan> itemList = new ArrayList<>();
    CustomListProfil adapter;
    String provider;
    ListView listView;

    Bundle bundleProfil;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String url = Server.URL + "LoadIklanHaversineProfil.php?id=";

    public Profil() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the list_iklan for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profil, container, false);
        setHasOptionsMenu(true);



        final Bundle bundle = getArguments();
        nama = bundle.getString("username");
        nama_akun = bundle.getString("nama_akun");
        id_akun = bundle.getString("id_akun");
        image_akun = bundle.getString("userimage");
        no_hp = bundle.getString("no_hp");
        email = bundle.getString("email");
        name = bundle.getString("name"); // jika login FB

        bundleProfil = new Bundle();
        bundleProfil.putString("id_akun", id_akun);
        bundleProfil.putString("username", nama);
        bundleProfil.putString("nama_akun", nama_akun);
        bundleProfil.putString("name", name);
        bundleProfil.putString("email", email);
        bundleProfil.putString("no_hp", no_hp);

        nama_akun_profil = (TextView) rootView.findViewById(R.id.textNama_akun);
        nama_profil = (TextView) rootView.findViewById(R.id.username_profil);
        id_profil = (TextView) rootView.findViewById(R.id.id_profil);
        image_profil = (NetworkImageView) rootView.findViewById(R.id.image_profil);


            image_profil.setImageUrl(image_akun, imageLoader);

            if (name == null){ //jika parameter name (login FB) kosong
                nama_akun_profil.setText(nama_akun); // setText nama_akun login non FB
            } else {
                nama_akun_profil.setText(name);
            }

            nama_profil.setText(nama);

            if (email.trim().equals("")){
                id_profil.setText(no_hp);
            } else {
                id_profil.setText(email);
            }


        listView = (ListView) rootView.findViewById(R.id.listProfil);
        swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeProfil);

        // mengisi data dari adapter ke listview
        adapter = new CustomListProfil(getActivity(), itemList);
        listView.setAdapter(adapter);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fabProfil);
        // fungsi floating action button memanggil form biodata
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Iklankan iklankan = new Iklankan();
                iklankan.setArguments(bundleProfil);

                fragmentTransaction.replace(R.id.content_main, iklankan).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
                       @Override
                       public void run() {
                           /*lokasi();*/
                           callDataIklan(id_akun);
                           swipe.setRefreshing(true);
                           itemList.clear();
                           adapter.notifyDataSetChanged();
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
                String alamat = itemList.get(position).getAlamat();
                String tgl_iklan = itemList.get(position).getTgl_iklan();
                String hp_jasa = itemList.get(position).getHp_jasa();
                String username = itemList.get(position).getUsername();
                String nama_akun = itemList.get(position).getNama_akun();

                bundleProfil.putString("ID", ID_jasa);
                bundleProfil.putString("nama", nama_jasa);
                bundleProfil.putString("harga", harga);
                bundleProfil.putString("deskripsi", deskripsi);
                bundleProfil.putDouble("jarak", jarak);
                bundleProfil.putString("image", image);
                bundleProfil.putString("alamat", alamat);
                bundleProfil.putString("tgl_iklan", tgl_iklan);
                bundleProfil.putString("hp_jasa", hp_jasa);
                bundleProfil.putString("username", username);
                bundleProfil.putString("nama_akun", nama_akun);
                bundleProfil.putString("id_akun", id_akun);
                bundleProfil.putString("userimage", image_akun);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                DetailIklanProfil detailIklanProfil = new DetailIklanProfil();
                detailIklanProfil.setArguments(bundleProfil);

                fragmentTransaction.replace(R.id.content_main, detailIklanProfil).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        return rootView;
    }

    @Override
    public void onRefresh() {
        callDataIklan(id_akun);
    }

    // untuk menampilkan lokasi penjual jasa terdekat dari device pengguna
    public void callDataIklan(String ID) {
        itemList.clear();
        adapter.notifyDataSetChanged();

        swipe.setRefreshing(true);

        JsonArrayRequest jArr = new JsonArrayRequest(url + ID,
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

                                getIklan.setTgl_iklan(obj.getString("tgl_iklan"));

                                getIklan.setHp_jasa(obj.getString("hp_jasa"));

                                getIklan.setUsername(obj.getString("username"));

                                getIklan.setNama_akun(obj.getString("nama_akun"));

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
                Toast.makeText(FacebookSdk.getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                swipe.setRefreshing(false);
            }
        });

        // menambah permintaan ke queue
        AppController.getInstance().addToRequestQueue(jArr);
    }

        @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //mengubah judul pada toolbar
        getActivity().setTitle("Profil");
    }

    /*public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        menu.clear();
    }*/

    public interface OnfragmentInteractionListener {
    }
}