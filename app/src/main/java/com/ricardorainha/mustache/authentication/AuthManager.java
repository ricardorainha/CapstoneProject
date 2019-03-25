package com.ricardorainha.mustache.authentication;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.model.User;
import com.ricardorainha.mustache.utils.FirebaseUtils;

public class AuthManager {
    public static final int RESULT_USER_CREATE_FAIL = 0;
    public static final int RESULT_USER_CREATE_EMAIL_SENT_FAIL = 1;
    public static final int RESULT_USER_CREATE_EMAIL_SENT_SUCCESS = 2;
    public static final int RESULT_USER_SIGN_IN_FAIL = 3;
    public static final int RESULT_USER_SIGN_IN_NEED_VERIFY_EMAIL = 4;
    public static final int RESULT_USER_SIGN_IN_SUCCESS = 5;

    private static final AuthManager ourInstance = new AuthManager();
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private GoogleSignInClient gsiClient = null;
    private GoogleSignInAccount googleAccount;

    public static AuthManager getInstance() {
        return ourInstance;
    }

    private AuthManager() {
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
    }

    public FirebaseUser getUser() {
        return firebaseUser;
    }

    public GoogleSignInClient getGsiClient(Context context) {
        if (gsiClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.oauth_web_client_id))
                    .requestEmail()
                    .build();

            gsiClient = GoogleSignIn.getClient(context, gso);
        }

        return gsiClient;
    }

    public boolean isUserLoggedIn() {
        return (firebaseUser != null);
    }

    public boolean needToLogin() {
        return !(isUserLoggedIn() && firebaseUser.isEmailVerified());
    }

    public void createUserWithEmailAndPassword(String email, String password, final AuthStateChange listener) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    firebaseUser = auth.getCurrentUser();
                    FirebaseUtils.createUserInfo(User.fromFirebaseUser(firebaseUser));
                    firebaseUser.sendEmailVerification().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            listener.onUserCreateFinished(RESULT_USER_CREATE_EMAIL_SENT_SUCCESS, null);
                        }
                        else {
                            listener.onUserCreateFinished(RESULT_USER_CREATE_EMAIL_SENT_FAIL, task1.getException().getMessage());
                        }
                    });
                }
                else {
                    listener.onUserCreateFinished(RESULT_USER_CREATE_FAIL, task.getException().getMessage());
                }
            });
    }

    public void signInWithEmailAndPassword(String email, String password, final AuthStateChange listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        firebaseUser = auth.getCurrentUser();
                        if (firebaseUser.isEmailVerified()) {
                            listener.onUserSignInFinished(RESULT_USER_SIGN_IN_SUCCESS, null);
                        }
                        else {
                            listener.onUserSignInFinished(RESULT_USER_SIGN_IN_NEED_VERIFY_EMAIL, null);
                        }
                    }
                    else {
                        listener.onUserSignInFinished(RESULT_USER_SIGN_IN_FAIL, task.getException().getMessage());
                    }
                });
    }

    public void resetPassword(String email, final AuthStateChange listener) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    listener.onResetPasswordFinished(task.isSuccessful());
                });
    }

    public interface AuthStateChange {
        void onUserCreateFinished(int resultCode, String message);
        void onUserSignInFinished(int resultCode, String message);
        void onResetPasswordFinished(boolean success);
    }

    public void updatePassword(String password) {
        if (!TextUtils.isEmpty(password)) {
            firebaseUser.updatePassword(password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                }
            });

            firebaseUser.reload();
        }
    }

    public void signOut() {
        auth.signOut();
        firebaseUser = auth.getCurrentUser();
    }

    public void handleGoogleSignIn(Intent gsiData, final AuthStateChange listener) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(gsiData);
        try {
            googleAccount = task.getResult(ApiException.class);
        } catch (ApiException e) {
            e.printStackTrace();
        }

        if (googleAccount != null) {
            AuthCredential googleCredential = GoogleAuthProvider.getCredential(googleAccount.getIdToken(), null);
            auth.signInWithCredential(googleCredential)
                    .addOnCompleteListener(task1 -> {
                        if (task.isSuccessful()) {
                            firebaseUser = auth.getCurrentUser();
                            FirebaseUtils.createUserInfo(User.fromFirebaseUser(firebaseUser));
                            listener.onUserSignInFinished(RESULT_USER_SIGN_IN_SUCCESS, null);
                        }
                        else {
                            listener.onUserSignInFinished(RESULT_USER_SIGN_IN_FAIL, task.getException().getMessage());
                        }
                    });
        }
    }


}