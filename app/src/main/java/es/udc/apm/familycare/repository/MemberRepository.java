package es.udc.apm.familycare.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import es.udc.apm.familycare.model.Member;

/**
 * Created by Gonzalo on 29/04/2018.
 * Fetch member data from Firestore.
 */

public class MemberRepository {

    public static final String TAG = "TAG_MEMBERREPO";
    private FirebaseFirestore mFirestore;
    private MutableLiveData<List<Member>> memberList;

    public MemberRepository() {
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    /**
     * Connects to firestore and retrieve member list related with a vip. It will listen snapshot
     * changes and update live data value.
     * @param vipUid Vip UID from firestore.
     * @return A live data object that will be notified of snapshot changes. If query has already
     * been fetched it will return the live data object from memory.
     */
    public LiveData<List<Member>> getAll(String vipUid) {
        if(this.memberList != null) {
            return this.memberList;
        }

        this.memberList = new MutableLiveData<>();
        final List<Member> dataList = new ArrayList<>();
        this.memberList.setValue(dataList);

        this.mFirestore.collection("users")
                .whereEqualTo("vip", vipUid)
                .addSnapshotListener((documentSnapshots, e) -> {
                    // Handle errors
                    if (e != null || documentSnapshots == null) {
                        Log.w(TAG, "onEvent:error", e);
                        return;
                    }

                    // Dispatch the event
                    for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
                        // Snapshot of the changed document
                        DocumentSnapshot snapshot = change.getDocument();

                        switch (change.getType()) {
                            case ADDED:
                                dataList.add(new Member(
                                        snapshot.getId(),
                                        (String) snapshot.get("name"),
                                        (String) snapshot.get("photo")));
                                Log.d(TAG, "onEvent:ADDED "+snapshot.toString());
                                break;
                            case MODIFIED:
                                dataList.add(new Member(
                                        snapshot.getId(),
                                        (String) snapshot.get("name"),
                                        (String) snapshot.get("photo")));
                                Log.d(TAG, "onEvent:MODIFIED "+snapshot.toString());
                                break;
                            case REMOVED:
                                dataList.remove(new Member(
                                        snapshot.getId(),
                                        (String) snapshot.get("name"),
                                        (String) snapshot.get("photo")));
                                Log.d(TAG, "onEvent:REMOVED "+snapshot.toString());
                                break;
                        }
                    }
                    this.memberList.setValue(dataList);
                });
        return this.memberList;
    }

}
