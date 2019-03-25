package com.ricardorainha.mustache.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ricardorainha.mustache.model.Session;
import com.ricardorainha.mustache.model.User;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FirebaseUtils {

    private static String TAG = FirebaseUtils.class.getSimpleName();
    private static final String USERS_DATABASE_REFERENCE = "users";
    private static final String PHOTOS_STORAGE_REFERENCE = "photos";
    private static final String PHOTOS_STORAGE_EXTENSION = ".jpg";

    public static void createUserInfo(User user) {
        requestUserInfo(user.getUid(), dbUser -> {
            if (dbUser == null) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference userReference = database.getReference(USERS_DATABASE_REFERENCE).child(user.getUid()).getRef();
                userReference.setValue(user).addOnCompleteListener(task -> Log.d(TAG, "User with UID " + user.getUid() + " created on RealtimeDatabase"));
            }
        });
    }

    public static void updateUserInfo(User user) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference(USERS_DATABASE_REFERENCE).child(user.getUid());
        userReference.updateChildren(user.toMap(), (databaseError, databaseReference) -> Log.d(TAG, "User with UID " + user.getUid() + " updated on RealtimeDatabase"));
    }

    public static void requestUserInfo(String uid, Session.Callback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userReference = database.getReference(USERS_DATABASE_REFERENCE).child(uid);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static StorageReference getFirebasePhotoPath() {
        return FirebaseStorage.getInstance().getReference(PHOTOS_STORAGE_REFERENCE);
    }

    public static StorageReference getProfilePhotoPath(String uid) {
        return getFirebasePhotoPath().child(uid + PHOTOS_STORAGE_EXTENSION);
    }
}
