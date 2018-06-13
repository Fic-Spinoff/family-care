package es.udc.apm.familycare.members;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import es.udc.apm.familycare.model.Member;
import es.udc.apm.familycare.repository.MemberRepository;

/**
 * Created by Gonzalo on 29/04/2018.
 * Member fragment ViewModel. Binds view with repository live data.
 */

public class MemberViewModel extends ViewModel {

    public interface MemberViewModelListener extends LifecycleOwner {
        void onInit();
        void onChange();
    }

    private boolean init = false;
    private MemberViewModelListener mOwner;
    private MemberRepository mRepo;
    private LiveData<List<Member>> memberList;

    public MemberViewModel(MemberViewModelListener owner) {
        this.mRepo = new MemberRepository();
        this.mOwner = owner;
    }

    /**
     * Gets member list from repository.
     * @param vipUid VIP UID to retrieve members from that user.
     * @return The list bind to the live data object from repository.
     */
    public List<Member> getMembers(String vipUid) {
        this.memberList = this.mRepo.getAll(vipUid);
        this.memberList.observe(this.mOwner, members -> {
            // When you attach observer it is called, we want to prevent that call
            if(init) {
                mOwner.onChange();
            } else {
                mOwner.onInit();
                init = true;
            }
        });
        return this.memberList.getValue();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        this.memberList.removeObservers(this.mOwner);
    }
}
