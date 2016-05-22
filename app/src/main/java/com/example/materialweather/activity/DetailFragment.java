package com.example.materialweather.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.materialweather.R;
import com.example.materialweather.adapter.MyAdapter;
import com.example.materialweather.db.MaterialWeatherDB;
import com.example.materialweather.util.HttpCallbackListener;
import com.example.materialweather.util.HttpUtil;
import com.example.materialweather.util.Utility;


/**
 * Created by GavinAndre on 2016/5/6.
 */


public class DetailFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private MaterialWeatherDB mMaterialWeatherDB;
    private SwipeRefreshLayout mSwipeLayout;
    private View view;
    private String weatherCode;

    public static DetailFragment newInstance(String cityId) {
        Bundle args = new Bundle();
        DetailFragment fragment = new DetailFragment();
        args.putString("cityId", cityId);
        fragment.setArguments(args);
        //Log.e("DetailFragment","newInstance");
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater
            , ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_detail, container, false);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mAdapter = new MyAdapter(getActivity(), getArguments().getString("cityId"));
            mRecyclerView.setAdapter(mAdapter);

            mMaterialWeatherDB = MaterialWeatherDB.getInstance(getActivity());

            mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
            mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light
                    , android.R.color.holo_red_light
                    , android.R.color.holo_orange_light
                    , android.R.color.holo_green_light);

            mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            queryWeatherCode(getArguments().getString("cityId"));
                            queryRecentWeatherInfo(getArguments().getString("cityId"));
                        }
                    }, 2500);
                }
            });
            Log.e("DetailFragment", "onCreateView");
        }

        return view;
    }

    /**
     * 查询县级代号所对应的天气代号
     */
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" +
                countyCode + ".xml";
        queryFromServer(address, "countyCode", countyCode);
    }

    /**
     * 查询天气代号所对应的天气
     */
    private void queryRecentWeatherInfo(String weatherCode) {
        String address = "http://apis.baidu.com/apistore/weatherservice/recentweathers" +
                "?cityid=" + weatherCode;
        queryFromServer(address, "recentWeather", weatherCode);
    }

    private void queryCurrentWeatherInfo(String weatherCode) {
        String address = "http://apis.baidu.com/apistore/weatherservice/cityid" +
                "?cityid=" + weatherCode;
        queryFromServer(address, "currentWeather", weatherCode);
    }

    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
     */
    private void queryFromServer(final String address, final String type, final String cityId) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                /*if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        //从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            weatherCode = array[1];
                            queryRecentWeatherInfo(weatherCode);
                        }
                    }
                } else */
                if ("recentWeather".equals(type)) {
                    Log.i("Weather_response", response);
                    //处理服务器返回的天气信息
                    Utility.handleRecentWeathersResponse(mMaterialWeatherDB,
                            response);
                    queryCurrentWeatherInfo(cityId);
                } else if ("currentWeather".equals(type)) {
                    Log.i("Weather_response", response);
                    //处理服务器返回的天气信息
                    Utility.handleCurrentWeatherResponse(mMaterialWeatherDB,
                            response);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //showWeather();
                            mAdapter.notifyDataSetChanged();
                            mSwipeLayout.setRefreshing(false);
                            Snackbar.make(view, "同步成功", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //publishText.setText("同步失败");
                        Snackbar.make(view, "同步失败", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
