package app.skripsi.hva.aplikasijualbelijasa;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import app.skripsi.hva.aplikasijualbelijasa.R;

import app.skripsi.hva.aplikasijualbelijasa.adapter.AdapterKategori;
import app.skripsi.hva.aplikasijualbelijasa.adapter.GridKategoriAdapter;
import app.skripsi.hva.aplikasijualbelijasa.app.AppController;
import app.skripsi.hva.aplikasijualbelijasa.data.DataKategori;
import app.skripsi.hva.aplikasijualbelijasa.util.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class Kategori extends Fragment {
    Button btn;
    private RecyclerView rvCategory;
    private ArrayList<DataKategori> kategoris;
    private GridKategoriAdapter gridKategoriAdapter;
    public TextView textKategori;
    private ArrayList<String> jml = new ArrayList<String>();
    public ArrayList<String> pakai = new ArrayList<String>();
    private ArrayList<String> jam = new ArrayList<String>();
    Spinner spinnerJam;
    ArrayAdapter<String> adapterJam;
    Button btnTampil;
    ListView listRange;
    ArrayList<String> arj = new ArrayList<String>();
    int kpj;

    private static final String url = Server.URL + "Kategori.php";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_kategori, container, false);

        btn = rootView.findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Daftar daftar = new Daftar();

                fragmentTransaction.replace(R.id.content_main, daftar).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        rvCategory = (RecyclerView) rootView.findViewById(R.id.rv_category);

        btnTampil = rootView.findViewById(R.id.btnTampil);
        btnTampil.setEnabled(false);

        listRange = rootView.findViewById(R.id.listRange);

        jam.add("10:00-12:00");
        jam.add("13:00-15:00");

        spinnerJam = rootView.findViewById(R.id.spinnerJam);
        adapterJam = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                jam);

        spinnerJam.setAdapter(adapterJam);

        spinnerJam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arj.clear();
                jml.clear();
                String jam = spinnerJam.getSelectedItem().toString();

                String splitJam[] = jam.split("-");
                String jam1 = splitJam[0]; /*20:00*/
                String jam2 = splitJam[1]; /*22:00*/

                kpj = 1;

                // Range
                String[] jamawal = jam1.split(":");
                String jamawal1 = jamawal[0]; /*20*/ String nA = "1";
                String jamawal2 = jamawal[1]; /*00*/

                String[] jamakhir = jam2.split(":");
                String jamselesai1 = jamakhir[0]; /*22*/ String nK;
                String jamselesai2 = jamakhir[1]; /*00*/

                int jamp1 = Integer.parseInt(jamawal1); /*20*/ int aW = Integer.parseInt(nA);
                int jamp3 = (Integer.parseInt(jamselesai1) - jamp1); /*22 - 20 = 2*/

                int jamrange; int noRange;

                String jamranges1; String nRange1;
                String jamranges2; String noRange2;

                for (int lit = 1; lit <= /*2*/jamp3; lit++) {
                    jamrange = jamp1 /*20*/ + 1;
                    noRange = aW + kpj - 1;

                    if (jamp1 /*20*/ > 24) {
                        jamp1 = jamp1 - 24;
                    } else if (jamp1 == 24) {
                        jamp1 = 00;
                    }

                    nRange1 = String.valueOf(aW);
                    jamranges1 = /*20*/String.valueOf(jamp1);
                    if ("0".equals(jamranges1)) {
                        jamranges1 = "00";
                    }

                    if (jamrange > 24) {
                        jamrange = jamrange - 24;
                    } else if (jamrange == 24) {
                        jamrange = 00;
                    }

                    noRange2 = String.valueOf(noRange);

                    jamranges2 = /*21*/ String.valueOf(jamrange);
                    if ("0".equals(jamranges2)) {
                        jamranges2 = "00";
                    }


                    String range1 = (jamranges1 /*20*/ + ":" + /*00*/ jamawal2);
                    String range2 = (jamranges2 /*21*/ + ":" + /*00*/ jamselesai2);

                    aW+=kpj;
                    jamp1++;

                    if (noRange2.equals(nRange1)){
                        arj.add( "no." + nRange1 + " ("+ range1 + " - " + range2+")");
                    } else {
                        arj.add( "no." + nRange1 + "-" + noRange2 + " ("+ range1 + " - " + range2+")");
                    }


                }

                for (int i = 1; i <= 20; i++){ //button
                    pakai.add("1");
                    jml.add(String.valueOf(i));
                }

                btnTampil.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnTampil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ArrayAdapter<String> adapterRange = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, arj);
                listRange.setAdapter(adapterRange);

                rvCategory.setLayoutManager(new GridLayoutManager(getActivity(), kpj));
                GridKategoriAdapter gridKategoriAdapter = new GridKategoriAdapter(getActivity(), jml, pakai);
                rvCategory.setAdapter(gridKategoriAdapter);
            }
        });


        /*kategoris = new ArrayList<>();
        rvCategory = (RecyclerView) rootView.findViewById(R.id.rv_category);
        rvCategory.setLayoutManager(new LinearLayoutManager(getActivity()));*/

        /*callDataKategori();*/
        return rootView;
    }

    // untuk menampilkan lokasi penjual jasa terdekat dari device pengguna
    /*private void callDataKategori() {

        JsonArrayRequest jArr = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(AppController.TAG, response.toString());

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                DataKategori dataKategori = new DataKategori();

                                dataKategori.setId(obj.getString("id_kategori"));

                                dataKategori.setKategori(obj.getString("nama_kategori"));

                                dataKategori.setImage_kategori(obj.getString("image_kategori"));

                                kategoris.add(dataKategori);
                                *//*gridKategoriAdapter = new GridKategoriAdapter(getContext(), kategoris);*//*
                                *//*rvCategory.setAdapter(gridKategoriAdapter);*//*

                                rvCategory.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                                GridKategoriAdapter gridKategoriAdapter = new GridKategoriAdapter(getActivity(), );
                                *//*gridKategoriAdapter.setListKategori(list);*//*
                                rvCategory.setAdapter(gridKategoriAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        // memberitahu adapter jika ada perubahan data

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(AppController.TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // menambah permintaan ke queue
        AppController.getInstance().addToRequestQueue(jArr);
    }*/

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //mengubah judul pada toolbar
        getActivity().setTitle("Halaman Kategori");
    }
}
