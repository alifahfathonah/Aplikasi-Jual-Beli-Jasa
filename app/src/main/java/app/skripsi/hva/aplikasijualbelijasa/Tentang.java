package app.skripsi.hva.aplikasijualbelijasa;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.skripsi.hva.aplikasijualbelijasa.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tentang extends Fragment {
    TextView versi;


    public Tentang() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the list_iklan for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_tentang, container, false);
        versi = (TextView)rootView.findViewById(R.id.textVersi);

        versi.setText("versi: " + BuildConfig.VERSION_NAME);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //mengubah judul pada toolbar
        getActivity().setTitle("Tentang");
    }
    public interface OnfragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
