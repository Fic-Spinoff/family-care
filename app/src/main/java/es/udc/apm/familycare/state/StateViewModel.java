package es.udc.apm.familycare.state;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import es.udc.apm.familycare.login.UserRepository;
import es.udc.apm.familycare.model.User;

/**
 * Created by Gonzalo on 04/05/2018.
 * Link Fragment view model
 */

public class StateViewModel extends ViewModel {

    private UserRepository mRepo;

    public StateViewModel() {
        this.mRepo = new UserRepository();
    }

    public LiveData<User> getUser(String uid) {
        return this.mRepo.getUser(uid);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
