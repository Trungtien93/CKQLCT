package com.example.ckqlct;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;

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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_transaction_income, parent, false);
        }

      //  TextView txtTransactionType = convertView.findViewById(R.id.txtTransactionType);
        TextView txtTransactionName = convertView.findViewById(R.id.txtTransactionName);
        TextView txtTransactionTotal = convertView.findViewById(R.id.txtTransactionTotal);
   //     TextView txtTransactionNote = convertView.findViewById(R.id.txtTransactionNote);
        TextView txtTransactionDate = convertView.findViewById(R.id.txtTransactionDate);

        ImageView imghome = convertView.findViewById(R.id.imgHome);
        Transaction transaction = transactions.get(position);

      //  txtTransactionType.setText(transaction.getType());
        txtTransactionName.setText(transaction.getName());
        txtTransactionTotal.setText(String.valueOf(transaction.getTotal()));
     //   txtTransactionNote.setText(transaction.getNote());
        txtTransactionDate.setText(transaction.getDate());

        // Apply different text colors for the total field based on the transaction type (income or expense)
        if ("income".equalsIgnoreCase(transaction.getType())) {
            txtTransactionTotal.setTextColor(ContextCompat.getColor(context, R.color.red));
            imghome.setBackground(ContextCompat.getDrawable(context,R.drawable.ic_buy_blue));
        } else if ("expense".equalsIgnoreCase(transaction.getType())) {
            txtTransactionTotal.setTextColor(ContextCompat.getColor(context, R.color.green));
            imghome.setBackground(ContextCompat.getDrawable(context,R.drawable.ic_statictical));
        }
        return convertView;
    }

    
}
