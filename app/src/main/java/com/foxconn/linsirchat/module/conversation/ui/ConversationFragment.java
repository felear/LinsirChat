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
    private CommonReclycleViewAdapter<ConversationBean> mAdapter;
    private List<ConversationBean> mList = new ArrayList<>();
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
        mAdapter = new CommonReclycleViewAdapter<ConversationBean>(getActivity(), mList, R.layout.layout_conversation_list_item) {
            @Override
            public void convert(CommonReclycleViewAdapter.MyHolder helper, final int position, final ConversationBean item) {

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
                                                        WhereBuilder.b("id", "=", item.getId()));
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
                        ConversationBean iUser = mList.get(position);
                        Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("nick", iUser.getNick());
                        bundle.putString("tel", iUser.getTel());
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                    }
                });
                // 设置TextView控件内容
                ((TextView) helper.mItemView.findViewById(R.id.tv_item_body)).setText(item.getBody());
                ((TextView) helper.mItemView.findViewById(R.id.tv_item_name)).setText(item.getNick());
                ((TextView) helper.mItemView.findViewById(R.id.tv_item_time)).setText(formatTime(item.getTime()));

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

                TextView tvCount = (TextView) helper.mItemView.findViewById(R.id.tv_newMsg_num);
                String msgCount = getMsgCount(item.getTel());
                if (msgCount == null) {
                    tvCount.setVisibility(View.GONE);
                } else {
                    tvCount.setVisibility(View.VISIBLE);
                    tvCount.setText(msgCount);
                }

            }
        };

        mrcv.setAdapter(mAdapter);

    }

    private String getMsgCount(String tel) {

        List<MsgBean> list = null;
        try {
            list = MyApplication.mDbUtils.findAll(Selector.from(MsgBean.class)
                    .where(Constant.MSG_SENDER, "=", tel)
                    .and(Constant.MSG_IS_READ, "=", false));
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (list == null || list.size() < 1) {
            return null;
        } else {
            return list.size() + "";
        }

    }

    // 刷新消息列表
    private void refreshData() {
        try {
            mList = MyApplication.mDbUtils.findAll(Selector.from(ConversationBean.class).where("time", ">", 0).orderBy("time", true));
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

        String str = null;

        switch ((c1.get(Calendar.HOUR_OF_DAY) + 2) / 8) {
            case 1:
                str = "上午";
                break;
            case 2:
                str = "下午";
                break;
            default:
                str = "晚上";
        }

        if (c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR) == 0) {
            return new SimpleDateFormat("HH:mm").format(l);
        } else if (c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR) == 1) {
            return new SimpleDateFormat("昨天").format(new Date(l)) + " " + str;
        } else if (c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR) == 2) {
            return new SimpleDateFormat("前天").format(new Date(l)) + " " + str;
        } else {
            return new SimpleDateFormat("yyyy-MM-dd").format(new Date(l)) + " " + str;
        }
    }

    @Override
    protected void initEvent() {
        // 下拉列表事件监听
        msrfLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
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
                refreshData();
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mLocalBroadcastReceiver, new IntentFilter(Constant.BROAD_URI_MSG));

    }

    @Override
    protected void loadData() {
        // 初始打开时加载列表
        refreshData();
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
        refreshData();
    }
}
