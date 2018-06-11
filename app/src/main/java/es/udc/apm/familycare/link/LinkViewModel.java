package es.udc.apm.familycare.link;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import es.udc.apm.familycare.login.UserRepository;
import es.udc.apm.familycare.utils.Constants;

/**
 * Created by Gonzalo on 04/05/2018.
 * Link Fragment view model
 */

public class LinkViewModel extends ViewModel {

    private UserRepository mRepo;
    private String userUid;

    public LinkViewModel(UserRepository mRepo, String userUid) {
        this.mRepo = mRepo;
        this.userUid = userUid;
    }

    private String link = null;
    public LiveData<String> refreshLink() {
        link = UUID.randomUUID().toString().substring(0, 18);
        if(link.charAt(17) == '-') {
            link = link.substring(0, 17);
        }

        return Transformations.map(this.mRepo.getUserByLink(link), input -> {
            // Check if there are any vip using this link
            if(input == null || input.size() == 0) {
                // If no users found then use this link
                Map<String, Object> data = new HashMap<>();
                data.put(Constants.Properties.LINK, link);
                this.mRepo.editUser(userUid, data);
                return link;
            } else {
                // If link exists get a new one
                refreshLink();
                return null;
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
