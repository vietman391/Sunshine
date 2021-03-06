package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TYPE_TODAY = 0, VIEW_TYPE_FUTURE_DAY = 1;
    private boolean mUseTodayLayout = true;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }
    /*
        Remember that these views are reused as needed.
    */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        if(viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;
        } else if (viewType == VIEW_TYPE_FUTURE_DAY) {
            layoutId = R.layout.list_item_forecast;
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ForecastViewHolder forecastViewHolder = new ForecastViewHolder(view);
        view.setTag(forecastViewHolder);
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
    */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        ForecastViewHolder forecastViewHolder = (ForecastViewHolder) view.getTag();

        int viewType = getItemViewType(cursor.getPosition());
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        if(viewType == VIEW_TYPE_TODAY) {
            forecastViewHolder.iconView
                    .setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
        } else {
            forecastViewHolder.iconView
                    .setImageResource(Utility.getIconResourceForWeatherCondition(weatherId));
        }

        long dateInMillis = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        forecastViewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateInMillis));

        TextView descriptionView = forecastViewHolder.descriptionView;
        descriptionView.setText(cursor.getString(ForecastFragment.COL_WEATHER_DESC));

        boolean isMetric = Utility.isMetric(context);
        forecastViewHolder.highTempView.setText(Utility.formatTemperature(
                context, cursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP), isMetric));

        forecastViewHolder.lowTempView.setText(Utility.formatTemperature(
                context, cursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP), isMetric));
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ForecastViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ForecastViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}