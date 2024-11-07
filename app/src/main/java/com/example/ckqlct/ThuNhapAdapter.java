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

public class ThuNhapAdapter extends BaseAdapter {
    private Context context;
    private List<ThuNhapItem> thuNhapItems;
    private LayoutInflater inflater;

    public ThuNhapAdapter(Context context, List<ThuNhapItem> thuNhapItems) {
        this.context = context;
        this.thuNhapItems = thuNhapItems;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return thuNhapItems.size();
    }

    @Override
    public Object getItem(int position) {
        return thuNhapItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_transaction_expense, parent, false);
        }

        // Get views from the inflated layout
        TextView txtName = convertView.findViewById(R.id.txtTransactionName1);
        TextView txtTotal = convertView.findViewById(R.id.txtTransactionTotal1);
        TextView txtDate = convertView.findViewById(R.id.txtTransactionDate1);

        // Get the current ThuNhapItem
        ThuNhapItem thuNhapItem = thuNhapItems.get(position);

        // Set values for the TextViews
        if (thuNhapItem != null) {
            txtName.setText(thuNhapItem.getExpense_name());
            txtDate.setText(thuNhapItem.getExpense_date());

            // Parse the expense_total to double if it's a valid number
            String expenseTotalStr = thuNhapItem.getExpense_total();
            double expenseTotal = 0;

            try {
                expenseTotal = Double.parseDouble(expenseTotalStr);
            } catch (NumberFormatException e) {
                e.printStackTrace(); // Handle error if parsing fails (e.g., invalid string)
            }

            // Format and set the total to the TextView
            txtTotal.setText(formatCurrency(expenseTotal));
        }

        return convertView;
    }

    // Utility method to format currency (VND)
    private String formatCurrency(double total) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormat.format(total);
    }
}
