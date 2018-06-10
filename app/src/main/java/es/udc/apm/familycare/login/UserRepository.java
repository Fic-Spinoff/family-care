package es.udc.apm.familycare.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.List;
import java.util.Map;

import es.udc.apm.familycare.model.User;
import es.udc.apm.familycare.utils.Constants;

/**
 * Created by Gonzalo on 04/05/2018.
 */

public class UserRepository {

    public static final String TAG = "TAG_LOGINREPO";
    private FirebaseFirestore mFirestore;

    public UserRepository() {
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    public LiveData<User> getUser(String uid) {
        final MutableLiveData<User> user = new MutableLiveData<>();
        DocumentReference docRef = this.mFirestore.collection("users").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    User u = document.toObject(User.class);
                    user.setValue(u);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
        return user;
    }

    public LiveData<List<User>> getUserByLink(String link) {
        final MutableLiveData<List<User>> users = new MutableLiveData<>();
        this.mFirestore.collection("users")
                .whereEqualTo("link", link).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            users.setValue(task.getResult().toObjects(User.class));
                        } else {
                            Log.d(TAG, "No documents by link: "+link);
                            users.setValue(null);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
        return users;
    }


    public void setUser(User user) {
        this.mFirestore.collection(Constants.Collections.USERS).document(user.getUid()).set(user);
    }

    public void editUser(User user) {
        this.mFirestore.collection(Constants.Collections.USERS).document(user.getUid()).set(user, SetOptions.merge());
    }

    public void editUser(String uid, Map<String, Object> userData) {
        this.mFirestore.collection(Constants.Collections.USERS).document(uid).update(userData);
    }
}
