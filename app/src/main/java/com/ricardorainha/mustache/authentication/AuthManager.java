package com.ricardorainha.mustache.authentication;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private static FirebaseAuth auth;
    private static FirebaseUser firebaseUser;

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

    public interface AuthStateChange {
        void onUserCreateFinished(int resultCode, String message);
        void onUserSignInFinished(int resultCode, String message);
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


}