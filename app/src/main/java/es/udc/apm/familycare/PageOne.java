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
 * Fragment one from preview
 */
public class PageOne extends Fragment {

    @BindView(R.id.imgCloud1)
    ImageView imgCloud1;
    @BindView(R.id.imgCloud2)
    ImageView imgCloud2;
    @BindView(R.id.imgCloud3)
    ImageView imgCloud3;
    @BindView(R.id.imgCloud4)
    ImageView imgCloud4;
    @BindView(R.id.tvTrack1)
    TextView tvTrack1;
    @BindView(R.id.tvTrack2)
    TextView tvTrack2;

    public PageOne() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static PageOne newInstance() {
        PageOne fragment = new PageOne();
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
        View v = inflater.inflate(R.layout.fragment_track, container, false);
        ButterKnife.bind(this, v);
        return v;
    }
}
