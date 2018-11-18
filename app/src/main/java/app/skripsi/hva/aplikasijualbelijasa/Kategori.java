package app.skripsi.hva.aplikasijualbelijasa;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import app.skripsi.hva.aplikasijualbelijasa.R;

import app.skripsi.hva.aplikasijualbelijasa.adapter.GridKategoriAdapter;
import app.skripsi.hva.aplikasijualbelijasa.app.AppController;
import app.skripsi.hva.aplikasijualbelijasa.data.DataKategori;
import app.skripsi.hva.aplikasijualbelijasa.util.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class Kategori extends Fragment {
    private RecyclerView rvCategory;
    private ArrayList<DataKategori> kategoris;
    private GridKategoriAdapter gridKategoriAdapter;
    public TextView textKategori;
    private static final String url = Server.URL + "Kategori.php";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_kategori, container, false);

        kategoris = new ArrayList<>();
        rvCategory = (RecyclerView) rootView.findViewById(R.id.rv_category);
        rvCategory.setLayoutManager(new LinearLayoutManager(getActivity()));
        textKategori = (TextView) rootView.findViewById(R.id.textKategori);

        callDataKategori();
        return rootView;
    }

    // untuk menampilkan lokasi penjual jasa terdekat dari device pengguna
    private void callDataKategori() {

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
                                /*gridKategoriAdapter = new GridKategoriAdapter(getContext(), kategoris);*/
                                /*rvCategory.setAdapter(gridKategoriAdapter);*/

                                rvCategory.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                                GridKategoriAdapter gridKategoriAdapter = new GridKategoriAdapter(getActivity(), kategoris);
                                /*gridKategoriAdapter.setListKategori(list);*/
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
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //mengubah judul pada toolbar
        getActivity().setTitle("Halaman Kategori");
    }
}
