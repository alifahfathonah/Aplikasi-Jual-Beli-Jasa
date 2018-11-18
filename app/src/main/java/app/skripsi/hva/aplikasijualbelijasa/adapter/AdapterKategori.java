package app.skripsi.hva.aplikasijualbelijasa.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import app.skripsi.hva.aplikasijualbelijasa.R;
import app.skripsi.hva.aplikasijualbelijasa.data.DataKategori;

import java.util.List;

public class AdapterKategori extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<DataKategori> item;

    public AdapterKategori(Activity activity, List<DataKategori> item) {
        this.activity = activity;
        this.item = item;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int location) {
        return item.get(location);
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
            convertView = inflater.inflate(R.layout.list_kategori, null);

        TextView kategori = (TextView) convertView.findViewById(R.id.kategori);

        DataKategori dataKategori;
        dataKategori = item.get(position);

        kategori.setText(dataKategori.getKategori());

        return convertView;
    }
}

