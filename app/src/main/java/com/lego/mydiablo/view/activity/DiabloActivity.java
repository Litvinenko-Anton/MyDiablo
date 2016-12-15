package com.lego.mydiablo.view.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.lego.mydiablo.R;
import com.lego.mydiablo.presenter.activity.DiabloPresenter;
import com.lego.mydiablo.presenter.activity.DiabloView;
import com.lego.mydiablo.utils.Settings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.lego.mydiablo.utils.Settings.mCurrentLocale;
import static com.lego.mydiablo.utils.Settings.mTwoPane;
import static com.lego.mydiablo.view.activity.LoginActivity.AUTH_CODE;

public class DiabloActivity extends MvpAppCompatActivity implements DiabloView {

    @InjectPresenter(type = PresenterType.GLOBAL)
    DiabloPresenter mDiabloPresenter;

    @BindView(R.id.main_container)
    FrameLayout mContainer;

    private Unbinder mUnbinder;
    private FragmentManager mFragmentManager;

    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diablo);
        mUnbinder = ButterKnife.bind(this);
        mCurrentLocale = Resources.getSystem().getConfiguration().locale.toString();
        mFragmentManager = getSupportFragmentManager();
        mDiabloPresenter.startConfig(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Snackbar.make(mContainer, R.string.doubleClick_backBtn, Snackbar.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("AuthDiablo", "onActivityResult: ololo worked!!!" + requestCode);
        Log.d("AuthDiablo", "onActivityResult: ololo worked!!!" + resultCode);
        if (resultCode == RESULT_OK && requestCode == AUTH_CODE) {
            Log.d("AuthDiablo", "onActivityResult: ololo worked!!!" + data.getIntExtra(LoginActivity.TAG_AUTH, 1));
        }
    }

    @Override
    public void showFragment(@IdRes int containerViewID, Fragment fragment, String tag) {
        mFragmentManager.beginTransaction()
                .replace(containerViewID, fragment, tag)
                .commit();
    }

    @Override
    public void checkOrientation() {
        mTwoPane = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_diablo);
        mDiabloPresenter.restoreScreen();
    }

}
