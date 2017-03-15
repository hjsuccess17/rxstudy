package com.packtpub.apps.rxjava_essentials.chapter5;

import com.packtpub.apps.rxjava_essentials.L;
import com.packtpub.apps.rxjava_essentials.apps.ApplicationsList;
import com.packtpub.apps.rxjava_essentials.R;
import com.packtpub.apps.rxjava_essentials.apps.AppInfo;
import com.packtpub.apps.rxjava_essentials.apps.ApplicationAdapter;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MapExampleFragment extends Fragment {

    @InjectView(R.id.fragment_first_example_list)
    RecyclerView mRecyclerView;

    @InjectView(R.id.fragment_first_example_swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private ApplicationAdapter mAdapter;

    private ArrayList<AppInfo> mAddedApps = new ArrayList<>();

    public MapExampleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_example, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mAdapter = new ApplicationAdapter(new ArrayList<>(), R.layout.applications_list_item);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.myPrimaryColor));
        mSwipeRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        // Progress
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setRefreshing(true);
        mRecyclerView.setVisibility(View.GONE);

        List<AppInfo> apps = ApplicationsList.getInstance().getList();

        loadList(apps);
    }

    private void loadList(List<AppInfo> apps) {
        mRecyclerView.setVisibility(View.VISIBLE);

        //1. map
        Observable.from(new String[]{"Hello", "world!"})
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        return s.length();
                    }
                })
                .subscribe(action -> {
                   L.d(">>>"+action);
                });

        //2. map
        Observable.from(apps)
                .map(new Func1<AppInfo, AppInfo>() {
                    @Override
                    public AppInfo call(AppInfo appInfo) {
                        String currentName = appInfo.getName();
                        String lowerCaseName = currentName.toLowerCase();
                        appInfo.setName(lowerCaseName);
                        L.d(">>>"+lowerCaseName);
                        return appInfo;
                    }
                })
                .subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onCompleted() {
                        L.d(">>>");
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "Something went south!", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(AppInfo appInfo) {
                        L.d(">>>"+appInfo);
                        mAddedApps.add(appInfo);
                        mAdapter.addApplication(mAddedApps.size() - 1, appInfo);
                    }
                });

        //3. flatMap
        String[][] array2 = { {"hello", "world"}, {"goodbye", "world!"}};
        Observable.from(array2)
                .flatMap(array -> {
                    L.d(">>>text="+array[0]+","+array[1]);

//                    String error = array[3]; //에러발생시 스트림은 끊긴다
                    return Observable.from(array);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> {
                   L.d(">>>text="+text);
                }, error -> {
                    L.d(">>>error");
                });


    }
}
