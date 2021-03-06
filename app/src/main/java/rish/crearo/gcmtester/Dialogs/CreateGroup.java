package rish.crearo.gcmtester.Dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import rish.crearo.gcmtester.Database.EachGroup;
import rish.crearo.gcmtester.Database.User;
import rish.crearo.gcmtester.R;
import rish.crearo.gcmtester.ToServer.NewGroup;
import rish.crearo.gcmtester.Utils.TheDate;

/**
 * Created by rish on 23/10/15.
 */
public class CreateGroup extends Dialog implements NewGroup.NewGroupCallback {

    @Bind(R.id.dialog_create_btn)
    Button createBtn;

    @Bind(R.id.dialog_new_group_name)
    EditText nameGroup;

    Context context;

    ProgressDialog progDailog;

    public CreateGroup(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_create_new_group);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ButterKnife.bind(this);
        setCancelable(true);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewGroup.createGroup(new EachGroup(nameGroup.getText().toString(), User.getUsername(getContext()), TheDate.getDate(), true), CreateGroup.this);
                createBtn.setEnabled(false);
                progDailog = ProgressDialog.show(context, "Creating Group", "", true);
            }
        });
    }

    @Override
    public void onSuccessGroup() {
        Snackbar.make(findViewById(R.id.dialog_create_group_rellay), "Successfully Created!", Snackbar.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 1500);
        progDailog.dismiss();
    }

    @Override
    public void onFailureGroup(String message) {
        Snackbar.make(findViewById(R.id.dialog_create_group_rellay), "Couldn't be created. \n" + message, Snackbar.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 1500);
        progDailog.dismiss();
    }
}
