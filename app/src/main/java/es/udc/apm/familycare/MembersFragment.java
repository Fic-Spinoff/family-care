package es.udc.apm.familycare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MembersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MembersFragment extends ListFragment implements AdapterView.OnItemClickListener {

    public static MembersFragment newInstance() {
        return new MembersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_members, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.MembersList, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
       // Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();

        Fragment detailMemberFragment = new DetailMemberFragment();
        FragmentManager frm = this.getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = frm.beginTransaction();
        transaction.replace(R.id.layout_vip, detailMemberFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}

