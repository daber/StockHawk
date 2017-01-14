package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.common.base.Joiner;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by daber on 14/01/17.
 */

public class StockWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
        return new StockRemoteViewsFactory(this, id);
    }

    class StockRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        Cursor cursor;
        DataSetObserver observer = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                AppWidgetManager.getInstance(service).notifyAppWidgetViewDataChanged(widgetId, R.id.stockView);
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
            }
        };

        final StockWidgetService service;
        final int widgetId;

        public StockRemoteViewsFactory(StockWidgetService stockWidgetService, int appWidgetId) {
            service = stockWidgetService;
            dollarFormatWithPlus.setPositivePrefix("+$");
            percentageFormat.setMaximumFractionDigits(2);
            percentageFormat.setMinimumFractionDigits(2);
            percentageFormat.setPositivePrefix("+");
            widgetId = appWidgetId;

        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if (cursor != null) {
                cursor.close();
            }
            final Set<String> stocks = PrefUtils.getStocks(service);
            HashSet<String> escaped_stocks = new HashSet<>();
            for (String s : stocks) {
                escaped_stocks.add("'" + s + "'");
            }

            String where = Contract.Quote.COLUMN_SYMBOL + " IN (" + Joiner.on(", ").join(escaped_stocks) + ")";

            cursor = service.getContentResolver().query(Contract.Quote.URI, null, where, null, Contract.Quote.COLUMN_SYMBOL);
            cursor.registerDataSetObserver(observer);
        }

        @Override
        public void onDestroy() {
            if (cursor != null) {
                cursor.close();
            }

        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            cursor.moveToPosition(i);
            String stockName = cursor.getString(Contract.Quote.POSITION_SYMBOL);
            float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
            float percentageChange = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
            float price = cursor.getFloat(Contract.Quote.POSITION_PRICE);

            String change = dollarFormatWithPlus.format(rawAbsoluteChange);
            String percentage = percentageFormat.format(percentageChange / 100);
            String priceText =dollarFormat.format(price);

            RemoteViews v = new RemoteViews(getPackageName(), R.layout.list_item_quote_widget);
            if (rawAbsoluteChange > 0) {
                v.setInt(R.id.change_absolute, "setBackgroundResource", R.drawable.percent_change_pill_green);
                v.setInt(R.id.change_percent, "setBackgroundResource", R.drawable.percent_change_pill_green);
            } else {
                v.setInt(R.id.change_absolute, "setBackgroundResource", R.drawable.percent_change_pill_red);
                v.setInt(R.id.change_percent, "setBackgroundResource", R.drawable.percent_change_pill_red);
            }
            v.setTextViewText(R.id.stockName, stockName);


            v.setTextViewText(R.id.change_percent, percentage);
            v.setTextViewText(R.id.change_absolute, change);
            v.setTextViewText(R.id.price,priceText);
            v.setTextViewText(R.id.symbol, stockName);
            Intent intent = new Intent();
            intent.setData(Contract.Quote.makeUriForStock(stockName));
            v.setOnClickFillInIntent(R.id.widget_item, intent);
            return v;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            cursor.moveToPosition(i);
            return cursor.getInt(Contract.Quote.POSITION_ID);
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }

}

