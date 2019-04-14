package com.ricardorainha.mustache.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ricardorainha.mustache.authentication.AuthManager;
import com.ricardorainha.mustache.model.Session;
import com.ricardorainha.mustache.model.User;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;

public class FirebaseUtils {

    private static String TAG = FirebaseUtils.class.getSimpleName();
    private static final String USERS_DATABASE_REFERENCE = "users";
    private static final String PHOTOS_STORAGE_REFERENCE = "photos";
    private static final String PHOTOS_STORAGE_EXTENSION = ".jpg";

    private static FirebaseDatabase instance;

    private static FirebaseDatabase getFirebaseDatabaseInstance() {
        if (instance == null) {
            instance = FirebaseDatabase.getInstance();
            instance.setPersistenceEnabled(true);
        }

        return instance;
    }

    public static void createUserInfo(User user, AuthManager.UserDataChange listener) {
        requestUserInfo(user.getUid(), dbUser -> {
            if (dbUser == null) {
                FirebaseDatabase database = getFirebaseDatabaseInstance();
                DatabaseReference userReference = database.getReference(USERS_DATABASE_REFERENCE).child(user.getUid()).getRef();
                userReference.setValue(user).addOnCompleteListener(task -> {
                    Log.d(TAG, "User with UID " + user.getUid() + " created on RealtimeDatabase");
                    if (listener != null) {
                        listener.onUserDataCreated();
                    }
                });
            }
            else {
                listener.onUserDataCreated();
            }
        });
    }

    public static void updateUserInfo() {
        User user = Session.getInstance().getUser().getValue();
        DatabaseReference userReference = getFirebaseDatabaseInstance().getReference(USERS_DATABASE_REFERENCE).child(user.getUid());
        userReference.updateChildren(user.toMap(), (databaseError, databaseReference) -> Log.d(TAG, "User with UID " + user.getUid() + " updated on RealtimeDatabase"));
    }

    public static void requestUserInfo(String uid, Session.Callback callback) {
        FirebaseDatabase database = getFirebaseDatabaseInstance();
        DatabaseReference userReference = database.getReference(USERS_DATABASE_REFERENCE).child(uid);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.onUserInfoReceived(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void updateUserPhoto(Bitmap photo) {
        StorageReference photoReference = getFirebasePhotoPath().child(Session.getInstance().getUser().getValue().getUid() + PHOTOS_STORAGE_EXTENSION);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] photoData = outputStream.toByteArray();

        UploadTask uploadTask = photoReference.putBytes(photoData);
        uploadTask.addOnSuccessListener(taskSnapshot -> Log.d(TAG, "Profile photo uploaded to " + taskSnapshot.getMetadata().getPath()));
    }

    public static void removeProfilePhoto(OnCompleteListener<Void> listener) {
        StorageReference photoReference = getFirebasePhotoPath().child(Session.getInstance().getUser().getValue().getUid() + PHOTOS_STORAGE_EXTENSION);
        photoReference.delete().addOnCompleteListener(listener);
    }

    public static void deleteAccount() {
        User user = Session.getInstance().getUser().getValue();
        getProfilePhotoPath(user.getUid()).delete();
        getFirebaseDatabaseInstance().getReference(USERS_DATABASE_REFERENCE).child(user.getUid()).removeValue();
    }

    public static StorageReference getFirebasePhotoPath() {
        return FirebaseStorage.getInstance().getReference(PHOTOS_STORAGE_REFERENCE);
    }

    public static StorageReference getProfilePhotoPath(String uid) {
        return getFirebasePhotoPath().child(uid + PHOTOS_STORAGE_EXTENSION);
    }
}
