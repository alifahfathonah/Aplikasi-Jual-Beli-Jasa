package app.skripsi.hva.aplikasijualbelijasa.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import app.skripsi.hva.aplikasijualbelijasa.Iklan;

import app.skripsi.hva.aplikasijualbelijasa.Kategori;
import app.skripsi.hva.aplikasijualbelijasa.R;
import app.skripsi.hva.aplikasijualbelijasa.app.AppController;
import app.skripsi.hva.aplikasijualbelijasa.data.DataKategori;

import java.util.ArrayList;

public class GridKategoriAdapter extends RecyclerView.Adapter<GridKategoriAdapter.ViewHolder>{
    private Context context;
    /*private ArrayList<DataKategori> listKategori;*/
    ArrayList<Integer> jumlah;
    private ImageLoader imageLoader;

    public GridKategoriAdapter(Context context, ArrayList<Integer> jumlah){
        this.jumlah = jumlah;
        this.context = context;
    }

    @Override
    public GridKategoriAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_kategori, null);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){

            holder.btnK.setText(String.valueOf(jumlah.get(position).toString()));

        Kategori kategori = new Kategori();


        final String nmt = holder.btnK.getText().toString();
            holder.btnK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(v.getContext(), "Diklik" + nmt, Toast.LENGTH_LONG).show();
                }
            });


        holder.btnK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        /*DataKategori dataKategori = listKategori.get(position);

        holder.textKategori.setText(String.valueOf(listKategori.get(position).getKategori()));
        holder.thumbNail.setImageUrl(dataKategori.getImage_kategori(), imageLoader);

        holder.thumbNail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                String kategori = listKategori.get(position).getKategori();
                Bundle bundleKategori = new Bundle();
                bundleKategori.putString("kategori", kategori);
                Iklan iklan = new Iklan();
                iklan.setArguments(bundleKategori);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, iklan).addToBackStack(null).commit();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return jumlah.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button btnK;

        public ViewHolder(View itemView) {
            super(itemView);
            btnK = (Button) itemView.findViewById(R.id.btn);

            /*if (imageLoader == null)
                imageLoader = AppController.getInstance().getImageLoader();
            thumbNail = (NetworkImageView) itemView
                    .findViewById(R.id.img_item_photo);*/
        }
    }

    /*private Context context;
    private ArrayList<DataKategori> listKategori;

    private ArrayList<DataKategori> getListKategori() {
        return listKategori;
    }

    public void setListKategori(ArrayList<DataKategori> listKategori){
        this.listKategori = listKategori;
    }

    public GridKategoriAdapter(Context context){
        this.context = context;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_kategori, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position){
        Glide.with(context)
                .load(getListKategori().get(position).getImage_kategori())
                .override(120, 120)
                .into(holder.imgPhoto);
    }

    @Override
    public int getItemCount(){
        return getListKategori().size();
    }

    class GridViewHolder extends RecyclerView.ViewHolder{
        ImageView imgPhoto;
        GridViewHolder(View itemView){
            super(itemView);
            imgPhoto = (ImageView)itemView.findViewById(R.id.img_item_photo);
        }
    }*/

    /*===========*/

    /*private Activity activity;
    private LayoutInflater inflater;
    private List<DataKategori> getKategoriItem;
    ImageLoader imageLoader;

    public GridKategoriAdapter(Activity activity, List<DataKategori> getKategoriItem) {
        this.activity = activity;
        this.getKategoriItem = getKategoriItem;
    }

    @Override
    public int getCount() {
        return getKategoriItem.size();
    }

    @Override
    public Object getItem(int location) {
        return getKategoriItem.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.grid_kategori, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.imageViewJasa);
        TextView nama = (TextView) convertView.findViewById(R.id.textKategori);

        DataKategori dataKategori = getKategoriItem.get(position);

        thumbNail.setImageUrl(dataKategori.getImage_kategori(), imageLoader);
        nama.setText(dataKategori.getKategori());

        return convertView;
    }*/

}
