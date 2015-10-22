package rish.crearo.gcmtester;

import android.app.ProgressDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.orm.StringUtil;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;
import rish.crearo.gcmtester.Adapters.SubscribedGroupsAdapter;
import rish.crearo.gcmtester.Database.EachGroup;
import rish.crearo.gcmtester.Dialogs.CreateGroup;
import rish.crearo.gcmtester.ToServer.NewBroadcast;

public class GroupsActivity extends AppCompatActivity implements NewBroadcast.BroadcastToListener {

    @Bind(R.id.new_group_btn_fab)
    FloatingActionButton okaybtn;

    @Bind(R.id.groups_broadcastable_ll)
    ListView broadcastableListView;

    @Bind(R.id.groups_subscribed_title)
    LinearLayout subscribedGroupTitle;

    @Bind(R.id.groups_broadcastable_title)
    LinearLayout groups_broadcastable_title;

    @Bind(R.id.groups_subscribed_ll)
    ListView subscribedGroupListView;

    ProgressDialog loadingProgressDialog;
    MaterialDialog warningDialog;

    @Bind(R.id.groups_tool_bar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Groups");

        loadingProgressDialog = ProgressDialog.show(GroupsActivity.this, "Validating You", "Are you eligible to send broadcasts?", true);
        loadingProgressDialog.setCancelable(false);
        loadingProgressDialog.show();
        NewBroadcast.groupBroadcastableTo(getApplicationContext(), this);

        subscribedGroupListView.setVisibility(View.GONE);
        broadcastableListView.setVisibility(View.GONE);

        subscribedGroupTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subscribedGroupListView.getVisibility() == View.VISIBLE)
                    subscribedGroupListView.setVisibility(View.GONE);
                else
                    subscribedGroupListView.setVisibility(View.VISIBLE);
            }
        });

        groups_broadcastable_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (broadcastableListView.getVisibility() == View.VISIBLE)
                    broadcastableListView.setVisibility(View.GONE);
                else
                    broadcastableListView.setVisibility(View.VISIBLE);
            }
        });

        ArrayList<EachGroup> subscribedGroups = (ArrayList<EachGroup>) Select.from(EachGroup.class).orderBy(StringUtil.toSQLName("name")).list();

        SubscribedGroupsAdapter adapter = new SubscribedGroupsAdapter(getApplicationContext(), subscribedGroups);
        subscribedGroupListView.setAdapter(adapter);

        okaybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CreateGroup(GroupsActivity.this).show();
            }
        });
    }

    @Override
    public void onSuccessGroupBroadcastTo(ArrayList<EachGroup> canBroadcastTo) {
        loadingProgressDialog.dismiss();
        if (canBroadcastTo.size() == 0) {
            loadingProgressDialog.dismiss();

        } else {
            loadingProgressDialog.dismiss();

            ArrayList<String> groupNames = new ArrayList<>();
            for (EachGroup group : canBroadcastTo)
                groupNames.add(group.name);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, groupNames);
            broadcastableListView.setAdapter(adapter);
        }
    }

    @Override
    public void onFailureGroupBroadcastTo() {
        loadingProgressDialog.dismiss();
        warningDialog = new MaterialDialog(GroupsActivity.this).
                setTitle("Problem in Connecting").
                setMessage("You can view and change subscribed groups, but not create and view groups you can broadcast to").
                setCanceledOnTouchOutside(false).
                setPositiveButton("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        warningDialog.dismiss();
                    }
                });
        warningDialog.show();
        List emptyList = new ArrayList<>();
        emptyList.add("Network Unavailable");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, emptyList);
        broadcastableListView.setAdapter(adapter);
    }
}