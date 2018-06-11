package es.udc.apm.familycare.messages;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.Hashtable;

import es.udc.apm.familycare.FamilyCare;
import es.udc.apm.familycare.model.User;
import es.udc.apm.familycare.repository.GroupRepository;
import es.udc.apm.familycare.repository.UserRepository;
import es.udc.apm.familycare.utils.Constants;

public class FCMInstanceIdService extends FirebaseInstanceIdService {

    public static final String TAG = "FCMInstanceIdService";

    private UserRepository mRepo;
    private GroupRepository mGroupRepo;

    public FCMInstanceIdService() {
        super();
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // Save FCM Instance Id
        this.mRepo = new UserRepository();
        this.mGroupRepo = new GroupRepository();
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {

        Observer<User> observer = new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    // Update token
                    Hashtable<String, Object> data = new Hashtable<>();
                    data.put(Constants.Properties.FCM_ID, refreshedToken);
                    mRepo.editUser(user.getUid(), data);

                    // Update message group if any
                    if (Constants.ROLE_GUARD.equals(user.getRole()) && user.getVip() != null) {
                        mGroupRepo.addGuard(user.getVip(), user.getUid(), refreshedToken);
                    }

                    FamilyCare.getUser().removeObserver(this);
                }
            }
        };
        // Get user data to update
        FamilyCare.getUser().observeForever(observer);
    }
}
