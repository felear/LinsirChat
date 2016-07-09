package com.foxconn.linsirchat.module.contact.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxconn.linsirchat.MyApplication;
import com.foxconn.linsirchat.R;
import com.foxconn.linsirchat.base.BaseFragment;
import com.foxconn.linsirchat.common.adapter.CommonReclycleViewAdapter;
import com.foxconn.linsirchat.common.constant.Constant;
import com.foxconn.linsirchat.module.contact.bean.ConversationBean;
import com.foxconn.linsirchat.module.conversation.ui.ChatRoomActivity;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/5.
 */
public class ContactFragment extends BaseFragment {

    private PullLoadMoreRecyclerView mprcv;
    private List<ConversationBean> mList = new ArrayList<ConversationBean>();
    private CommonReclycleViewAdapter<ConversationBean> mAdapter;
    private BroadcastReceiver mLocalBroadcastReceiver;

    @Override
    protected int setViewId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void findViews(View view) {
        mprcv = (PullLoadMoreRecyclerView) view.findViewById(R.id.prcv_contact);
    }

    @Override
    protected void init() {

        // 设置适配器
        mAdapter = new CommonReclycleViewAdapter<ConversationBean>(getActivity(), mList, R.layout.layout_contact_list_item) {
            @Override
            public void convert(CommonReclycleViewAdapter.MyHolder helper, final int position, ConversationBean item) {
                helper.mItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConversationBean iUser = mList.get(position);
                        Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("nick", iUser.getNick());
                        bundle.putString("tel", iUser.getTel());
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                    }
                });
                ((TextView) helper.mItemView.findViewById(R.id.tv_item_name)).setText(item.getNick());

                // 设置头像
                String icon = item.getIcon();
                String gender = item.getGender();
                ImageView ivIcon = (ImageView) helper.mItemView.findViewById(R.id.iv_item_icon);
                if (icon == null) {
                    if (TextUtils.equals(gender, "男")) {
                        ivIcon.setImageResource(R.mipmap.icon_man);
                    } else {
                        ivIcon.setImageResource(R.mipmap.icon_woman2);
                    }
                }

            }
        };

        // 设置为垂直线性布局
        mprcv.setLinearLayout();
        mprcv.setAdapter(mAdapter);
        // 关闭上啦加载更多
        mprcv.setPushRefreshEnable(false);

    }

    @Override
    protected void initEvent() {
        // 设置下拉刷新监听
        mprcv.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                // 下拉刷新列表
                refreshData();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mprcv.setPullLoadMoreCompleted();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {

            }
        });

        // 接受消息列表变化监听
        mLocalBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 重新加载列表
                refreshData();
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mLocalBroadcastReceiver, new IntentFilter(Constant.BROAD_URI_CONTACT));

    }

    @Override
    public void onDestroy() {
        // 取消广播注册
        if (mLocalBroadcastReceiver != null)
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mLocalBroadcastReceiver);
        super.onDestroy();
    }

    // 刷新数据
    private void refreshData() {
        try {
            // 获得数据库中的所有联系人，按昵称排序
            mList = MyApplication.mDbUtils.findAll(Selector.from(ConversationBean.class)
            .orderBy("nick"));
        } catch (DbException e) {
            e.printStackTrace();
        }
        // 当获取的结果不为空时，更新适配器
        if (mList != null)
            mAdapter.refresh(mList);
    }

    @Override
    protected void loadData() {
        // 初次加载是更新数据
        refreshData();
    }
}
