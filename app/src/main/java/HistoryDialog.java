package com.example.smartwastemanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.smartwastemanagement.R;

public class HistoryDialog extends DialogFragment {
    private Button confirmButton;
    private Spinner monthSpinner;
    private EditText yearEditText;
    private String selectedMonth = null;
    private int selectedYear = 0;
    public static int historyflag = 0;
    static int fyear = 0;
    static String fmonth = null;

    // Listener interface
    public interface OnDismissListener {
        void onDismiss();
    }

    private OnDismissListener dismissListener;

    // Method to set the listener
    public void setOnDismissListener(OnDismissListener listener) {
        this.dismissListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_dialog, container, false);

        monthSpinner = view.findViewById(R.id.month_spinner);
        yearEditText = view.findViewById(R.id.historyyear);
        confirmButton = view.findViewById(R.id.confirmButton);

        // Month options
        String[] months = new String[]{
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, months);
        monthSpinner.setAdapter(adapter);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = months[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fyear = Integer.parseInt(String.valueOf(yearEditText.getText()));
                fmonth = selectedMonth;
                historyflag = 1;
                dismiss();
                if (dismissListener != null) {
                    dismissListener.onDismiss(); // Notify listener
                }
            }
        });

        return view;
    }

    public String getMonthHistory() {
        return fmonth;
    }

    public int getYearHistory() {
        return fyear;
    }


}
