package com.lego.mydiablo.view.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lego.mydiablo.R;
import com.lego.mydiablo.data.model.Hero;
import com.lego.mydiablo.events.FragmentEvent;
import com.lego.mydiablo.logic.Core;
import com.lego.mydiablo.view.fragments.HeroTabsFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

import static com.lego.mydiablo.utils.Settings.mHeroId;

/**
 * @author Lego on 12.09.2016.
 */

public class TableItemRecyclerViewAdapter
        extends RecyclerView.Adapter<TableItemRecyclerViewAdapter.HeroViewHolder> {

    private List<Hero> mHeroList;
    private Context mContext;
    private Core mCore;
    private EventBus bus = EventBus.getDefault();

    public TableItemRecyclerViewAdapter(List<Hero> heroList, Context context) {
        mHeroList = new ArrayList<>(heroList);
        mContext = context;
        mCore = Core.getInstance();
    }

    @Override
    public HeroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_content, parent, false);
        return new HeroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HeroViewHolder holder, int position) {
        Hero hero = mHeroList.get(position);
        holder.mIdView.setText("#" + hero.getRank());
        holder.mContentView.setText(hero.getBattleTag());
        holder.mClassView.setImageDrawable(pickImage(hero.getHeroClass()));
        holder.mRankView.setText("Rift - " + hero.getRiftLevel());
        holder.mView.setTag(hero.getId());
        mHeroId = hero.getRank();
        holder.mView.setOnClickListener(v -> {
                    if (mCore.checkHeroData(hero.getRank())) {
                        bus.post(new FragmentEvent(HeroTabsFragment.newInstance(hero.getRank()), HeroTabsFragment.TAG)); //send to diablo activity
                    } else {
                        mCore.loadDetailHeroData(hero.getBattleTag(), hero.getId()).subscribe(new Subscriber<Hero>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Hero hero) {
                                bus.post(new FragmentEvent(HeroTabsFragment.newInstance(hero.getRank()), HeroTabsFragment.TAG)); //send to diablo activity
                            }
                        });
                    }
                }
        );
    }

    public void add(List<Hero> items) {
        int previousDataSize = this.mHeroList.size();
        if (!items.isEmpty()) {
            mHeroList.addAll(items);
            this.mHeroList.addAll(items);
            notifyItemRangeInserted(previousDataSize, items.size());
        }
    }

    private Drawable pickImage(String s) {
        switch (s) {
            case "demon hunter":
                return mContext.getResources().getDrawable(mContext.getResources().getIdentifier("dh", "drawable", mContext.getPackageName()));
            case "witch doctor":
                return mContext.getResources().getDrawable(mContext.getResources().getIdentifier("wd", "drawable", mContext.getPackageName()));
            default:
                return mContext.getResources().getDrawable(mContext.getResources().getIdentifier(s, "drawable", mContext.getPackageName()));
        }
    }

    public void setItems(List<Hero> heroList) {
        this.mHeroList = heroList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mHeroList.size();
    }

    class HeroViewHolder extends RecyclerView.ViewHolder {
        View mView;
        @BindView(R.id.id)
        TextView mIdView;
        @BindView(R.id.content)
        TextView mContentView;
        @BindView(R.id.rank)
        TextView mRankView;
        @BindView(R.id.idClass)
        ImageView mClassView;

        HeroViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, mView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

}
