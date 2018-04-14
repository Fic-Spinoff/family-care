package es.udc.apm.familycare.members;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.udc.apm.familycare.R;
import es.udc.apm.familycare.VipActivity;
import es.udc.apm.familycare.interfaces.RouterActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MemberListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemberListFragment extends ListFragment {

    private static final String KEY_ACTIVATED_POSITION = "activated_position";
    private static final String KEY_IS_SELECTED = "mIsSelected";

    private boolean mIsDualPane = false;
    private ListView mListView;

    private List<String> members;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private boolean mIsSelected = false, mDetailStarted = false;

    private RouterActivity routerActivity = null;
    @BindView(R.id.membersToolbar) Toolbar toolbar;

    public static MemberListFragment newInstance() {
        return new MemberListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Data from model
        members = Arrays.asList(getResources().getStringArray(R.array.members_list));

        MemberAdapter mListAdapter = new MemberAdapter(getActivity(), members);
        setListAdapter(mListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_member_list, container, false);
        ButterKnife.bind(this, v);

        mIsDualPane =  v.findViewById(R.id.layout_member_detail) != null;

        this.routerActivity.setActionBar(this.toolbar);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle saved) {
        super.onViewCreated(view, saved);

        mListView = getListView();

        if (saved != null) {
            // Restore the previously serialized activated item position.
            if (saved.containsKey(KEY_ACTIVATED_POSITION)) {
                mActivatedPosition = saved.getInt(KEY_ACTIVATED_POSITION);
            }
            if (saved.containsKey(KEY_IS_SELECTED)) {
                mIsSelected = saved.getBoolean(KEY_IS_SELECTED);
            }
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(KEY_ACTIVATED_POSITION, mActivatedPosition);
        }
        outState.putBoolean(KEY_IS_SELECTED, mIsSelected);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        mActivatedPosition = position;
        mIsSelected = true;
        if (!mIsDualPane) {
            mDetailStarted = true;
        }

        if (mIsDualPane) {
            // If dual replace side detail fragment
            // TODO: Model data
            this.toolbar.setTitle(members.get(position));
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

    /**
     * Sets current position. If none selected then first item
     */
    private void setActivatedPosition() {
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