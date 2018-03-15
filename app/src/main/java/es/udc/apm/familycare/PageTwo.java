package es.udc.apm.familycare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PageTwo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageTwo extends Fragment {

    @BindView(R.id.bg)
    ViewGroup bg;
    @BindView(R.id.imgFall)
    ImageView imgFall;
    @BindView(R.id.tvFall1)
    TextView tvFall1;
    @BindView(R.id.tvFall2)
    TextView tvFall2;

    public PageTwo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static PageTwo newInstance() {
        PageTwo fragment = new PageTwo();
        fragment.setArguments(null);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fall, container, false);
        ButterKnife.bind(this, v);
        return v;
    }
}
