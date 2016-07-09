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
import com.foxconn.linsirchat.module.contact.bean.ConversationBean;
import com.foxconn.linsirchat.module.conversation.ui.ChatRoomActivity;

import java.util.List;

/**
 * Created by Administrator on 2016/7/5.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyHolder> {

    private List<ConversationBean> mList;
    private Context mContext;

    public ContactAdapter(Context context, List<ConversationBean> list) {
        mContext = context;
        mList = list;
    }

    /**
     * 替换元素并刷新
     *
     * @param mList
     */
    public void refresh(List<ConversationBean> list) {
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
    public ContactAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_contact_list_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactAdapter.MyHolder holder, int position) {
        holder.mtvName.setText(mList.get(position).getNick());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public ImageView mivIcon;
        public TextView mtvName;

        public MyHolder(final View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    ConversationBean iUser = mList.get(position);
                    Intent intent = new Intent(mContext, ChatRoomActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("nick",iUser.getNick());
                    bundle.putString("tel",iUser.getTel());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });

            mtvName = (TextView) itemView.findViewById(R.id.tv_contact_name);
            mivIcon = (ImageView) itemView.findViewById(R.id.iv_contact_icon);
        }
    }

}
