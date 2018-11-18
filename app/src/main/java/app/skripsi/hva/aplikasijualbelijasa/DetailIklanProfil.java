package app.skripsi.hva.aplikasijualbelijasa;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import app.skripsi.hva.aplikasijualbelijasa.R;

import app.skripsi.hva.aplikasijualbelijasa.app.AppController;
import app.skripsi.hva.aplikasijualbelijasa.util.Server;
import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailIklanProfil extends Fragment {


    Button btnEdit,btnDelete;

    int success;
    public static String url_delete = Server.URL + "DeleteIklanRev.php?id_jasa=";

    private static final String TAG = MainActivity.class.getSimpleName();

    String TagID_jasa, TagNama_jasa, TagHarga, TagDeskripsi, TagImage,
            TagAlamat, TagTgl_iklan, TagHp_jasa, TagUsername, TagNama_akun, TagID_akun, TagImageAkun, name, email, no_hp;
    NetworkImageView imageViewJasa, image_pengiklan;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    TextView txt_nama, txt_deskripsi, txt_harga, txt_jarak, txt_alamat, txt_tglIklan, txt_username;

    public DetailIklanProfil() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.detail_iklan_profil, container, false);
        /*// Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) rootView.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);*/

        final Bundle bundle = getArguments();

        TagID_jasa = bundle.getString("ID");
        TagNama_jasa = bundle.getString("nama");
        TagHarga = bundle.getString("harga");
        TagDeskripsi = bundle.getString("deskripsi");
        TagImage = bundle.getString("image");
        TagAlamat = bundle.getString("alamat");
        TagTgl_iklan = bundle.getString("tgl_iklan");
        TagHp_jasa = bundle.getString("hp_jasa");
        TagUsername = bundle.getString("username");
        TagNama_akun = bundle.getString("nama_akun");
        TagID_akun = bundle.getString("id_akun");
        TagImageAkun = bundle.getString("userimage");
        name = bundle.getString("name"); // jika login FB
        email = bundle.getString("email");
        no_hp = bundle.getString("no_hp");

        imageViewJasa = (NetworkImageView) rootView.findViewById(R.id.imageViewJasa);
        txt_nama = (TextView) rootView.findViewById(R.id.txtNamaJasa);
        txt_deskripsi = (TextView) rootView.findViewById(R.id.txtDeskripsi);
        txt_harga = (TextView) rootView.findViewById(R.id.txtHarga);
        txt_jarak = (TextView) rootView.findViewById(R.id.jarak);
        txt_alamat = (TextView) rootView.findViewById(R.id.txtAlamat);
        txt_tglIklan = (TextView) rootView.findViewById(R.id.tgl_iklan);
        txt_username = (TextView) rootView.findViewById(R.id.akunPengiklan);
        btnEdit = (Button) rootView.findViewById(R.id.btnEdit);
        btnDelete = (Button) rootView.findViewById(R.id.btnDelete);

        imageViewJasa.setImageUrl(TagImage, imageLoader);
        txt_nama.setText(TagNama_jasa);
        txt_harga.setText(TagHarga);
        txt_deskripsi.setText(TagDeskripsi);
        txt_alamat.setText(TagAlamat);
        txt_tglIklan.setText(TagTgl_iklan);
        txt_username.setText(TagNama_akun);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundleIklanProfil = new Bundle();
                bundleIklanProfil.putString("ID", TagID_jasa);
                bundleIklanProfil.putString("nama", TagNama_jasa);
                bundleIklanProfil.putString("harga", TagHarga);
                bundleIklanProfil.putString("deskripsi", TagDeskripsi);
                bundleIklanProfil.putString("image", TagImage);
                bundleIklanProfil.putString("alamat", TagAlamat);
                bundleIklanProfil.putString("hp_jasa", TagHp_jasa);
                bundleIklanProfil.putString("id_akun", TagID_akun);
                bundleIklanProfil.putString("userimage", TagImageAkun);
                bundleIklanProfil.putString("username", TagUsername);
                bundleIklanProfil.putString("nama_akun", TagNama_akun);
                bundleIklanProfil.putString("name", name);
                bundleIklanProfil.putString("email", email);
                bundleIklanProfil.putString("no_hp", no_hp);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                EditIklan editIklan = new EditIklan();
                editIklan.setArguments(bundleIklanProfil);

                fragmentTransaction.replace(R.id.content_main, editIklan).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return rootView;
    }

    /*// Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {


        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new TodaysFixturesFragment(), "Today");
        adapter.addFragment(new WeekFixturesFragment(), "Week");
        viewPager.setAdapter(adapter);

    }*/

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void delete(final String idJasa){
        StringRequest stringRequest = new StringRequest(url_delete + idJasa, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);

                try {

                    JSONObject obj = new JSONObject(response);
                    success = obj.getInt("success");

                    if (success == 1){
                        /*Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_LONG).show();*/
                    } else {
                        /*Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_LONG).show();*/
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(FacebookSdk.getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // menambah permintaan ke queue
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //mengubah judul pada toolbar
        getActivity().setTitle("Detail Iklan Anda");
    }

}
