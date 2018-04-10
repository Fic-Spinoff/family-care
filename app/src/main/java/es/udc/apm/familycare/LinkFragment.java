package es.udc.apm.familycare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LinkFragment extends Fragment {

    public static LinkFragment newInstance() {
        return new LinkFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_link, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.btn_link_unbind)
    public void onClick(View view) {
        Toast.makeText(getActivity(), "Unbind device clicked", Toast.LENGTH_SHORT).show();
    }
}
