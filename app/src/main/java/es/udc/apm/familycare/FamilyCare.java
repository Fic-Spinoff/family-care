package es.udc.apm.familycare;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import es.udc.apm.familycare.model.User;
import es.udc.apm.familycare.utils.Constants;

/**
 * Created by Gonzalo on 07/05/2018.
 */

public class FamilyCare extends Application {

    public static final String TAG_APP = "TAG_APP";
    public static FamilyCare app = null;

    private FirebaseFirestore mFirestore;
    private MutableLiveData<User> mUser = null;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mFirestore = FirebaseFirestore.getInstance();
        FamilyCare.app = this;
    }

    /**
     * Gets current user statically form app context
     * @return Logged user data
     */
    public static LiveData<User> getUser() {
        if(FamilyCare.app.mUser != null) {
            if (FamilyCare.app.mUser.getValue() == null) {
                fetchUser();
            }
            return FamilyCare.app.mUser;
        }

        FamilyCare.app.mUser = new MutableLiveData<>();
        fetchUser();

        return FamilyCare.app.mUser;
    }

    /**
     * Fetches user data from remote Firebase
     */
    private static void fetchUser() {
        String uid = app.getSharedPreferences(Constants.PREFS_USER, MODE_PRIVATE)
                .getString(Constants.PREFS_USER_UID, null);

        if(uid != null) {
            DocumentReference docRef = FamilyCare.app.mFirestore
                    .collection(Constants.Collections.USERS).document(uid);
            docRef.addSnapshotListener((documentSnapshot, e) -> {
                // Handle errors
                if (e != null || documentSnapshot == null) {
                    Toast.makeText(FamilyCare.app, R.string.error_get_user, Toast.LENGTH_SHORT).show();
                    Log.w(TAG_APP, "getUser():error", e);
                    return;
                }

                User user = documentSnapshot.toObject(User.class);
                FamilyCare.app.mUser.postValue(user);
            });
        }
    }

}
