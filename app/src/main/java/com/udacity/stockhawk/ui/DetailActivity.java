package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by daber on 14/01/17.
 */

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXTRA_STOCK_NAME = "extra_stock_name";
    private static final int HISTORY_LOADER = 1;
    private Cursor cursor = null;
    @BindView(R.id.chart_view)
    CandleStickChart mChart;

    @BindView(R.id.stockName)
    TextView mStockName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        getSupportLoaderManager().initLoader(HISTORY_LOADER, null, this);
        setupChart();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setupChart() {
        mChart.setBackgroundColor(Color.WHITE);

        mChart.getDescription().setEnabled(false);



        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
//        leftAxis.setEnabled(false);
        leftAxis.setLabelCount(7, false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
//        rightAxis.setStartAtZero(false);


        mChart.getLegend().setEnabled(false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, getIntent().getData(), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursor = data;
        if (data.moveToFirst()) {
            String historyString = data.getString(Contract.Quote.POSITION_HISTORY);
            String stockName = data.getString(Contract.Quote.POSITION_SYMBOL);
            fillChartData(stockName, historyString);

            mStockName.setText(stockName);
        }
    }

    private void fillChartData(String stockName, String historyString) {
        String[] rawEntries = historyString.split("\n");
        ArrayList<CandleEntry> entries = new ArrayList<>(rawEntries.length);

        for (int i = 0; i < rawEntries.length; i++) {
            String[] elems = rawEntries[i].split(",");

            float high = Float.valueOf(elems[1]);
            float low = Float.valueOf(elems[2]);
            float open = Float.valueOf(elems[3]);
            float close = Float.valueOf(elems[4]);

            entries.add(new CandleEntry(i, high, low, open, close,Long.parseLong(elems[0])));

        }
        CandleDataSet dataset = new CandleDataSet(entries, stockName);


        dataset.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set1.setColor(Color.rgb(80, 80, 80));
        dataset.setShadowColor(Color.DKGRAY);
        dataset.setShadowWidth(0.7f);
        dataset.setDecreasingColor(Color.RED);
        dataset.setDecreasingPaintStyle(Paint.Style.FILL);
        dataset.setIncreasingColor(Color.rgb(122, 242, 84));
        dataset.setIncreasingPaintStyle(Paint.Style.FILL);
        dataset.setNeutralColor(Color.BLUE);
        CandleData data = new CandleData(dataset);
        mChart.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (cursor != null) {
            cursor.close();
        }
    }
}