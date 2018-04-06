package es.udc.apm.familycare;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.udc.apm.familycare.interfaces.RouterActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class WalkFragment extends Fragment {

    private Unbinder mUnbinder;
    private RouterActivity routerActivity = null;

    @BindView(R.id.chart) public PieChart chart;
    @BindView(R.id.bar_chart) public HorizontalBarChart barChart;
    @BindView(R.id.walkToolbar) Toolbar toolbar;

    public WalkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_walk, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        this.routerActivity.setActionBar(this.toolbar);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(33.0f));
        entries.add(new PieEntry(67.0f));
        PieDataSet set = new PieDataSet(entries, "Pasos");
        set.setColors(getResources().getColor(android.R.color.black),
                getResources().getColor(android.R.color.transparent));
        this.chart.setEntryLabelColor(getResources().getColor(android.R.color.transparent));
        this.chart.setData(new PieData(set));
        this.chart.setTouchEnabled(false);
        this.chart.setDescription(null);
        this.chart.invalidate();

        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0f, 3000f));
        barEntries.add(new BarEntry(1f, 5000f));
        BarDataSet barSet = new BarDataSet(barEntries, "Semana");
        this.barChart.setData(new BarData(barSet));
        this.barChart.setFitBars(true);
        this.barChart.setTouchEnabled(false);
        this.barChart.setDescription(null);
        this.barChart.invalidate();

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
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
    }

}
