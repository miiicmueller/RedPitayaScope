package com.apps.darkone.redpitayascope.menu.oscilloscope;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;

import com.apps.darkone.redpitayascope.R;
import com.apps.darkone.redpitayascope.menu.CustomButtonMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DarkOne on 11.12.15.
 */
public class ChannelMenu extends CustomButtonMenu {


    private List<Drawable> mButtonImageList;
    private Context mContext;
    private IOnChannelMenuListener mChannelMenuListener;

    public interface IOnChannelMenuListener
    {
        public void onGainMenuButtonClick();

        public void onProbeAttMenuButtonClick();

        public void onChannelOffsetMenuButtonClick();

        public void onChannelScaleMenuClick();
    }


    public ChannelMenu(Context context, Toolbar toolBar, int menuColor) {


        super(context, toolBar, menuColor);
        this.mContext = context;
        this.mButtonImageList = new ArrayList<>();
        this.mChannelMenuListener = null;

        this.mButtonImageList.add(this.mContext.getResources().getDrawable(R.drawable.ic_hv));
        this.mButtonImageList.add(this.mContext.getResources().getDrawable(R.drawable.ic_1x));
        this.mButtonImageList.add(this.mContext.getResources().getDrawable(R.drawable.ic_swap_vert_black_24dp));
        this.mButtonImageList.add(this.mContext.getResources().getDrawable(R.drawable.ic_add));

        this.createMenu(this.mButtonImageList);
    }


    public  void setOnChannelMenuListener(IOnChannelMenuListener channelMenuListener)
    {
        this.mChannelMenuListener = channelMenuListener;
    }


    public void deleteOnChannelMenuListener()
    {
        this.mChannelMenuListener = null;
    }





}
