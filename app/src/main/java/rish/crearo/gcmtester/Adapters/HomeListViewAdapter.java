package rish.crearo.gcmtester.Adapters;

import android.content.Context;
import android.util.Log;
import android.util.Printer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.PriorityQueue;

import rish.crearo.gcmtester.Database.Broadcast;
import rish.crearo.gcmtester.R;
import rish.crearo.gcmtester.Utils.Constants;

/**
 * Created by rish on 10/10/15.
 */
public class HomeListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Broadcast> broadcasts;

    public HomeListViewAdapter(Context context, ArrayList<Broadcast> broadcasts) {
        this.context = context;
        this.broadcasts = broadcasts;
    }

    private static class ViewHolder {
        TextView h_title, h_content, h_date, h_location, h_from;
    }

    @Override
    public int getCount() {
        return broadcasts.size();
    }

    @Override
    public Broadcast getItem(int i) {
        return broadcasts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Broadcast broadcast = getItem(position);
        ViewHolder viewHolder;
//        Typeface type = TheFont.getPrimaryTypeface(getContext());
//        Typeface typeBold = TheFont.getPrimaryTypefaceBold(getContext());

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.element_broadcast, null);
            viewHolder.h_title = (TextView) convertView.findViewById(R.id.listelement_title);
            viewHolder.h_content = (TextView) convertView.findViewById(R.id.listelement_content);
            viewHolder.h_date = (TextView) convertView.findViewById(R.id.listelement_date);
            viewHolder.h_location = (TextView) convertView.findViewById(R.id.listelement_location);
            viewHolder.h_from = (TextView) convertView.findViewById(R.id.listelement_from);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (viewHolder != null) {
            viewHolder.h_content.setText(broadcast.content);
            viewHolder.h_title.setText(broadcast.title);
            viewHolder.h_location.setText(broadcast.location);
            viewHolder.h_from.setText(broadcast.sender);

            Date date = new Date(Long.parseLong(broadcast.dateEvent));
            DateFormat formatter = new SimpleDateFormat("dd-MM");
            viewHolder.h_date.setText(formatter.format(date));

            try {
                formatter = new SimpleDateFormat("HH:mm");
                String time = formatter.format(date);

                long dayEvent = (Long.parseLong(broadcast.dateEvent) / (1000 * 60 * 60 * 24));
                long today = (System.currentTimeMillis() / (1000 * 60 * 60 * 24));
                int diffInDays = (int) (dayEvent - today);
                Log.d("Homelv", "diff in days is " + diffInDays + " | curr = " + System.currentTimeMillis() + " | event = " + broadcast.dateEvent);
                if (diffInDays == -3) {
                    viewHolder.h_date.setText("3 days ago");
                } else if (diffInDays == -2) {
                    viewHolder.h_date.setText("2 days ago");
                } else if (diffInDays == -1) {
                    viewHolder.h_date.setText("Yesterday");
                } else if (diffInDays == 0) {
                    viewHolder.h_date.setText("Today at " + time);
                } else if (diffInDays == 1) {
                    viewHolder.h_date.setText("Tomorrow at " + time);
                } else if (diffInDays == 2) {
                    viewHolder.h_date.setText("In 2 days at " + time);
                } else if (diffInDays == 3) {
                    viewHolder.h_date.setText("In 3 days at " + time);
                }

            } catch (Exception e) {
                viewHolder.h_date.setText(broadcast.dateEvent);
            }
        }
        return convertView;
    }
}