package com.foxconn.linsirchat.module.main.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.foxconn.linsirchat.MyApplication;
import com.foxconn.linsirchat.R;
import com.foxconn.linsirchat.base.BaseActivity;
import com.foxconn.linsirchat.module.contact.ui.ContactFragment;
import com.foxconn.linsirchat.module.conversation.ui.ConversationFragment;
import com.foxconn.linsirchat.module.me.ui.MeFragment;
import com.se7en.utils.NetworkUtils;

public class MainActivity extends BaseActivity {


    private ViewPager mvpFrag;
    private RadioGroup mrp;
    private Fragment[] mfragments;
    private FragmentPagerAdapter madapter;

    @Override
    protected int setViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void findViews() {
        mvpFrag = (ViewPager) findViewById(R.id.vp_main);
        mrp = (RadioGroup) findViewById(R.id.radio_group);
    }

    @Override
    protected void init() {
        ((MyApplication)getApplication()).startReceviverMsg();
        mfragments = new Fragment[3];

    }

    @Override
    protected void initEvent() {
        mvpFrag.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                RadioButton raidoButton = (RadioButton) mrp.getChildAt(position);
                raidoButton.setChecked(true);
            }
        });



    }

    @Override
    protected void loadData() {
        mfragments[0] = new ConversationFragment();
        mfragments[1] = new ContactFragment();
        mfragments[2] = new MeFragment();

        Bundle bundle = new Bundle();
        madapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mfragments.length;
            }

            @Override
            public Fragment getItem(int position) {
                return mfragments[position];
            }
        };

        mvpFrag.setAdapter(madapter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_conversation:
                mvpFrag.setCurrentItem(0);
                break;
            case R.id.rb_contact:
                mvpFrag.setCurrentItem(1);
                break;
            case R.id.rb_me:
                mvpFrag.setCurrentItem(2);
                break;
        }
    }
}
