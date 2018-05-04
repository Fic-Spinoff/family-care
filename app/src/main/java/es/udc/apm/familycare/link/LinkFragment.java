package es.udc.apm.familycare.link;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.udc.apm.familycare.R;
import es.udc.apm.familycare.interfaces.RouterActivity;
import es.udc.apm.familycare.login.UserRepository;
import es.udc.apm.familycare.utils.Constants;

public class LinkFragment extends Fragment {

    private RouterActivity routerActivity = null;

    @BindView(R.id.link_toolbar) Toolbar toolbar;
    @BindView(R.id.et_link_key) EditText etLink;

    private LinkViewModel viewModel;

    public static LinkFragment newInstance() {
        return new LinkFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_link, container, false);
        ButterKnife.bind(this, v);

        if(this.routerActivity != null) {
            this.routerActivity.setActionBar(this.toolbar);
        }

        etLink.setKeyListener(null);

        String uid = getActivity().getSharedPreferences(Constants.PREFS_USER, Context.MODE_PRIVATE)
                .getString(Constants.PREFS_USER_UID, null);
        this.viewModel = new LinkViewModel(new UserRepository(), uid);

        return v;
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

    @OnClick(R.id.btn_link_refresh) void onClickRefresh(View v) {
        v.setVisibility(View.INVISIBLE);
        this.viewModel.refreshLink().observe(this, s -> {
            v.setVisibility(View.VISIBLE);
            if (s != null) {
                this.etLink.setText(s);
            }
        });
    }

    @OnClick(R.id.btn_link_share) void onClickShare() {

    }

    @OnClick(R.id.btn_link_share_link) void onClickShareLink() {
        // TODO
    }
}
