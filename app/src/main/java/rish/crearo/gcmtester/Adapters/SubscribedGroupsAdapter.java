package rish.crearo.gcmtester.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import rish.crearo.gcmtester.Database.EachGroup;
import rish.crearo.gcmtester.R;

/**
 * Created by rish on 14/10/15.
 */
public class SubscribedGroupsAdapter extends BaseAdapter {

    Context context;
    ArrayList<EachGroup> subscribedGroups;

    public SubscribedGroupsAdapter(Context context, ArrayList<EachGroup> subscribedGroups) {
        this.context = context;
        this.subscribedGroups = subscribedGroups;
    }

    @Override
    public int getCount() {
        return subscribedGroups.size();
    }

    @Override
    public EachGroup getItem(int i) {
        return subscribedGroups.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        EachGroup subscribedGroup = getItem(i);
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.element_subscribed_group, null);
            new ViewHolder(convertView);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();

        holder.checkBox.setChecked(subscribedGroup.subscribed);
        holder.checkBox.setText(subscribedGroup.name);

        return convertView;
    }

    class ViewHolder {
        CheckBox checkBox;

        public ViewHolder(View view) {
            checkBox = (CheckBox) view.findViewById(R.id.element_subgroup_checkbox);
            view.setTag(this);
        }
    }
}
