package com.example.materialweather.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.materialweather.R;
import com.example.materialweather.db.MaterialWeatherDB;
import com.example.materialweather.util.Utility;


/**
 * Created by GavinAndre on 2016/5/7.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private final String TAG = getClass().getSimpleName();
    private final int mBackground;
    private Context mContext;
    private String mCityId;
    private final int TYPE_ONE = 0;
    private final int TYPE_TWO = 1;
    private final int TYPE_THREE = 2;
    private MaterialWeatherDB mMaterialWeatherDB;
    private final TypedValue mTypedValue = new TypedValue();
    private String substring;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            //mTextView = (TextView) v.findViewById(R.id.textView);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //String text = "I Love " + mTextView.getText() + ".";
            //Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
            //Snackbar.make(v, "" + v.getId(), Snackbar.LENGTH_SHORT).show();
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, String cityId) {
        mContext = context;
        mCityId = cityId;
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mMaterialWeatherDB = MaterialWeatherDB.getInstance(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == TYPE_ONE) {
            return TYPE_ONE;
        }
        if (position == TYPE_TWO) {
            return TYPE_TWO;
        }
        if (position == TYPE_THREE) {
            return TYPE_THREE;
        }
        return super.getItemViewType(position);
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ONE) {
            return new ForecastViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_forecast, parent, false));
        }
        if (viewType == TYPE_TWO) {
            return new CurrentWeatherViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_weather_info, parent, false));
        }
        if (viewType == TYPE_THREE) {
            return new SuggestionViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_suggestion, parent, false));
        }
        return null;
    }

    class MyOnClickListener implements View.OnClickListener {
        private ViewHolder holder;
        private int i;

        public MyOnClickListener(ViewHolder holder, int i) {
            // TODO Auto-generated constructor stub
            this.holder = holder;
            this.i = i;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.forecast_view_id + i) {
                if (mMaterialWeatherDB.loadRecentWeatherInfo(mCityId).size() > 0) {
                    ((ForecastViewHolder) holder).forecastDetailDate.setText(String.format("周%s",
                            mMaterialWeatherDB.loadRecentWeatherInfo(mCityId).get(i).getWeek()));
                    ((ForecastViewHolder) holder).forecastDetailIcon.setImageResource(
                            Utility.weatherIconMap.get(
                                    mMaterialWeatherDB.loadRecentWeatherInfo(mCityId).get(i).getType()));
                    ((ForecastViewHolder) holder).forecastDetailTemp.setText(String.format("%s°/%s°",
                            mMaterialWeatherDB.loadRecentWeatherInfo(mCityId).get(i).getLowTemp(),
                            mMaterialWeatherDB.loadRecentWeatherInfo(mCityId).get(i).getHighTemp()));
                    ((ForecastViewHolder) holder).forecastDetailInfo.setText(
                            String.format("%s。 %s。 最高温度%s， 最低温度%s。 风向%s， 风力%s。",
                                    mMaterialWeatherDB.loadRecentWeatherInfo(mCityId).get(i).getDate(),
                                    mMaterialWeatherDB.loadRecentWeatherInfo(mCityId).get(i).getType(),
                                    mMaterialWeatherDB.loadRecentWeatherInfo(mCityId).get(i).getHighTemp(),
                                    mMaterialWeatherDB.loadRecentWeatherInfo(mCityId).get(i).getLowTemp(),
                                    mMaterialWeatherDB.loadRecentWeatherInfo(mCityId).get(i).getWindDirection(),
                                    mMaterialWeatherDB.loadRecentWeatherInfo(mCityId).get(i).getWindStrength()));
                }
                ((ForecastViewHolder) holder).forecastLinear.setVisibility(View.INVISIBLE);
                ((ForecastViewHolder) holder).forecastDetail.setVisibility(View.VISIBLE);
            } else if (v.getId() == R.id.suggestion_view_id + i) {
                if (mMaterialWeatherDB.loadWeatherIndexes(mCityId).size() > 0) {
                    ((SuggestionViewHolder) holder).suggestionDetailIcon.setImageResource(
                            Utility.suggestionIconMap.get(
                                    mMaterialWeatherDB.loadWeatherIndexes(mCityId).get(i).getName()));
                    ((SuggestionViewHolder) holder).suggestionDetailInfo.setText(
                            String.format("%s - %s",
                                    mMaterialWeatherDB.loadWeatherIndexes(mCityId).get(i).getName(),
                                    mMaterialWeatherDB.loadWeatherIndexes(mCityId).get(i).getIndex()));
                    ((SuggestionViewHolder) holder).suggestionDetailIntroduction.setText(
                            mMaterialWeatherDB.loadWeatherIndexes(mCityId).get(i).getDetails());
                }
                ((SuggestionViewHolder) holder).suggestionGrid.setVisibility(View.INVISIBLE);
                ((SuggestionViewHolder) holder).suggestionRelative.setVisibility(View.VISIBLE);
            } else if (v.getId() == R.id.forecast_detail) {
                ((ForecastViewHolder) holder).forecastLinear.setVisibility(View.VISIBLE);
                ((ForecastViewHolder) holder).forecastDetail.setVisibility(View.INVISIBLE);
            } else if (v.getId() == R.id.suggestion_detail) {
                ((SuggestionViewHolder) holder).suggestionGrid.setVisibility(View.VISIBLE);
                ((SuggestionViewHolder) holder).suggestionRelative.setVisibility(View.INVISIBLE);
            } else {
                Snackbar.make(v, "" + v.getId(), Snackbar.LENGTH_SHORT).show();
            }
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (holder instanceof ForecastViewHolder) {
            try {
                ((ForecastViewHolder) holder).forecastDetail
                        .setOnClickListener(new MyOnClickListener(holder, 0));
                for (int i = 0; i < 6; i++) {
                    ((ForecastViewHolder) holder).forecastLinear
                            .getChildAt(i).setOnClickListener(new MyOnClickListener(holder, i));
                    if (mMaterialWeatherDB.loadRecentWeatherInfo(mCityId).size() > 0) {
                        if (i == 0) {
                            ((ForecastViewHolder) holder).forecastDate[i].setText("昨日");
                        } else if (i == 1) {
                            ((ForecastViewHolder) holder).forecastDate[i].setText("今日");
                        } else {
                            ((ForecastViewHolder) holder).forecastDate[i].setText(
                                    String.format("周%s", mMaterialWeatherDB
                                            .loadRecentWeatherInfo(mCityId).get(i).getWeek()));
                        }
                        ((ForecastViewHolder) holder).forecastIcon[i].setImageResource(
                                Utility.weatherIconMap.get(
                                        mMaterialWeatherDB.loadRecentWeatherInfo(mCityId).get(i).getType()));
                        substring = mMaterialWeatherDB.loadRecentWeatherInfo(mCityId).get(i).getLowTemp();

                        ((ForecastViewHolder) holder).forecastTemp[i].setText(String.format("%s°/%s°",
                                mMaterialWeatherDB.loadRecentWeatherInfo(mCityId).get(i).getLowTemp(),
                                mMaterialWeatherDB.loadRecentWeatherInfo(mCityId).get(i).getHighTemp()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                //Log.e(TAG, e.toString());
            }
        }

        if (holder instanceof CurrentWeatherViewHolder) {
            try {
                ((CurrentWeatherViewHolder) holder).currentWeatherText.setText(
                        mMaterialWeatherDB.loadCurrentWeatherInfo(mCityId).get(0).getWeather());
                ((CurrentWeatherViewHolder) holder).currentWeatherIcon.setImageResource(
                        Utility.weatherIconMap.get(
                                mMaterialWeatherDB.loadCurrentWeatherInfo(mCityId).get(0).getWeather()));
                ((CurrentWeatherViewHolder) holder).currentTempText.setText(
                        mMaterialWeatherDB.loadCurrentWeatherInfo(mCityId).get(0).getCurTemp());
                ((CurrentWeatherViewHolder) holder).sunRiseText.setText(
                        mMaterialWeatherDB.loadCurrentWeatherInfo(mCityId).get(0).getSunRise());
                ((CurrentWeatherViewHolder) holder).sunSetText.setText(
                        mMaterialWeatherDB.loadCurrentWeatherInfo(mCityId).get(0).getSunSet());
                ((CurrentWeatherViewHolder) holder).airQualityText.setText(
                        mMaterialWeatherDB.loadCurrentWeatherInfo(mCityId).get(0).getCurPM());
                ((CurrentWeatherViewHolder) holder).windDirectionText.setText(
                        mMaterialWeatherDB.loadCurrentWeatherInfo(mCityId).get(0).getCurWindDirection());
            } catch (Exception e) {
                e.printStackTrace();
                //Log.e(TAG, e.toString());
            }
        }

        if (holder instanceof SuggestionViewHolder) {
            try {
                ((SuggestionViewHolder) holder).suggestionRelative
                        .setOnClickListener(new MyOnClickListener(holder, 0));
                for (int i = 0; i < 6; i++) {
                    ((SuggestionViewHolder) holder).suggestionGrid
                            .getChildAt(i).setOnClickListener(new MyOnClickListener(holder, i));
                    if (mMaterialWeatherDB.loadWeatherIndexes(mCityId).size() > 0) {
                        ((SuggestionViewHolder) holder).suggestionIcon[i].setImageResource(
                                Utility.suggestionIconMap.get(
                                        mMaterialWeatherDB.loadWeatherIndexes(mCityId).get(i).getName()));
                        ((SuggestionViewHolder) holder).suggestionType[i].setText(
                                mMaterialWeatherDB.loadWeatherIndexes(mCityId).get(i).getName());
                        ((SuggestionViewHolder) holder).suggestionInfo[i].setText(
                                mMaterialWeatherDB.loadWeatherIndexes(mCityId).get(i).getIndex());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                //Log.e(TAG, e.toString());
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return 3;
    }


    /**
     * 最近天气
     */
    class ForecastViewHolder extends MyAdapter.ViewHolder {

        private LinearLayout forecastDetail;
        private ImageView forecastDetailIcon;
        private TextView forecastDetailDate;
        private TextView forecastDetailTemp;
        private TextView forecastDetailInfo;

        private LinearLayout forecastLinear;
        private TextView[] forecastDate = new TextView[6];
        private TextView[] forecastTemp = new TextView[6];
        private ImageView[] forecastIcon = new ImageView[6];

        public ForecastViewHolder(View itemView) {
            super(itemView);

            forecastDetail = (LinearLayout) itemView.findViewById(R.id.forecast_detail);
            forecastDetailIcon = (ImageView) itemView.findViewById(R.id.forecast_detail_icon);
            forecastDetailDate = (TextView) itemView.findViewById(R.id.forecast_detail_date);
            forecastDetailTemp = (TextView) itemView.findViewById(R.id.forecast_detail_temp);
            forecastDetailInfo = (TextView) itemView.findViewById(R.id.forecast_detail_info);


            forecastLinear = (LinearLayout) itemView.findViewById(R.id.forecast_linear);
            for (int i = 0; i < 6; i++) {
                View view = View.inflate(mContext, R.layout.item_forecast_line, null);
                view.setId(R.id.forecast_view_id + i);
                view.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT
                        , LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
                forecastDate[i] = (TextView) view.findViewById(R.id.forecast_date);
                forecastTemp[i] = (TextView) view.findViewById(R.id.forecast_temp);
                forecastIcon[i] = (ImageView) view.findViewById(R.id.forecast_icon);
                forecastLinear.addView(view);
            }
        }
    }

    /**
     * 当日信息
     */
    class CurrentWeatherViewHolder extends MyAdapter.ViewHolder {

        private TextView currentWeatherText;
        private TextView currentTempText;
        private TextView sunRiseText;
        private TextView sunSetText;
        private TextView airQualityText;
        private TextView windDirectionText;
        private ImageView currentWeatherIcon;

        public CurrentWeatherViewHolder(View itemView) {
            super(itemView);

            currentWeatherText = (TextView) itemView.findViewById(R.id.current_weather_text);
            currentWeatherIcon = (ImageView) itemView.findViewById(R.id.current_weather_icon);
            currentTempText = (TextView) itemView.findViewById(R.id.current_temp_text);
            sunRiseText = (TextView) itemView.findViewById(R.id.sun_rise_text);
            sunSetText = (TextView) itemView.findViewById(R.id.sun_set_text);
            airQualityText = (TextView) itemView.findViewById(R.id.air_quality_text);
            windDirectionText = (TextView) itemView.findViewById(R.id.wind_direction_text);
        }
    }

    /**
     * 建议指数
     */
    class SuggestionViewHolder extends MyAdapter.ViewHolder {

        private RelativeLayout suggestionRelative;
        private ImageView suggestionDetailIcon;
        private TextView suggestionDetailInfo;
        private TextView suggestionDetailIntroduction;
        private GridLayout suggestionGrid;
        private TextView[] suggestionType = new TextView[6];
        private TextView[] suggestionInfo = new TextView[6];
        private ImageView[] suggestionIcon = new ImageView[6];

        public SuggestionViewHolder(View itemView) {
            super(itemView);

            suggestionRelative = (RelativeLayout) itemView.findViewById(R.id.suggestion_detail);
            suggestionDetailIcon = (ImageView) itemView.findViewById(R.id.suggestion_detail_icon);
            suggestionDetailInfo = (TextView) itemView.findViewById(R.id.suggestion_detail_info);
            suggestionDetailIntroduction = (TextView) itemView.findViewById(R.id.suggestion_detail_introduction);

            suggestionGrid = (GridLayout) itemView.findViewById(R.id.suggestion_grid);
            for (int i = 0; i < 6; i++) {
                View view = View.inflate(mContext, R.layout.item_suggestion_grid, null);
                view.setId(R.id.suggestion_view_id + i);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.setMargins(90, 15, 0, 15);//设置边距
                view.setLayoutParams(params);
                suggestionType[i] = (TextView) view.findViewById(R.id.suggestion_type);
                suggestionInfo[i] = (TextView) view.findViewById(R.id.suggestion_info);
                suggestionIcon[i] = (ImageView) view.findViewById(R.id.suggestion_icon);
                suggestionGrid.addView(view);
            }
        }
    }
}
