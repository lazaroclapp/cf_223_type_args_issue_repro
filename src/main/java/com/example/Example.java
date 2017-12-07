package com.example;

import io.reactivex.Single;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import java.util.concurrent.Callable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import io.reactivex.schedulers.Schedulers;

/*>>>import org.checkerframework.checker.tainting.qual.*;*/

class Example {

  Example() {
  }

  static final class Tuple<T, S> {
    private T v1;
    private S v2;

    public Tuple(T v1, S v2) {
      this.v1 = v1;
      this.v2 = v2;
    }

    public T getV1() {
      return v1;
    }

    public S getV2() {
      return v2;
    }
  }

  private Single<Tuple<Object, Object>> getSingleTuple() {
    return Single.fromCallable(new Callable<Tuple<Object, Object>>() {
      @Override
      public Tuple<Object, Object> call() throws Exception {
        return new Tuple<Object, Object>(new Object(), new Object());
      }
    });
  }

  public static final class TwoParamTupleMaybe<T, U> extends Maybe<Tuple<T, U>> {

    private final Maybe<Tuple<T, U>> mSourceMaybe;

    TwoParamTupleMaybe(Single<Tuple<T, U>> sourceSingle) {
      mSourceMaybe = sourceSingle.toMaybe();
    }

    @Override
    protected void subscribeActual(MaybeObserver<? super Tuple<T, U>> observer) {
      mSourceMaybe.subscribe(observer);
    }
  }

  public static <T, U>
  Function<Single<Tuple<T, U>>, TwoParamTupleMaybe<T, U>> singleTupleToTwoParamMaybe() {
    return new Function<Single<Tuple<T, U>>, TwoParamTupleMaybe<T, U>>() {
      @Override
      public TwoParamTupleMaybe<T, U> apply(Single<Tuple<T, U>> responseSingle) throws Exception {
        return new TwoParamTupleMaybe<>(responseSingle);
      }
    };
  }

  protected void errorExample() {
    requireUI(getSingleTuple()
        .observeOn(Schedulers.single())
        .to(Example.singleTupleToTwoParamMaybe())
        .doOnComplete(
            new Action() {
              @Override
              public void run() throws Exception {
              }
            }));
  }

  protected void noErrorExample() {
    requireUI(getSingleTuple()
        .observeOn(Schedulers.single())
        .to(Example.<Object,Object>singleTupleToTwoParamMaybe())
        .doOnComplete(
            new Action() {
              @Override
              public void run() throws Exception {
              }
            }));
  }

  private void requireUI(/*@Untainted*/ Maybe var) {
    // Don't care
  }

}

