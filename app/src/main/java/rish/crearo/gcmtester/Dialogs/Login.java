package rish.crearo.gcmtester.Dialogs;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import rish.crearo.gcmtester.Database.User;
import rish.crearo.gcmtester.R;

public class Login extends Dialog implements User.UserAuthenticationListener {

    TextView title, title_sub;
    EditText username, pwd;
    Button verify;
    User.UserAuthenticationListener parentActivityListener;

    public Login(Context context, User.UserAuthenticationListener listener) {
        super(context);
        parentActivityListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_login);
        this.setCancelable(false);

        title = (TextView) findViewById(R.id.dialog_user_tv);
        title_sub = (TextView) findViewById(R.id.dialog_user_sub_tv);
        username = (EditText) findViewById(R.id.dialog_user_et);
        pwd = (EditText) findViewById(R.id.dialog_user_pwd_et);
        verify = (Button) findViewById(R.id.dialog_user_verify);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.verifyUser(username.getText().toString(), pwd.getText().toString(), Login.this);
                verify.setText("Verifying ...");
            }
        });
    }

    @Override
    public void AuthenticationResult(boolean auth) {
        if (!auth) {
            parentActivityListener.AuthenticationResult(false);
            verify.setText("Verify");
            pwd.setText("");
            ObjectAnimator
                    .ofFloat(verify, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0)
                    .setDuration(750)
                    .start();
        } else {
            parentActivityListener.AuthenticationResult(true);
            User.setUsername(getContext(), username.getText().toString());
            dismiss();
        }
    }
}