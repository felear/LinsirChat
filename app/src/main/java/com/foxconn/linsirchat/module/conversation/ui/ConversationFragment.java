package com.foxconn.linsirchat.module.conversation.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.foxconn.linsirchat.MyApplication;
import com.foxconn.linsirchat.R;
import com.foxconn.linsirchat.base.BaseFragment;
import com.foxconn.linsirchat.common.adapter.CommonReclycleViewAdapter;
import com.foxconn.linsirchat.common.constant.Constant;
import com.foxconn.linsirchat.module.contact.bean.UserInfoBean;
import com.foxconn.linsirchat.module.conversation.bean.MsgBean;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/7/5.
 */
public class ConversationFragment extends BaseFragment {

    @ViewInject(R.id.rcv_conversation)
    private RecyclerView mrcv;
    @ViewInject(R.id.srflayout_conversation)
    private SwipeRefreshLayout msrfLayout;
    private CommonReclycleViewAdapter<UserInfoBean> mAdapter;
    private List<UserInfoBean> mList = new ArrayList<>();
    private BroadcastReceiver mLocalBroadcastReceiver;

    @Override
    protected int setViewId() {
        return R.layout.fragment_conversation;
    }

    @Override
    protected void findViews(View view) {
        ViewUtils.inject(this, view);
    }

    @Override
    protected void init() {

        // 设置ReclycleVeiw布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mrcv.setLayoutManager(linearLayoutManager);

        // 设置适配器
        mAdapter = new CommonReclycleViewAdapter<UserInfoBean>(getActivity(), mList, R.layout.layout_conversation_list_item) {
            @Override
            public void convert(CommonReclycleViewAdapter.MyHolder helper, final int position,final UserInfoBean item) {

                // 长安item事件监听
                helper.mItemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        new AlertDialog.Builder(getActivity())
                                .setItems(new String[]{"置顶聊天", "删除该聊天"}, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            // 置顶

                                        }
                                        if (which == 1) {
                                            // 删除该聊天记录
                                            mAdapter.deleteList(position);
                                            item.setTime(null);
                                            item.setBody(null);

                                            // 删除数据库中的记录
                                            try {
                                                MyApplication.mDbUtils.update(item,
                                                        WhereBuilder.b("id","=",item.getId()));
                                            } catch (DbException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    }
                                })
                                .show();

                        return true;
                    }
                });

                // 短按item事件监听
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
                ((TextView) helper.mItemView.findViewById(R.id.tv_item_body)).setText(item.getBody());
                ((TextView) helper.mItemView.findViewById(R.id.tv_item_name)).setText(item.getNick());
                ((TextView) helper.mItemView.findViewById(R.id.tv_item_time)).setText(formatTime(item.getTime()));
            }
        };

        mrcv.setAdapter(mAdapter);

    }

    // 刷新消息列表
    private void refreshList() {
        try {
            mList = MyApplication.mDbUtils.findAll(Selector.from(UserInfoBean.class).where("time", ">", 0).orderBy("time", true));
        } catch (DbException e) {
            e.printStackTrace();
        }
        // 替换list
        if (mList != null)
            mAdapter.refresh(mList);

    }

    // 格式化时间
    private String formatTime(String time) {
        long l = Long.parseLong(time);

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c2.setTime(new Date());
        c1.setTimeInMillis(l);

        if (c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR) == 0) {
            return new SimpleDateFormat("HH:mm").format(l);
        } else if (c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR) == 1) {
            return new SimpleDateFormat("昨天 HH:mm").format(new Date(l));
        } else if (c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR) == 2) {
            return new SimpleDateFormat("前天 HH:mm").format(new Date(l));
        } else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(l));
        }
    }

    @Override
    protected void initEvent() {
        // 下拉列表事件监听
        msrfLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        msrfLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        // 接受消息列表变化监听
        mLocalBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshList();
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mLocalBroadcastReceiver, new IntentFilter(Constant.BROAD_URI));

    }

    @Override
    protected void loadData() {
        // 初始打开时加载列表
        refreshList();
    }

    @Override
    public void onDestroy() {
        // 取消广播注册
        if (mLocalBroadcastReceiver != null)
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mLocalBroadcastReceiver);
        super.onDestroy();
    }

    //每次重新加载都刷新一次列表
    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }
}
