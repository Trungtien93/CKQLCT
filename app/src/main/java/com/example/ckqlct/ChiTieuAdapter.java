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

public class ChiTieuAdapter extends ArrayAdapter<ChiTieuItem> {
    private LayoutInflater inflater;

    public ChiTieuAdapter(Context context, ArrayList<ChiTieuItem> ChiTieuList) {
        super(context, 0, ChiTieuList);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.share_item_layout, parent, false);
        }
        ChiTieuItem chiTieuItem = getItem(position);
        // Gán các giá trị vào view
        TextView txtType = convertView.findViewById(R.id.txtTypeIncome);
        TextView txtName = convertView.findViewById(R.id.txtNameIncome);

        txtType.setText(chiTieuItem.getIncome_type());
        txtName.setText(chiTieuItem.getIncome_name());
        return convertView;
    }
}
