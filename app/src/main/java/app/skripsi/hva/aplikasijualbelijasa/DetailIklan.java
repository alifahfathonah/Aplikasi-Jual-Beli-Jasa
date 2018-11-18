package app.skripsi.hva.aplikasijualbelijasa;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import app.skripsi.hva.aplikasijualbelijasa.R;

import app.skripsi.hva.aplikasijualbelijasa.app.AppController;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailIklan extends Fragment implements View.OnClickListener {

    String TagID_jasa, TagNama_jasa, TagHarga, TagDeskripsi, TagImage, TagAlamat, TagHp_jasa, TagUsername,
            TagImagePengiklan, TagNama_akun, TagTglIklan;
    Double TagLatitude, TagLongitude, TagJarak;
    NetworkImageView imageViewJasa, image_pengiklan;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    TextView txt_nama, txt_deskripsi, txt_harga, txt_jarak, tgl_iklan, txt_alamat, txt_username;
    Button buttonDirection, buttonWA;

    public DetailIklan() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail_iklan, container, false);

        Bundle bundle = getArguments();

        TagID_jasa = bundle.getString("ID");
        TagNama_jasa = bundle.getString("nama_jasa");
        TagHarga = bundle.getString("harga");
        TagDeskripsi = bundle.getString("deskripsi");
        TagJarak = bundle.getDouble("jarak");
        TagImage = bundle.getString("image");
        TagAlamat = bundle.getString("alamat");
        TagHp_jasa = bundle.getString("hp_jasa");
        TagUsername = bundle.getString("username");
        TagLatitude = bundle.getDouble("latitude");
        TagLongitude = bundle.getDouble("longitude");
        TagNama_akun = bundle.getString("nama_akun");
        TagImagePengiklan = bundle.getString("userimage");
        TagTglIklan = bundle.getString("tgl_iklan");


        imageViewJasa = (NetworkImageView) rootView.findViewById(R.id.imageViewJasa);
        txt_nama = (TextView) rootView.findViewById(R.id.txtNamaJasa);
        txt_deskripsi = (TextView) rootView.findViewById(R.id.txtDeskripsi);
        txt_harga = (TextView) rootView.findViewById(R.id.txtHarga);
        txt_jarak = (TextView) rootView.findViewById(R.id.jarak);
        txt_alamat = (TextView) rootView.findViewById(R.id.txtAlamat);
        txt_username = (TextView) rootView.findViewById(R.id.akunPengiklan);
        buttonDirection = (Button) rootView.findViewById(R.id.buttonDirection);
        buttonWA = (Button) rootView.findViewById(R.id.btnWA);
        image_pengiklan = (NetworkImageView) rootView.findViewById(R.id.image_pengiklan);
        tgl_iklan = (TextView) rootView.findViewById(R.id.tgl_iklan);


        buttonDirection.setOnClickListener(this);
        buttonWA.setOnClickListener(this);

        imageViewJasa.setImageUrl(TagImage, imageLoader);
        txt_nama.setText(TagNama_jasa);
        txt_harga.setText("Rp " + TagHarga);
        txt_deskripsi.setText(TagDeskripsi);
        txt_alamat.setText(TagAlamat);
        txt_jarak.setText(TagJarak + "Km");
        txt_username.setText(TagNama_akun);
        image_pengiklan.setImageUrl(TagImagePengiklan, imageLoader);
        tgl_iklan.setText(TagTglIklan);


        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //mengubah judul pada toolbar
        getActivity().setTitle("Detail Iklan");
    }

    @Override
    public void onClick(View v) {
        if (v == buttonDirection){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Uri gmmIntentUri = Uri.parse("http://maps.google.com/?daddr=" + TagLatitude + "," + TagLongitude);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }, 1000);
        } else if (v == buttonWA) {
            PackageManager packageManager = getActivity().getPackageManager();
            try {
                Uri uri = Uri.parse("smsto:" + TagHp_jasa);
                Intent WAintent = new Intent(Intent.ACTION_SENDTO, uri);
                PackageInfo info=packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                WAintent.setPackage("com.whatsapp");
                startActivity(WAintent);

            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(getActivity(), "WhatsApp belum dipasang", Toast.LENGTH_LONG).show();
            }

        }
    }
}