package com.ricardorainha.mustache.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.android.material.snackbar.Snackbar;
import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.authentication.AuthManager;
import com.ricardorainha.mustache.databinding.ActivityLoginBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 3498;

    private ActivityLoginBinding binding;
    private LoginActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        viewModel = ViewModelProviders.of(this).get(LoginActivityViewModel.class);
        binding.setViewModel(viewModel);

        configureObservables();
        configureFields();
    }

    private void configureObservables() {
        viewModel.getLoading().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                showProgress(viewModel.getLoading().get());
            }
        });

        viewModel.getSignedIn().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (viewModel.getSignedIn().get()) {
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
                else {
                    String finalMessage = (viewModel.getMessage() == null) ? getString(viewModel.getMessageId()) : getString(viewModel.getMessageId(), viewModel.getMessage());
                    Snackbar.make(binding.loginRoot, finalMessage, Snackbar.LENGTH_LONG).show();

                    if (viewModel.getMessageId() == R.string.user_login_fail_message) {
                        binding.resetPassword.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        viewModel.getSignedUp().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (viewModel.getSignedUp().get()) {
                    String finalMessage = (viewModel.getMessage() == null) ? getString(viewModel.getMessageId()) : getString(viewModel.getMessageId(), viewModel.getMessage());
                    Snackbar.make(binding.loginRoot, finalMessage, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        viewModel.getPasswordReset().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (viewModel.getPasswordReset().get()) {
                    String finalMessage = getString(R.string.reset_password_successfully, binding.email.getText().toString());
                    Snackbar.make(binding.loginRoot, finalMessage, Snackbar.LENGTH_LONG).show();
                    viewModel.getPasswordReset().set(false);
                }
            }
        });
    }

    private void configureFields() {
        populateAutoComplete();

        // TODO: add Google Sign In
        // TODO: add Facebook Sign In
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        LoaderManager.getInstance(this).initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(binding.email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, v -> requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS));
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    private boolean fieldsAreValid() {
        // Reset errors.
        binding.email.setError(null);
        binding.password.setError(null);

        // Store values at the time of the login attempt.
        String email = binding.email.getText().toString();
        String password = binding.password.getText().toString();

        boolean hasErrors = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            binding.password.setError(getString(R.string.error_invalid_password));
            focusView = binding.password;
            hasErrors = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            binding.email.setError(getString(R.string.error_field_required));
            focusView = binding.email;
            hasErrors = true;
        } else if (!isEmailValid(email)) {
            binding.email.setError(getString(R.string.error_invalid_email));
            focusView = binding.email;
            hasErrors = true;
        }

        if (hasErrors) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }

        return !hasErrors;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void doSignIn() {
        viewModel.doSignIn();
    }

    private void doSignUp() {
        viewModel.doSignUp();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            binding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            binding.loginProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    binding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            binding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        binding.email.setAdapter(adapter);
    }

    public void onSignInClicked(View view) {
        if (fieldsAreValid()) {
            doSignIn();
        }
    }

    public void onResetPasswordClicked(View view) {
        if (isEmailValid(binding.email.getText().toString())) {
            viewModel.resetPassword();
        }

    }

    public void onGoogleSignInClicked(View view) {
        Intent googleSignInIntent = AuthManager.getInstance().getGsiClient(this).getSignInIntent();
        startActivityForResult(googleSignInIntent, REQUEST_CODE_GOOGLE_SIGN_IN);
    }

    public void onSignUpClicked(View view) {
        if (fieldsAreValid()) {
            doSignUp();
        }
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN && resultCode == RESULT_OK) {
            viewModel.doGoogleSignIn(data);
        }
    }
}