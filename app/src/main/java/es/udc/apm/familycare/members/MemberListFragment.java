package es.udc.apm.familycare.members;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.udc.apm.familycare.R;
import es.udc.apm.familycare.VipActivity;
import es.udc.apm.familycare.interfaces.RouterActivity;
import es.udc.apm.familycare.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MemberListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemberListFragment extends Fragment implements MemberViewModel.MemberViewModelListener, AdapterView.OnItemClickListener {

    private static final String KEY_ACTIVATED_POSITION = "activated_position";
    private static final String KEY_IS_SELECTED = "mIsSelected";

    private boolean mIsDualPane = false;
    private MemberAdapter mListAdapter;

    private int mActivatedPosition = ListView.INVALID_POSITION;
    private boolean mIsSelected = false, mDetailStarted = false;

    private RouterActivity routerActivity = null;
    @BindView(R.id.toolbar_member_list) Toolbar toolbar;
    @BindView(R.id.lv_member_list) ListView mListView;
    @BindView(R.id.ll_member_list) ViewGroup layoutList;
    @BindView(R.id.ll_member_list_info) ViewGroup layoutEmpty;


    public static MemberListFragment newInstance() {
        return new MemberListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_member_list, container, false);
        ButterKnife.bind(this, v);

        this.mIsDualPane =  v.findViewById(R.id.layout_member_detail) != null;

        this.routerActivity.setActionBar(this.toolbar);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle saved) {
        super.onViewCreated(view, saved);

        if (saved != null) {
            // Restore the previously serialized activated item position.
            if (saved.containsKey(KEY_ACTIVATED_POSITION)) {
                mActivatedPosition = saved.getInt(KEY_ACTIVATED_POSITION);
            }
            if (saved.containsKey(KEY_IS_SELECTED)) {
                mIsSelected = saved.getBoolean(KEY_IS_SELECTED);
            }
        }

        SharedPreferences prefs = getActivity()
                .getSharedPreferences(Constants.PREFS_USER, Context.MODE_PRIVATE);
        String userUid = prefs.getString(Constants.PREFS_USER_UID, null);

        if(userUid == null) {
            Toast.makeText(getActivity(), "Error no user found!", Toast.LENGTH_SHORT).show();
            return;
        }

        //TODO: Injection
        MemberViewModel viewModel = new MemberViewModel(this);
        this.mListAdapter = new MemberAdapter(getActivity(),
                viewModel.getMembers(userUid));
        this.mListView.setAdapter(this.mListAdapter);
        this.mListView.setOnItemClickListener(this);
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

    /**
     * En onResume comprobas si es DualPane. En caso de que lo sea seleccionamos el último item.
     * Si es simple lanzmaos vista de detalle en caso de que estuviese selccionado alguno.
     */
    @Override
    public void onResume() {
        super.onResume();

        if (mIsDualPane) {
            // Activate selection mark
            this.setActivateOnItemClick(true);
            // Select previous item or default
            this.setActivatedPosition();
            mDetailStarted = false;
        } else {
            // Deactivate selection mark
            this.setActivateOnItemClick(false);

            if (mDetailStarted) {
                mDetailStarted = false;
                mIsSelected = false;
            } else if (mIsSelected) {
                // if no dual and selected open detail
                mDetailStarted = true;
                if(routerActivity != null) {
                    routerActivity.navigate(
                            DetailMemberFragment.newInstance(mActivatedPosition, mIsDualPane),
                            VipActivity.SCREEN_DETAIL);

                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(KEY_ACTIVATED_POSITION, mActivatedPosition);
        }
        outState.putBoolean(KEY_IS_SELECTED, mIsSelected);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        mActivatedPosition = position;
        mIsSelected = true;
        if (!mIsDualPane) {
            mDetailStarted = true;
        }

        if (mIsDualPane) {
            // If dual replace side detail fragment
            this.toolbar.setTitle(this.mListAdapter.getItem(position).getName());
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_member_detail,
                            DetailMemberFragment.newInstance(position, true),
                            VipActivity.SCREEN_DETAIL)
                    .commit();
        } else {
            // If not dual navigate to detail
            if(routerActivity != null) {
                routerActivity.navigate(DetailMemberFragment.newInstance(position, false),
                        VipActivity.SCREEN_DETAIL);

            }
        }
    }

    @Override
    public void onInit() {
        // Show loading
    }

    @Override
    public void onChange() {
        if(this.mListAdapter != null) {
            this.mListAdapter.notifyDataSetChanged();
            this.updateEmptyUI(this.mListAdapter.getCount() == 0);
        }
    }

    /**
     * Updates UI status to show info message when empty list.
     * @param empty If list is empty
     */
    private void updateEmptyUI(boolean empty) {
        if(empty) {
            this.layoutList.setVisibility(View.GONE);
            this.layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            this.layoutList.setVisibility(View.VISIBLE);
            this.layoutEmpty.setVisibility(View.GONE);
        }
    }

    /**
     * Sets current position. If none selected then first item
     */
    private void setActivatedPosition() {
        if(this.mListAdapter == null || this.mListAdapter.getCount() == 0) {
            return;
        }

        Boolean tmp = mIsSelected;
        if (mActivatedPosition == ListView.INVALID_POSITION) {
            mListView.performItemClick(null, 0, 0);
        }
        else {
            mListView.performItemClick(null, mActivatedPosition, 0);
        }
        mIsSelected = tmp; //Conservar el valor modificado en el onclick
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        mListView.setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }
}