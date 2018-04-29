package es.udc.apm.familycare.members;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.udc.apm.familycare.R;
import es.udc.apm.familycare.interfaces.RouterActivity;

public class DetailMemberFragment extends Fragment {

    public final static String KEY_MEMBER = "KEY_ID";
    public final static String KEY_DUAL = "KEY_DUAL";

    private boolean mIsDualPane = false;

    private int memberId = -1;

    private RouterActivity routerActivity = null;
    @Nullable @BindView(R.id.toolbar_detail) Toolbar toolbar;

    public DetailMemberFragment() {
        // Required empty public constructor
    }

    public static DetailMemberFragment newInstance(int memberId, boolean isDual) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MEMBER, memberId);
        bundle.putBoolean(KEY_DUAL, isDual);
        DetailMemberFragment fragment = new DetailMemberFragment();
        fragment.setArguments(bundle);
        return fragment;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(KEY_MEMBER)) {
            this.memberId = bundle.getInt(KEY_MEMBER);
            if(bundle.containsKey(KEY_DUAL)) {
                this.mIsDualPane = bundle.getBoolean(KEY_DUAL);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detail_member, container, false);
        ButterKnife.bind(this, v);

        if(!mIsDualPane) {

            if(v.findViewById(R.id.detail_dual_hack) != null) {
                // It should be dual pane!
                this.routerActivity.goBack();
                return null;
            }

            this.routerActivity.setActionBar(this.toolbar);

            if(this.memberId != -1 && this.toolbar != null) {
                // TODO: Model data
//                this.toolbar.setTitle(getActivity().getResources()
//                        .getStringArray(R.array.members_list)[memberId]);
            }
        }

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.btn_detail_delete)
    public void onClick(View view) {
        Toast.makeText(getActivity(), "Delete member clicked", Toast.LENGTH_SHORT).show();
    }
}
