package com.foxconn.linsirchat.common.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.foxconn.linsirchat.R;
import com.foxconn.linsirchat.common.constant.Constant;
import com.foxconn.linsirchat.module.conversation.bean.MsgBean;
import com.se7en.utils.SystemUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/7/6.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mcontext;
    private final List<MsgBean> mlist;
    private final String mstrTel;

    public ChatAdapter(Context context, List<MsgBean> list) {
        mcontext = context;
        mlist = list;
        mstrTel = SystemUtil.getSharedString(Constant.USER_TEL);
    }

    @Override
    public int getItemViewType(int position) {
        String sender = mlist.get(position).getSender();
        if (mstrTel.equals(sender)) {
            return 0;
        }
        return 1;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        View view = null;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(mcontext).inflate(R.layout.layout_chat_item0, parent, false);
                viewHolder = new SendHolder(view);
                break;
            case 1:
                view = LayoutInflater.from(mcontext).inflate(R.layout.layout_chat_item1, parent, false);
                viewHolder = new ReceiveHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 0:
                ((SendHolder)holder).mtvBody.setText(mlist.get(position).getBody());
                switch (mlist.get(position).getSendType()) {
                    case 0:
                        ((SendHolder) holder).mpb.setVisibility(View.GONE);
                        break;
                    case 1:
                        ((SendHolder) holder).mpb.setVisibility(View.VISIBLE);
                    case 2:
                        ((SendHolder) holder).mpb.setVisibility(View.GONE);
                        break;
                }
                break;
            case 1:
                ((ReceiveHolder)holder).mtvBody.setText(mlist.get(position).getBody());
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mlist == null ? 0 : mlist.size();
    }

    class SendHolder extends RecyclerView.ViewHolder {

        private final TextView mtvBody;
        private final ProgressBar mpb;

        public SendHolder(View itemView) {
            super(itemView);
            mtvBody = (TextView) itemView.findViewById(R.id.tv_chat_item);
            mpb = (ProgressBar) itemView.findViewById(R.id.pb_chat_item);
        }
    }

    class ReceiveHolder extends RecyclerView.ViewHolder {

        private final TextView mtvBody;

        public ReceiveHolder(View itemView) {
            super(itemView);
            mtvBody = (TextView) itemView.findViewById(R.id.tv_chat_item);
        }
    }

}
