package es.udc.apm.familycare;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailMemberFragment extends Fragment {

    public final static String KEY_MEMBER = "KEY_ID";

    private int memberId = -1;

    @BindView(R.id.detailToolbar) Toolbar toolbar;

    public DetailMemberFragment() {
        // Required empty public constructor
    }

    public static DetailMemberFragment newInstance(int memberId) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MEMBER, memberId);
        DetailMemberFragment fragment = new DetailMemberFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(KEY_MEMBER)) {
            this.memberId = bundle.getInt(KEY_MEMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detail_member, container, false);
        ButterKnife.bind(this, v);

        if(this.memberId != -1) {
            this.toolbar.setTitle(getActivity().getResources().getStringArray(R.array.members_list)[memberId]);
        }

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
