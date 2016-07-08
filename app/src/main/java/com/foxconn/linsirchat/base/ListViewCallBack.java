package com.foxconn.linsirchat.base;

/**在线程中需要刷新ListView时回调
 * Created by Administrator on 2016/6/27.
 */
public interface ListViewCallBack {

    /**
     *更新ListView条目
     * @param ListView新对象集合
     */
    void updateListView(Object object);

}
