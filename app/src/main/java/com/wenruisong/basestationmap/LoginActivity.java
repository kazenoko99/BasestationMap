package com.wenruisong.basestationmap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wenruisong.basestationmap.utils.UserProxy;

import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements UserProxy.ILoginListener,UserProxy.ISignUpListener,UserProxy.IResetPasswordListener {


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private EditText mConfirmPasswordView;
    private View mLoginFormView;
    private UserProxy mUserProxy;
    private TextView mActivityTitle;
    private TextView mActivityAction;
    private Button  mLoginActionButton;
    private TextView mForgetPassword;

    private enum UserOperation{
        LOGIN,REGISTER,RESET_PASSWORD
    }

    UserOperation mUserOperation = UserOperation.LOGIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mActivityTitle = (TextView)findViewById(R.id.activity_title);
        mActivityAction  = (TextView)findViewById(R.id.change_action);
        mForgetPassword  = (TextView)findViewById(R.id.forget_password);
        mUserProxy = new UserProxy(this);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        mPasswordView = (EditText) findViewById(R.id.password);
        mConfirmPasswordView = (EditText) findViewById(R.id.comfirm_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginActionButton = (Button) findViewById(R.id.email_sign_in_button);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mForgetPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserOperation = UserOperation.RESET_PASSWORD;
                updateLayout(mUserOperation);
            }
        });

        mUserOperation = UserOperation.LOGIN;
        updateLayout(mUserOperation);
    }

    private void populateAutoComplete() {
    }


    private void updateLayout(UserOperation op) {
        switch (op) {
            case LOGIN: {
                mActivityTitle.setText("登录");
                mActivityAction.setText("注册");
                mConfirmPasswordView.setVisibility(View.GONE);
                mActivityAction.setOnClickListener(toRegisterClickListener);
                mLoginActionButton.setText("登录");
                mLoginActionButton.setOnClickListener(onLoginClickListener);
                mForgetPassword.setVisibility(View.VISIBLE);
                break;
            }
            case REGISTER: {
                mActivityTitle.setText("注册");
                mActivityAction.setText("登录");
                mConfirmPasswordView.setVisibility(View.VISIBLE);
                mActivityAction.setOnClickListener(toLoginClickListener);
                mLoginActionButton.setText("注册");
                mLoginActionButton.setOnClickListener(onRegisterClickListener);
                mForgetPassword.setVisibility(View.GONE);
                break;
            }
            case RESET_PASSWORD: {
                mActivityTitle.setText("忘记密码");
                mActivityAction.setText("登录");
                mConfirmPasswordView.setVisibility(View.VISIBLE);
                mActivityAction.setOnClickListener(toLoginClickListener);
                mLoginActionButton.setText("修改密码");
                mLoginActionButton.setOnClickListener(onChangePasswordClickListener);
                mForgetPassword.setVisibility(View.GONE);
                break;
            }
        }
    }

    private OnClickListener onLoginClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            attemptLogin();
        }
    };

    private OnClickListener onRegisterClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            attemptRegist();
        }
    };

    private OnClickListener onChangePasswordClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            attemptChangePassword();
        }
    };

    private OnClickListener toRegisterClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mUserOperation = UserOperation.REGISTER;
            updateLayout(mUserOperation);
        }
    };

    private OnClickListener toLoginClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mUserOperation = UserOperation.LOGIN;
            updateLayout(mUserOperation);
        }
    };

    private void attemptLogin() {

        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mUserProxy.setOnLoginListener(this);
            mUserProxy.login(email, password);

        }
    }


    private void attemptRegist() {

        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String rePassword = mConfirmPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(rePassword) && !isRePasswordValid(password,rePassword)) {

            mConfirmPasswordView.setError(getString(R.string.error_invalid_repassword));
            focusView = mPasswordView;
            cancel = true;
        }


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mUserProxy.setOnSignUpListener(this);
            mUserProxy.signUp(mEmailView.getText().toString().trim(),
                    mPasswordView.getText().toString().trim());
        }
    }


    private void attemptChangePassword() {

    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isRePasswordValid(String password,String rePassword) {
       if(rePassword.length() > 4 && rePassword.equals(password))
        return true;
        else return false;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onLoginSuccess() {
        showProgress(false);
        Toast.makeText(this, "登录成功。", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onLoginFailure(String msg) {
        showProgress(false);
        Toast.makeText(this, "登录失败,"+ msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSignUpSuccess() {
        // TODO Auto-generated method stub
        showProgress(false);
        Toast.makeText(this, "注册成功。", Toast.LENGTH_SHORT);
        mUserOperation = UserOperation.LOGIN;
        updateLayout(mUserOperation);
    }

    @Override
    public void onSignUpFailure(String msg) {
        // TODO Auto-generated method stub
        showProgress(false);
        Toast.makeText(this, "注册失败。请确认网络连接后再重试", Toast.LENGTH_SHORT);
    }

    @Override
    public void onResetSuccess() {
        // TODO Auto-generated method stub
        showProgress(false);
        Toast.makeText(this, "请到邮箱修改密码后再登录", Toast.LENGTH_SHORT);
        mUserOperation = UserOperation.LOGIN;
        updateLayout(mUserOperation);
    }

    @Override
    public void onResetFailure(String msg) {
        // TODO Auto-generated method stub
        showProgress(false);
        Toast.makeText(this, "重置密码失败。请确认网络连接后再重试", Toast.LENGTH_SHORT);
    }

}

