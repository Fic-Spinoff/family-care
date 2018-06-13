package es.udc.apm.familycare.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import es.udc.apm.familycare.model.Geofence;
import es.udc.apm.familycare.utils.Constants;

/**
 * Created by Gonzalo on 04/05/2018.
 */

public class GeofenceRepository {

    public static final String TAG = "TAG_GEOREPO";
    private FirebaseFirestore mFirestore;

    public GeofenceRepository() {
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    public LiveData<Geofence> getGeofence(String userUid, String geofenceUid) {
        final MutableLiveData<Geofence> geofence = new MutableLiveData<>();
        DocumentReference docRef = this.mFirestore.collection(Constants.Collections.USERS)
                .document(userUid).collection(Constants.Collections.GEOFENCE).document(geofenceUid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    Geofence e = document.toObject(Geofence.class);
                    if (e != null) {
                        e = e.withId(document.getId());
                        geofence.setValue(e);
                    }
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
        return geofence;
    }

    public LiveData<List<Geofence>> getAllGeofences(String userUid) {
        final MutableLiveData<List<Geofence>> geoList = new MutableLiveData<>();
        CollectionReference collection = this.mFirestore.collection(Constants.Collections.USERS)
                .document(userUid).collection(Constants.Collections.GEOFENCE);
        // Get all collection for user
        collection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Geofence> list = new ArrayList<>();
                QuerySnapshot snapshots = task.getResult();
                // Add all geo to local list
                for (DocumentSnapshot document: snapshots) {
                    if (document != null && document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Geofence g = document.toObject(Geofence.class);
                        if (g != null) {
                            g = g.withId(document.getId());
                            list.add(g);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                }
                // Update live data
                geoList.setValue(list);
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
        return geoList;
    }

    public Task<DocumentReference> createGeofence(String userUid, Geofence geofence) {
        return this.mFirestore.collection(Constants.Collections.USERS).document(userUid)
                .collection(Constants.Collections.GEOFENCE).add(geofence);
    }

    public void deleteGeofence(String userUid, String geofenceUid) {
        this.mFirestore.collection(Constants.Collections.USERS).document(userUid)
                .collection(Constants.Collections.GEOFENCE).document(geofenceUid).delete();
    }

    public void setGeofence(String userUid, String uid, Geofence geofence) {
        this.mFirestore.collection(Constants.Collections.USERS).document(userUid)
                .collection(Constants.Collections.GEOFENCE).document(uid).set(geofence);
    }
}
