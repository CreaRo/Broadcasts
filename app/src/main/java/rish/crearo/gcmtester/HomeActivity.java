package rish.crearo.gcmtester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.orm.StringUtil;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import rish.crearo.gcmtester.Adapters.HomeListViewAdapter;
import rish.crearo.gcmtester.Database.Broadcast;
import rish.crearo.gcmtester.Utils.Constants;

public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.home_listview)
    ListView listView;

    @Bind(R.id.home_new_broadcast_fab)
    FloatingActionButton floatingActionButton;

    HomeListViewAdapter adapter;
    ArrayList<Broadcast> broadcasts;

    private BroadcastReceiver mRefreshAdapterReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.home_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Broadcasts");

        ButterKnife.bind(this);

        broadcasts = (ArrayList<Broadcast>) Select.from(Broadcast.class).orderBy(StringUtil.toSQLName("datePost")).list();
        Collections.reverse(broadcasts);
        adapter = new HomeListViewAdapter(getApplicationContext(), broadcasts);

        listView.setAdapter(adapter);

        mRefreshAdapterReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                broadcasts = (ArrayList<Broadcast>) Select.from(Broadcast.class).orderBy(StringUtil.toSQLName("datePost")).list();
                Collections.reverse(broadcasts);

                adapter.notifyDataSetChanged();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshAdapterReceiver, new IntentFilter(Constants.LOCALBR_REFRESH_ADAPTERS));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, NewBroadcastActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshAdapterReceiver, new IntentFilter(Constants.LOCALBR_REFRESH_ADAPTERS));
    }

    @Override
    protected void onPause() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRefreshAdapterReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_home, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home_groups) {
            startActivity(new Intent(HomeActivity.this, GroupsActivity.class));
        } else if (item.getItemId() == R.id.home_settings) {
        }
        return (super.onOptionsItemSelected(item));
    }
}