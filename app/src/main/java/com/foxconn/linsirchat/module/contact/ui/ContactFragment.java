package com.foxconn.linsirchat.module.contact.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.foxconn.linsirchat.MyApplication;
import com.foxconn.linsirchat.R;
import com.foxconn.linsirchat.base.BaseFragment;
import com.foxconn.linsirchat.common.adapter.CommonReclycleViewAdapter;
import com.foxconn.linsirchat.module.contact.bean.UserInfoBean;
import com.foxconn.linsirchat.module.conversation.ui.ChatRoomActivity;
import com.lidroid.xutils.exception.DbException;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/5.
 */
public class ContactFragment extends BaseFragment {

    private PullLoadMoreRecyclerView mprcv;
    private List<UserInfoBean> mList = new ArrayList<UserInfoBean>();
    private CommonReclycleViewAdapter<UserInfoBean> mAdapter;

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
        mAdapter = new CommonReclycleViewAdapter<UserInfoBean>(getActivity(), mList, R.layout.layout_contact_list_item) {
            @Override
            public void convert(CommonReclycleViewAdapter.MyHolder helper, final int position, UserInfoBean item) {
                helper.mItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserInfoBean iUser = mList.get(position);
                        Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("nick", iUser.getNick());
                        bundle.putString("tel", iUser.getTel());
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                    }
                });
                ((TextView) helper.mItemView.findViewById(R.id.tv_item_name)).setText(item.getNick());

            }
        };

        mprcv.setLinearLayout();
        mprcv.setAdapter(mAdapter);
        mprcv.setPushRefreshEnable(false);

    }

    @Override
    protected void initEvent() {
        mprcv.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {

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

    }

    // 刷新数据
    private void refreshData() {
        try {
            mList = MyApplication.mDbUtils.findAll(UserInfoBean.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (mList != null)
            mAdapter.refresh(mList);
    }

    @Override
    protected void loadData() {
        refreshData();
    }
}
