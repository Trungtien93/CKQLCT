package com.example.ckqlct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends BaseAdapter {
    private Context context;
    private List<Transaction> transactions;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int position) {
        return transactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_transaction, parent, false);
        }

      //  TextView txtTransactionType = convertView.findViewById(R.id.txtTransactionType);
        TextView txtTransactionName = convertView.findViewById(R.id.txtTransactionName);
        TextView txtTransactionTotal = convertView.findViewById(R.id.txtTransactionTotal);
   //     TextView txtTransactionNote = convertView.findViewById(R.id.txtTransactionNote);
        TextView txtTransactionDate = convertView.findViewById(R.id.txtTransactionDate);

        Transaction transaction = transactions.get(position);

      //  txtTransactionType.setText(transaction.getType());
        txtTransactionName.setText(transaction.getName());
        txtTransactionTotal.setText(String.valueOf(transaction.getTotal()));
     //   txtTransactionNote.setText(transaction.getNote());
        txtTransactionDate.setText(transaction.getDate());

        return convertView;
    }
    
}
