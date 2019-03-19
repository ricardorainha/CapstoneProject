package com.ricardorainha.mustache.authentication;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;

public class AuthManager {
    public static final int RESULT_USER_CREATE_FAIL = 0;
    public static final int RESULT_USER_CREATE_EMAIL_SENT_FAIL = 1;
    public static final int RESULT_USER_CREATE_EMAIL_SENT_SUCCESS = 2;
    public static final int RESULT_USER_SIGN_IN_FAIL = 3;
    public static final int RESULT_USER_SIGN_IN_NEED_VERIFY_EMAIL = 4;
    public static final int RESULT_USER_SIGN_IN_SUCCESS = 5;

    private static final AuthManager ourInstance = new AuthManager();
    private static FirebaseAuth auth;
    private static FirebaseUser user;

    public static AuthManager getInstance() {
        return ourInstance;
    }

    private AuthManager() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }

    public FirebaseUser getUser() {
        return user;
    }

    public boolean isUserLoggedIn() {
        return (user != null);
    }

    public boolean needToLogin() {
        return !(isUserLoggedIn() && user.isEmailVerified());
    }

    public void createUserWithEmailAndPassword(String email, String password, final AuthStateChange listener) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        user = auth.getCurrentUser();
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    listener.onUserCreateFinished(RESULT_USER_CREATE_EMAIL_SENT_SUCCESS, null);
                                }
                                else {
                                    listener.onUserCreateFinished(RESULT_USER_CREATE_EMAIL_SENT_FAIL, task.getException().getMessage());
                                }
                            }
                        });
                    }
                    else {
                        listener.onUserCreateFinished(RESULT_USER_CREATE_FAIL, task.getException().getMessage());
                    }
                }
            });
    }

    public void signInWithEmailAndPassword(String email, String password, final AuthStateChange listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = auth.getCurrentUser();
                            if (user.isEmailVerified()) {
                                listener.onUserSignInFinished(RESULT_USER_SIGN_IN_SUCCESS, null);
                            }
                            else {
                                listener.onUserSignInFinished(RESULT_USER_SIGN_IN_NEED_VERIFY_EMAIL, null);
                            }
                        }
                        else {
                            listener.onUserSignInFinished(RESULT_USER_SIGN_IN_FAIL, task.getException().getMessage());
                        }
                    }
                });
    }

    public interface AuthStateChange {
        void onUserCreateFinished(int resultCode, String message);
        void onUserSignInFinished(int resultCode, String message);
    }


}
