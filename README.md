# rxstudy
## 도서는 Rx Java Essentials(에이콘 출판사) 로 학습하였습니다. 위 소스 예제는 Rx Java Essentials 셈플 소스에서 개인적인 학습으로 사용되었습니다.
# 기본
## Observable.create()
- call() 함수에서 발행을 구현할 수 있다

<pre><code>
//자바 표현식
Observable.create(new Observable.OnSubscribe<AppInfo>() {
            @Override
            public void call(Subscriber<? super AppInfo> subscriber) {
                subscriber.onNext(new AppInfo(name, iconPath, appInfo.getLastUpdateTime()));
                subscriber.onCompleted();
            }
        });
//람다 표현식
Observable.create(subscriber -> { } );

</code></pre>

## Observable.just()
- 기존 코드를 Obserable로 변환
- 아래 예시는 onNext() 가 3번 불린다.
<pre><code>
        Observable<AppInfo> observable = Observable.just(appOne, appTwo, appThree);
        observable.subscribe(appInfo -> {
                    mAddedApps.add(appInfo);
                    mAdapter.addApplication(mAddedApps.size() - 1, appInfo);
                    L.d("onNext->appInfo=" + appInfo);
                }, error -> {
                    L.d("onError->error=" + error);
                }, () -> {
                    L.d("onCompleted");
                }
        );
</code></pre>

## Observable.repeat(), defer(), range(), interval(), timer()
- 반복, 구독전까지 옵저버블을 미루고 싶을때, X개중 N개 발행시, 3초동안 주기적으로, 3초 후 발행

## 필터링
- filter() 로 리스트 중 원하는 조건일때 바로 발행된다
<pre><code>
Observable.from(apps)
                .filter((appInfo) -> {
                    //true인 경우 onNext() 호출. 아닌경우 onNext()는 호출되지 않음
                    L.d("filter->"+appInfo.getName());
                    return appInfo.getName().startsWith("C");
                })
                .subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onCompleted() {
                        L.d("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(AppInfo appInfo) {
                        L.d("onNext->appInfo="+appInfo);
                    }
                });
//아래처럼 null 객체를 필터링하여 구독자는 별도의 null 값을 체크할 필요없이 앱로직만 신경쓰면된다.(분리/추상화)
ArrayList<AppInfo> testList = new ArrayList<>();
        testList.add(null);
        testList.add(new AppInfo("aaaa", "bbbb", 1234));
        Observable.from(testList)
                .filter(new Func1<AppInfo, Boolean>() {
                    @Override
                    public Boolean call(AppInfo appInfo) {
                        L.d("filter->"+(appInfo == null ? "isNull" : "isNotNull"));
                        return appInfo != null;
                    }
                })
                .subscribe(new Observer<AppInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(AppInfo appInfo) {
                        L.d("onNext->appInfo="+appInfo);
                    }
                });
</code></pre>

## take(), takeLast()
- 처음 2개 요소만 발행
- 마지막 1개 요소만 발행

##distinct(), distinctUntilChanged()
- 중복 제거
- 이전에 발행했던 값과 다른 새로운 값을 발행한 경우에만 onNext() 호출

## first(), last()
- 첫번째 한번 만
- 마지막 한번 만

## skip(), skipLast()
- skip(2) : 2개를 건너뛴 후 나머지는 발행
- skipLast(2) : 마지막 2개
- 목적에 부합하지 않는 제한해야 할 요소로 시작하거나 끝나는 경우

## elementAt(), sample()
- 특정 번째 요소만 발행시
- 지정한 시간 간격에서 최근 아이템을 발행한 것중 마지막 아이템을 발행하는 옵저버블 생성

## timeout()
- 지정한 시간내에 아무런 값을 바지 못할 경우 에러(onError)
- 타임아웃 후 도작한 아이템은 발행되지 않음

## debounce()
- 옵저버블이 아이템을 발행한 후 일정 시간 동안 발행되지 않은경우 마지막 아이템을 발행한다
- 일정 시간이 끝나기전 아이템이 발행되면 내부 타이머는 재시작된다

## map()
- 새로운 observable 을 생성한다
- 1:1, 동기

## flatMap()
- Observable 을 분해하여 1개의 observable 을 생성한다
- 배열을 갖고있는 Observble을 분해하여 각각의 문자로 발행할수 있다
- 에러 발생시 onError()를 발생시키고 중지된다
- merge()사용. 비동기

## concatMap()
- flatMap()은 입력즉시 발행되어 출력되는 순서가 달라질수 있지만, concatMap()은 입력된 항목 처리가 완료된 후 다음 항목을 처리하기때문에 입력한 그대로 발행되어 순서가 보장된다
- concat() 사용, 동기

## scan()
- 누적해서 비교한다

## flatMapIterable(), switchMap()
- ...

## groupBy()
- 같은 종류를 묶을수 있다

## buffer(), window(), cast()
- ...

## observeOn(), subscribeOn()
- subscribeOn: observable의 작업을 시작하는 쓰레드 지정
- observeOn: subscribe 의 쓰레드 또는 observeOn 이후에 나오는 오퍼레이터의 스케줄러 지정
- 쓰레드 스케줄러는 링크 참고 ([http://tiii.tistory.com/18](http://tiii.tistory.com/18))



# 추가 학습 링크
- [https://realm.io/kr/news/rxandroid/](https://realm.io/kr/news/rxandroid/)
기본설명
- [http://pluu.github.io/blog/rx/2015/04/29/rxjava/](http://pluu.github.io/blog/rx/2015/04/29/rxjava/)
map, flatMap, merge 등 간단한 설명
- [http://kunny.github.io/community/2016/02/08/gdg_korea_android_weekly_02_1/](http://kunny.github.io/community/2016/02/08/gdg_korea_android_weekly_02_1/) gdg 스터디 문서(flatMap, concatMap),
[http://blog.naver.com/PostView.nhn?blogId=tmondev&logNo=220591733135](http://blog.naver.com/PostView.nhn?blogId=tmondev&logNo=220591733135) (flatMap, concatMap)


