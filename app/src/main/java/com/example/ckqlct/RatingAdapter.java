package com.example.ckqlct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RatingAdapter extends ArrayAdapter<RatingItem> {

    private LayoutInflater inflater;

    public RatingAdapter(Context context, ArrayList<RatingItem> ratingList) {
        super(context, 0, ratingList);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.rating_item, parent, false);
        }

        // Lấy phần tử hiện tại
        RatingItem rating = getItem(position);

        // Gán các giá trị vào view
        TextView tvUserName = convertView.findViewById(R.id.tvUserName);
        TextView tvTime = convertView.findViewById(R.id.tvTime);
        TextView tvNote = convertView.findViewById(R.id.tvNote);
        TextView tvTimeNow = convertView.findViewById(R.id.tvTimeNow);

        tvUserName.setText(rating.getUserName());
        tvNote.setText(rating.getNote());
        tvTimeNow.setText(rating.getTime());

        // Tính toán thời gian "X ngày trước"
        String relativeTime = calculateDaysAgo(rating.getTime());
        tvTime.setText(relativeTime);

        return convertView;
    }

    // Phương thức tính "X ngày trước" dựa vào thời gian đánh giá
    private String calculateDaysAgo(String ratingTime) {
        // Ensure ratingTime is not null or empty
        if (ratingTime == null || ratingTime.isEmpty()) {
            return "Không xác định";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            // Parse the rating time
            Date ratingDate = sdf.parse(ratingTime);

            if (ratingDate == null) {
                return "Không xác định";
            }

            // Calculate the difference in milliseconds
            long diffInMillis = new Date().getTime() - ratingDate.getTime();

            // Calculate days difference
            long daysAgo = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            // Return the result
            if (daysAgo == 0) {
                return "Hôm nay";
            } else {
                return daysAgo + " ngày trước";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "Không xác định";
        }
    }
}