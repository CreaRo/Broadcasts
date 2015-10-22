package rish.crearo.gcmtester;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;
import rish.crearo.gcmtester.Database.Broadcast;
import rish.crearo.gcmtester.Database.EachGroup;
import rish.crearo.gcmtester.Database.User;
import rish.crearo.gcmtester.ToServer.NewBroadcast;
import rish.crearo.gcmtester.Utils.TheDate;

public class NewBroadcastActivity extends AppCompatActivity implements NewBroadcast.NewBroadcastCallback, NewBroadcast.BroadcastToListener {

    @Bind(R.id.new_title)
    EditText title;

    @Bind(R.id.new_content)
    EditText content;

    @Bind(R.id.new_location)
    EditText location;

    @Bind(R.id.new_for_spinner)
    Spinner forGroupSpinner;

    @Bind(R.id.new_broadcast)
    Button send;

    ProgressDialog loadingProgressDialog;
    MaterialDialog warningDialog;

    private static final String TAG = "NEWBROADCASTACT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_broadcast);

        ButterKnife.bind(this);

        validateBroadcaster();

        send.setEnabled(false);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewBroadcast.sendBroadcast(new Broadcast(title.getText().toString(), content.getText().toString(), User.getUserName(), forGroupSpinner.getSelectedItem().toString(), TheDate.getDate(), TheDate.getDate(), location.getText().toString()), NewBroadcastActivity.this);
                send.setEnabled(false);
            }
        });
    }

    private void validateBroadcaster() {
        loadingProgressDialog = ProgressDialog.show(NewBroadcastActivity.this, "Validating You", "Are you eligible to send broadcasts?", true);
        loadingProgressDialog.setCancelable(false);
        loadingProgressDialog.show();
        NewBroadcast.groupBroadcastableTo(getApplicationContext(), this);
    }

    @Override
    public void onSuccessBroadcast() {
        Snackbar.make(findViewById(R.id.send_rellay), "Successfully Posted!", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onFailureBroadcast() {
        Snackbar.make(findViewById(R.id.send_rellay), "Failed Broadcasting :(", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onSuccessGroupBroadcastTo(ArrayList<EachGroup> canBroadcastTo) {
        loadingProgressDialog.dismiss();
        if (canBroadcastTo.size() == 0) {
            loadingProgressDialog.dismiss();

            warningDialog = new MaterialDialog(NewBroadcastActivity.this).
                    setTitle("Validation Error!").
                    setMessage("You are not eligible to send broadcasts to any group. You may choose to create a group, where you can broadcast to.").
                    setCanceledOnTouchOutside(false).
                    setPositiveButton("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            warningDialog.dismiss();
                            finish();
                        }
                    });
            warningDialog.show();
        } else {
            send.setEnabled(true);
            setForGroupSpinner(canBroadcastTo);
            loadingProgressDialog.dismiss();
            Snackbar.make(findViewById(R.id.send_rellay), "Broadcast right away!", Snackbar.LENGTH_LONG).show();
            Log.d(TAG, "size of groups broadcastable to = " + canBroadcastTo.size());
        }
    }

    @Override
    public void onFailureGroupBroadcastTo() {
        loadingProgressDialog.dismiss();
        warningDialog = new MaterialDialog(NewBroadcastActivity.this).
                setTitle("Problem in Connecting").
                setMessage("Problem connecting to Server. Are you connected to the Internet?").
                setCanceledOnTouchOutside(false).
                setPositiveButton("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        warningDialog.dismiss();
                        finish();
                    }
                });
        warningDialog.show();
    }


    private void setForGroupSpinner(ArrayList<EachGroup> canBroadcastTo) {
        ArrayList<String> groupNames = new ArrayList<>();
        for (EachGroup group : canBroadcastTo)
            groupNames.add(group.name);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        forGroupSpinner.setAdapter(dataAdapter);
    }
}
