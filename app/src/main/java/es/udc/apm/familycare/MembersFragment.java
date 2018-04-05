package es.udc.apm.familycare;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.udc.apm.familycare.interfaces.RouterActivity;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MembersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MembersFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private RouterActivity routerActivity = null;
    @BindView(R.id.membersToolbar) Toolbar toolbar;

    public static MembersFragment newInstance() {
        return new MembersFragment();
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
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_members, container, false);
        ButterKnife.bind(this, v);

        this.routerActivity.setActionBar(this.toolbar);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.members_list, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(routerActivity != null) {
            routerActivity.navigate(DetailMemberFragment.newInstance(position), "DETAIL");
        }
    }
}

