package com.ricardorainha.mustache.view;

import android.content.Intent;

import com.google.android.material.snackbar.Snackbar;
import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.authentication.AuthManager;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginActivityViewModel extends ViewModel implements AuthManager.AuthStateChange {

    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();
    private ObservableField<Boolean> loading = new ObservableField<>();
    private ObservableField<Boolean> signedIn = new ObservableField<>();
    private ObservableField<Boolean> signedUp = new ObservableField<>();
    private ObservableField<Boolean> passwordReset = new ObservableField<>();
    private int messageId = -1;
    private String message = null;

    public LoginActivityViewModel() {
        loading.set(false);
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public void setEmail(MutableLiveData<String> email) {
        this.email = email;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(MutableLiveData<String> password) {
        this.password = password;
    }

    public ObservableField<Boolean> getLoading() {
        return loading;
    }

    public ObservableField<Boolean> getSignedIn() {
        return signedIn;
    }

    public ObservableField<Boolean> getSignedUp() {
        return signedUp;
    }

    public ObservableField<Boolean> getPasswordReset() {
        return passwordReset;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public void doSignIn() {
        loading.set(true);
        AuthManager.getInstance().signInWithEmailAndPassword(email.getValue(), password.getValue(), this);
    }

    public void doSignUp() {
        loading.set(true);
        AuthManager.getInstance().createUserWithEmailAndPassword(email.getValue(), password.getValue(), this);
    }

    public void resetPassword() {
        loading.set(true);
        AuthManager.getInstance().resetPassword(email.getValue(), this);
    }

    @Override
    public void onUserCreateFinished(int resultCode, String message) {
        loading.set(false);

        this.messageId = R.string.user_create_unknown_error_message;
        this.message = message;

        switch (resultCode) {
            case AuthManager.RESULT_USER_CREATE_FAIL:
                messageId = R.string.user_create_fail_message;
                break;
            case AuthManager.RESULT_USER_CREATE_EMAIL_SENT_FAIL:
                messageId = R.string.user_create_email_sent_fail_message;
                break;
            case AuthManager.RESULT_USER_CREATE_EMAIL_SENT_SUCCESS:
                messageId = R.string.user_create_email_sent_success_message;
                break;
        }

        signedUp.set(true);
    }

    @Override
    public void onUserSignInFinished(int resultCode, String message) {
        loading.set(false);

        if (resultCode == AuthManager.RESULT_USER_SIGN_IN_SUCCESS) {
            signedIn.set(true);
        }
        else {
            this.messageId = R.string.user_login_unknown_error_message;
            this.message = message;

            switch (resultCode) {
                case AuthManager.RESULT_USER_SIGN_IN_FAIL:
                    messageId = R.string.user_login_fail_message;
                    break;
                case AuthManager.RESULT_USER_SIGN_IN_NEED_VERIFY_EMAIL:
                    messageId = R.string.user_login_email_not_verified_message;
                    break;
            }

            signedIn.set(false);
        }
    }

    @Override
    public void onResetPasswordFinished(boolean success) {
        loading.set(false);
        passwordReset.set(success);
    }
}
