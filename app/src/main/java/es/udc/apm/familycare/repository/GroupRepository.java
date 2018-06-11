package es.udc.apm.familycare.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import es.udc.apm.familycare.model.MessageGroup;
import es.udc.apm.familycare.utils.Constants;

/**
 * Created by Gonzalo on 04/05/2018.
 */

public class GroupRepository {

    public static final String TAG = "TAG_GROUPREPO";
    private FirebaseFirestore mFirestore;

    public GroupRepository() {
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    public LiveData<MessageGroup> getGroup(String uid) {
        final MutableLiveData<MessageGroup> group = new MutableLiveData<>();
        DocumentReference docRef = this.mFirestore.collection(Constants.Collections.GROUPS).document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    MessageGroup g = document.toObject(MessageGroup.class);
                    group.setValue(g);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
        return group;
    }

    public void setGroup(String vipUid, MessageGroup group) {
        this.mFirestore.collection(Constants.Collections.GROUPS).document(vipUid).set(group);
    }

    public void editGroup(String vipUid, MessageGroup group) {
        this.mFirestore.collection(Constants.Collections.GROUPS).document(vipUid).set(group, SetOptions.merge());
    }

    public void editGroup(String vipUid, Map<String, Object> groupData) {
        this.mFirestore.collection(Constants.Collections.GROUPS).document(vipUid).update(groupData);
    }

    public void addGuard(String vipUid, String ref, String token) {
        Log.d(TAG, "Add Guard");
        DocumentReference docRef = this.mFirestore.collection(Constants.Collections.GROUPS).document(vipUid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    MessageGroup group = document.toObject(MessageGroup.class);
                    group = this.addGuard(group, ref, token);
                    this.editGroup(vipUid, group);
                } else {
                    Log.d(TAG, "No such document");
                    this.setGroup(vipUid, this.addGuard(new MessageGroup(), ref, token));
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }


    public void removeGuard(String vipUid, String ref) {
        DocumentReference docRef = this.mFirestore.collection(Constants.Collections.GROUPS).document(vipUid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    MessageGroup group = document.toObject(MessageGroup.class);
                    group = this.removeGuard(group, ref);
                    this.setGroup(vipUid, group);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private MessageGroup removeGuard(MessageGroup group,  String ref) {
        if (group == null || group.getGuards() == null) {
            return group;
        }

        group.getGuards().remove(ref);

        return group;
    }

    private MessageGroup addGuard(MessageGroup group,  String ref, String token) {
        if (group == null) {
            group = new MessageGroup();
        }

        if (group.getGuards() == null) {
            Map<String, String> data = new HashMap<>();
            group.setGuards(data);
        }

        group.getGuards().put(ref, token);

        return group;
    }
}
