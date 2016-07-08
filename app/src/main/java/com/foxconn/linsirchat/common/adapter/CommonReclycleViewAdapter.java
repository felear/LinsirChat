package com.foxconn.linsirchat.common.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxconn.linsirchat.R;
import com.foxconn.linsirchat.module.contact.bean.UserInfoBean;
import com.foxconn.linsirchat.module.conversation.ui.ChatRoomActivity;

import java.util.List;

/**
 * Created by Administrator on 2016/7/5.
 */
public abstract class CommonReclycleViewAdapter<T> extends RecyclerView.Adapter<CommonReclycleViewAdapter.MyHolder> {

    private int mItemLayoutId;
    private List<T> mList;
    private Context mContext;
    private int mCountSum = -1;

    public CommonReclycleViewAdapter(Context context, List<T> list, int itemLayoutId) {
        mContext = context;
        mList = list;
        mItemLayoutId = itemLayoutId;
    }

    /**
     * 定义itemCount
     */
    public CommonReclycleViewAdapter setCount(int i) {
        mCountSum = i;
        this.notifyDataSetChanged();
        return this;
    }

    /**
     * 替换元素并刷新
     *
     * @param list
     */
    public void refresh(List<T> list) {
        mList = list;
        this.notifyDataSetChanged();
    }

    /**
     * 删除元素并更新
     *
     * @param position
     */
    public void deleteList(int position) {
        mList.remove(position);
        this.notifyDataSetChanged();
    }

    @Override
    public CommonReclycleViewAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(mItemLayoutId, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(CommonReclycleViewAdapter.MyHolder holder,int position) {
        if (position >= mList.size()) {
            convert(holder, position, mList.get(position % mList.size()));
        } else {
            convert(holder, position, mList.get(position));
        }
    }

    public abstract void convert(CommonReclycleViewAdapter.MyHolder helper,final int position, T item);

    @Override
    public int getItemCount() {

        if (mCountSum == -1) {
            return mList == null ? 0 : mList.size();
        } else {
            return (mList == null || mList.size() < 1) ? 0 : mCountSum;
        }
    }


    protected class MyHolder extends RecyclerView.ViewHolder {

        public View mItemView;

        public MyHolder(final View itemView) {
            super(itemView);
            mItemView = itemView;
        }
    }

}
