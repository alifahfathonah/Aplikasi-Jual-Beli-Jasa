package app.skripsi.hva.aplikasijualbelijasa;


import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;


/**
 * A simple {@link Fragment} subclass.
 */
public class Daftar extends Fragment {

    private static final int NUM_ROWS = 2;
    private static final int NUM_COLS = 10;

    TableLayout table;


    public Daftar() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daftar, container, false);

        table = (TableLayout) view.findViewById(R.id.tableForButtons);


        populateButtons();
    return view;
    }

    private void populateButtons() {

        for (int row = 1; row <= NUM_ROWS; row++){
            TableRow tableRow = new TableRow(getActivity());
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    1.0f));
            table.addView(tableRow);

            for (int col = 1; col<= NUM_COLS; col++){
                Button button = new Button(getActivity());
                button.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        1.0f));
                tableRow.addView(button);

                button.setText("" + col);

            }
        }
    }

    public interface OnfragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
