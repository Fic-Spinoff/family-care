package es.udc.apm.familycare;

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
import es.udc.apm.familycare.interfaces.RouterActivity;

public class LinkFragment extends Fragment {

    private RouterActivity routerActivity = null;

    @BindView(R.id.link_toolbar) Toolbar toolbar;

    public static LinkFragment newInstance() {
        return new LinkFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_link, container, false);
        ButterKnife.bind(this, v);

        if(this.routerActivity != null) {
            this.routerActivity.setActionBar(this.toolbar);
        }

        return v;
    }

    @OnClick(R.id.btn_link_unbind)
    public void onClick(View view) {
        Toast.makeText(getActivity(), "Unbind device clicked", Toast.LENGTH_SHORT).show();
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
}
