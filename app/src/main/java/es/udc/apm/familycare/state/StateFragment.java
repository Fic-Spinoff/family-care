package es.udc.apm.familycare.state;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.udc.apm.familycare.FamilyCare;
import es.udc.apm.familycare.R;
import es.udc.apm.familycare.interfaces.RouterActivity;
import es.udc.apm.familycare.model.User;


public class StateFragment extends Fragment {

    private Unbinder mUnbinder;
    private RouterActivity routerActivity = null;

    @BindView(R.id.state_toolbar) Toolbar toolbar;
    @BindView(R.id.tv_state_still) TextView tvStill;

    private StateViewModel viewModel;
    private User user = null;

    public StateFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_state, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        this.routerActivity.setActionBar(this.toolbar);

        FamilyCare.getUser().observe(this, user -> {
            if (user != null) {
                this.user = user;
                this.initView();
            }
        });

        this.viewModel = new StateViewModel();

        return view;
    }

    private void initView() {
        if (this.user != null && this.user.getVip() != null) {
            this.viewModel.getUser(this.user.getVip()).observe(this, user -> {
                if (user != null) {
                    if (user.getStillSince() == null) {
                        tvStill.setText(R.string.caption_still_empty);
                    } else {
                        long diff = (new Date()).getTime() - user.getStillSince().toDate().getTime();
                        long hours = TimeUnit.MILLISECONDS.toHours(diff);
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff) - hours * 60;
                        tvStill.setText(getString(R.string.caption_still, hours, minutes));
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.routerActivity = (RouterActivity) context;
        } catch (ClassCastException ex){
            throw new ClassCastException(context.toString() + " must implement RouterActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.routerActivity = null;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
    }

}
