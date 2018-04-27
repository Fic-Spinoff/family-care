package es.udc.apm.familycare.members;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import es.udc.apm.familycare.base.LiveDataList;

/**
 * Created by Gonzalo on 23/04/2018.
 */

public class MemberListViewModel extends ViewModel{

    private FirebaseFirestore db;
    private LiveDataList<Member> memberList;

    public MemberListViewModel() {
        this.db = FirebaseFirestore.getInstance();
        this.memberList = new LiveDataList<>();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public LiveDataList<Member> getMemberList() {
        db.collection("users")
                .whereEqualTo("VIP", "vip-uid")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            Member member = dc.getDocument().toObject(Member.class);
                            switch (dc.getType()) {
                                case ADDED:
                                    memberList.addValue(member);
                                    Log.d("", "New member: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    memberList.updateValue(member);
                                    Log.d("", "Modified member: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    memberList.removeValue(member);
                                    Log.d("", "Removed member: " + dc.getDocument().getData());
                                    break;
                            }
                        }
                    }
                });
        return this.memberList;
    }
}
