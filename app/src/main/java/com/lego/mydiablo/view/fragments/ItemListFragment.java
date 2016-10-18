package com.lego.mydiablo.view.fragments;


import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.lego.mydiablo.R;
import com.lego.mydiablo.data.RealmDataController;
import com.lego.mydiablo.data.model.Hero;
import com.lego.mydiablo.logic.Core;
import com.lego.mydiablo.logic.ProgressBar;
import com.lego.mydiablo.utils.Settings;
import com.lego.mydiablo.view.adapters.ClassAdapter;
import com.lego.mydiablo.view.adapters.PaginateLoadingListItemCreator;
import com.lego.mydiablo.view.adapters.RegionAdapter;
import com.lego.mydiablo.view.adapters.SeasonAdapter;
import com.lego.mydiablo.view.adapters.TableItemRecyclerViewAdapter;
import com.paginate.Paginate;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.Unbinder;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import rx.Subscriber;

import static com.lego.mydiablo.utils.Const.SIZE;
import static com.lego.mydiablo.utils.Const.START_PROGRESS_VALUE;
import static com.lego.mydiablo.utils.Settings.mItemsPerPage;
import static com.lego.mydiablo.utils.Settings.mMode;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListFragment extends Fragment implements ProgressBar, Paginate.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

    @BindView(R.id.idClass)
    Spinner mClassSpinner;
    @BindView(R.id.idSeason)
    Spinner mSeasonSpinner;
    @BindView(R.id.idRegion)
    Spinner mRegionSpinner;
    @BindView(R.id.progressBar)
    android.widget.ProgressBar mProgressBar;
    @BindView(R.id.back_button)
    Button mBackBtn;
    @BindView(R.id.item_list)
    RecyclerView mRecyclerView;

    private MenuCallBack menuCallBack;
    private Core mCore;
    private Unbinder mUnbinder;
    private RealmDataController mRealmDataController;
    private boolean mDoubleQuery = true;
    private boolean mEmptyData = true;
    private TableItemRecyclerViewAdapter mTableItemRecyclerViewAdapter;
    private boolean mLoading = false;
    private int mPage;
    private Paginate mPaginate;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mPaginate.unbind();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealmDataController = RealmDataController.with(this);
        mCore = Core.getInstance(this.getActivity(), mRealmDataController);
        mCore.forCheckState(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_item_list, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Settings.mTwoPane = true;
        }

        menuCallBack = (MenuCallBack) getActivity();    //callbacks
        mCore.forCheckState(this);

        /**Видим кнопку назад, если нет второго фрагмента */
        if (Settings.mTwoPane) {
            mBackBtn.setVisibility(View.GONE);
        } else {
            mBackBtn.setVisibility(View.VISIBLE);
        }

        final ClassAdapter classAdapter = new ClassAdapter(getContext(), R.layout.spinner, getResources().getStringArray(R.array.hero_class));
        mClassSpinner.setAdapter(classAdapter);
        mClassSpinner.getBackground().setColorFilter(getResources().getColor(R.color.btn_text), PorterDuff.Mode.SRC_ATOP);

        final SeasonAdapter seasonAdapter = new SeasonAdapter(getContext(), R.layout.spinner, getResources().getStringArray(R.array.season));
        mSeasonSpinner.setAdapter(seasonAdapter);
        mSeasonSpinner.getBackground().setColorFilter(getResources().getColor(R.color.btn_text), PorterDuff.Mode.SRC_ATOP);

        RegionAdapter regionAdapter = new RegionAdapter(getContext(), R.layout.spinner, getResources().getStringArray(R.array.region));
        mRegionSpinner.setAdapter(regionAdapter);

        setupRecyclerView(mRecyclerView);

        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new TableItemRecyclerViewAdapter(Collections.emptyList(), getContext()));
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Hero> heroList) {
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        mTableItemRecyclerViewAdapter = new TableItemRecyclerViewAdapter(heroList, getContext());
        recyclerView.setAdapter(mTableItemRecyclerViewAdapter);

        mPaginate = Paginate.with(recyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(new PaginateLoadingListItemCreator(recyclerView, mTableItemRecyclerViewAdapter))
                .setLoadingListItemSpanSizeLookup(() -> 1)
                .build();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @OnClick(R.id.back_button)
    void onButtonPressed(View v) {
        menuCallBack.MenuCallBackClick(v);
    }

    @OnItemSelected({R.id.idSeason, R.id.idClass})
    void onItemSelected() {
        if (mMode != null) {
            if (mDoubleQuery) {
                mPage = 0;
                mDoubleQuery = false;       //delay for api query
                if (mRealmDataController.getHeroList(mClassSpinner.getSelectedItem().toString(), mSeasonSpinner.getSelectedItem().toString()) != null) {  //get data from db, if db !=null
                    mEmptyData = false;
                    setupRecyclerView(mRecyclerView, mRealmDataController.getHeroList(mClassSpinner.getSelectedItem().toString(), mSeasonSpinner.getSelectedItem().toString()));
                } else {
                    mPage = 0;
                    mEmptyData = true;
                }
                new Handler().postDelayed(() -> mDoubleQuery = true, 2000);
                mCore.doRequest(mClassSpinner.getSelectedItem().toString(), mSeasonSpinner.getSelectedItem().toString()).subscribe(new Subscriber<List<Hero>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        updateProgressBar(START_PROGRESS_VALUE, SIZE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.e("Request hero list", "onError: ", e);

                        if (e.getMessage().matches("40[1-3]{1}.*")){
                            Log.e("Request hero list", "onError: regex work fine");
                        }
                    }

                    @Override
                    public void onNext(List<Hero> heroList) {
                        if (mEmptyData) {
                            setupRecyclerView(mRecyclerView, heroList);
                            mEmptyData = false;
                        } else {
                            mTableItemRecyclerViewAdapter.setItems(heroList);
                        }
                        updateProgressBar(SIZE, SIZE);
                    }
                });
            }
        }
    }

    @Override
    public void updateProgressBar(int value, int size) {
        double progress = 0;
        if (mProgressBar.getVisibility() == View.GONE) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            progress = (value / 10);
            mProgressBar.setProgress((int) (progress));
        }
        if ((int) progress == 100) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoadMore() {
        if (!mEmptyData) {
            Log.d("Paginate", "onLoadMore" + mPage);
            mLoading = true;
            new Handler().postDelayed(this::loadMore, 2000);
        }
    }

    private void loadMore() {
        mPage += mItemsPerPage;
        mTableItemRecyclerViewAdapter.add(mRealmDataController.getNextHero(mClassSpinner.getSelectedItem().toString(),
                mSeasonSpinner.getSelectedItem().toString(), mPage));
        mLoading = false;
    }

    @Override
    public boolean isLoading() {
        return mLoading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        if (mEmptyData) {
            return mPage == 0;
        } else {
            return mPage == SIZE;
        }
    }
}