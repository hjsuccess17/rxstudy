package com.packtpub.apps.rxjava_essentials.chapter7;


import com.packtpub.apps.rxjava_essentials.L;
import com.packtpub.apps.rxjava_essentials.R;
import com.packtpub.apps.rxjava_essentials.apps.AppInfo;
import com.packtpub.apps.rxjava_essentials.apps.ApplicationAdapter;
import com.packtpub.apps.rxjava_essentials.apps.ApplicationsList;

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
import rx.android.schedulers.HandlerThreadScheduler;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class LongTaskFragment extends Fragment {

    @InjectView(R.id.fragment_first_example_list)
    RecyclerView mRecyclerView;

    @InjectView(R.id.fragment_first_example_swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private ApplicationAdapter mAdapter;

    private ArrayList<AppInfo> mAddedApps = new ArrayList<>();

    public LongTaskFragment() {
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

        getObservableApps(apps)
                .onBackpressureBuffer() //옵저버블이 옵저버가 소비하는 것보다 더 빠르게 아이템을 발행하는 경우 옵저버블에게 아이템을 버퍼에 저장
                .subscribeOn(Schedulers.computation()) //기본 스케줄러-getObservableApps -> Observable (RxComputationThreadPool)
//                .subscribeOn(Schedulers.io()) //io 스레드(RxCachedThreadScheduler)
//                .subscribeOn(Schedulers.immediate()) //현재 스레드 (main 또는 RxComputationThreadPool... 앞에 설정한 스케줄러에 영향을 받는다)
//                .subscribeOn(Schedulers.newThread()) //새로운 스레드 (RxNewThreadScheduler)
//                .subscribeOn(Schedulers.trampoline()) //현재 스레드-큐 (main 또는 RxComputationThreadPool... 앞에 설정한 스케줄러에 영향을 받는다)
                .observeOn(AndroidSchedulers.mainThread()) //메인 스레드-subscribe
                .subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onCompleted() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "Here is the list!", Toast.LENGTH_LONG).show();
                        L.d(">>>getObservableApps->subscribe->onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onNext(AppInfo appInfo) {
                        mAddedApps.add(appInfo);
                        mAdapter.addApplication(mAddedApps.size() - 1, appInfo);
                        L.d(">>>getObservableApps>subscribe->onNext");
                    }
                });


        //trampoline 예제
        Action1<Integer> onNext = new Action1<Integer>() {
            @Override public void call(Integer integer) {
                L.d("Number=" + integer);
            }
        };
        Observable.just(2, 4, 6, 8, 10)
                .subscribeOn(Schedulers.trampoline())
//                .subscribeOn(Schedulers.io())
                .subscribe(onNext);
        Observable.just(1, 3, 5, 7, 9)
                .subscribeOn(Schedulers.trampoline())
//                .subscribeOn(Schedulers.io())
                .subscribe(onNext);
    }

    private Observable<AppInfo> getObservableApps(List<AppInfo> apps) {
        return Observable
                .create(subscriber -> {
                    L.d(">>>getObservableApps");
                    for (double i = 0; i < 1000000000; i++) {
                        double y = i * i;
                    }

                    for (AppInfo app : apps) {
                        subscriber.onNext(app);
                    }
                    subscriber.onCompleted();
                });
    }
}
