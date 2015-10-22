package rish.crearo.gcmtester;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;
import rish.crearo.gcmtester.Adapters.SubscribedGroupsAdapter;
import rish.crearo.gcmtester.Database.EachGroup;
import rish.crearo.gcmtester.Database.User;
import rish.crearo.gcmtester.ToServer.NewBroadcast;
import rish.crearo.gcmtester.ToServer.NewGroup;
import rish.crearo.gcmtester.Utils.TheDate;

public class GroupsActivity extends AppCompatActivity implements NewGroup.NewGroupCallback, NewBroadcast.BroadcastToListener {

    @Bind(R.id.new_group_name)
    EditText name;

    @Bind(R.id.new_group_okay)
    Button okaybtn;

    @Bind(R.id.groups_broadcastable_ll)
    ListView broadcastableListView;

    @Bind(R.id.groups_subscribed_title)
    LinearLayout subscribedGroupTitle;

    @Bind(R.id.groups_subscribed_ll)
    ListView subscribedGroupListView;

    ProgressDialog loadingProgressDialog;
    MaterialDialog warningDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        ButterKnife.bind(this);

        loadingProgressDialog = ProgressDialog.show(GroupsActivity.this, "Validating You", "Are you eligible to send broadcasts?", true);
        loadingProgressDialog.setCancelable(false);
        loadingProgressDialog.show();
        NewBroadcast.groupBroadcastableTo(getApplicationContext(), this);

        subscribedGroupTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subscribedGroupListView.getVisibility() == View.VISIBLE)
                    subscribedGroupListView.setVisibility(View.GONE);
                else
                    subscribedGroupListView.setVisibility(View.VISIBLE);
            }
        });

        ArrayList<EachGroup> subscribedGroups = (ArrayList<EachGroup>) Select.from(EachGroup.class).list();

        Log.d("GroupsAct", "size of subgroups = " + subscribedGroups.size());

        SubscribedGroupsAdapter adapter = new SubscribedGroupsAdapter(getApplicationContext(), subscribedGroups);
        subscribedGroupListView.setAdapter(adapter);

        okaybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewGroup.createGroup(new EachGroup(name.getText().toString(), User.getUserName(), TheDate.getDate(), true), GroupsActivity.this);
            }
        });
    }

    @Override
    public void onSuccessGroup() {
        Snackbar.make(findViewById(R.id.new_group_rellay), "Successfully Created!", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onFailureGroup() {
        Snackbar.make(findViewById(R.id.new_group_rellay), "Couldn't be created :(", Snackbar.LENGTH_LONG).show();
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