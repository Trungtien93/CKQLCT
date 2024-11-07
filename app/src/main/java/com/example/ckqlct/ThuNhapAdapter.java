package com.example.ckqlct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ThuNhapAdapter extends ArrayAdapter<ThuNhapItem> {
    private LayoutInflater inflater;

    public ThuNhapAdapter(Context context, ArrayList<ThuNhapItem> ChiTieuList) {
        super(context, 0, ChiTieuList);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.income_item_layout, parent, false);
        }
        ThuNhapItem ThuNhapItem = getItem(position);
        // Gán các giá trị vào view
        TextView txtType = convertView.findViewById(R.id.txtTypeIncome);
        TextView txtName = convertView.findViewById(R.id.txtNameIncome);

        txtType.setText(ThuNhapItem.getExpense_type());
        txtName.setText(ThuNhapItem.getExpense_name());
        return convertView;
    }
}
