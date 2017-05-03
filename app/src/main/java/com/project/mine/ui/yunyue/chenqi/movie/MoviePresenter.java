package com.project.mine.ui.yunyue.chenqi.movie;

import com.project.mine.bean.request.RequestParamsBean;
import com.project.mine.bean.yunyue.movie.MovieBean;
import com.project.mine.bean.yunyue.movie.SubjectsBean;
import com.project.mine.data.net.AppManager;
import com.project.mine.util.JsonUtil;
import com.project.mine.util.rx.TransformUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by chenqi on 2017/4/25.
 * 描述:
 */
public class MoviePresenter implements MovieContract.Presenter {

    private MovieContract.View mView;


    public MoviePresenter(MovieContract.View view) {
        mView = view;
        mView.initPresenter(this);
    }

    @Override
    public Subscription requestData(RequestParamsBean params) {
        return  AppManager.getInstance().getDouBanService().getHotMovie()
                .compose(TransformUtils.<ResponseBody>defaultSchedulers())
                .map(json2ListData)
                .subscribe(new Action1<List<SubjectsBean>>() {
                    @Override
                    public void call(List<SubjectsBean> subjectsBeanList) {
                        mView.showResult(subjectsBeanList);
                        mView.onLoadSuccess();
                    }
                },mOnError);
    }

    private Func1<ResponseBody, List<SubjectsBean>> json2ListData = new Func1<ResponseBody, List<SubjectsBean>>() {
        @Override
        public List<SubjectsBean> call(ResponseBody response) {
            return parseRequestData(response);
        }
    };


    private List<SubjectsBean> parseRequestData(ResponseBody response) {
        MovieBean movieBean = null;
        try {
            String string = response.string();
            movieBean = JsonUtil.parseJson(string, MovieBean.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieBean.subjects;
    }


    //加载失败
    private Action1<Throwable> mOnError = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            mView.onLoadError(getClass().getSimpleName(), throwable);
        }
    };

    @Override
    public void start() {
        mView.onLoading();
    }
}
