package es.udc.apm.familycare.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import es.udc.apm.familycare.model.Event;
import es.udc.apm.familycare.utils.Constants;

/**
 * Created by Gonzalo on 04/05/2018.
 */

public class EventRepository {

    public static final String TAG = "TAG_GROUPREPO";
    private FirebaseFirestore mFirestore;

    public EventRepository() {
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    public LiveData<Event> getEvent(String userUid, String eventUid) {
        final MutableLiveData<Event> event = new MutableLiveData<>();
        DocumentReference docRef = this.mFirestore.collection(Constants.Collections.USERS)
                .document(userUid).collection(Constants.Collections.EVENTS).document(eventUid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    Event e = document.toObject(Event.class);
                    event.setValue(e);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
        return event;
    }

    public void createEvent(String userUid, Event event) {
        this.mFirestore.collection(Constants.Collections.USERS).document(userUid)
                .collection(Constants.Collections.EVENTS).add(event);
    }

    public void deleteEvent(String userUid, String eventUid) {
        this.mFirestore.collection(Constants.Collections.USERS).document(userUid)
                .collection(Constants.Collections.EVENTS).add(eventUid);
    }
}
