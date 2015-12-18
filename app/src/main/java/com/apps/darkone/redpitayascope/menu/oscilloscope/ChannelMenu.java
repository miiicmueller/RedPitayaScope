package com.apps.darkone.redpitayascope.menu.oscilloscope;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.LinearLayout;

import com.apps.darkone.redpitayascope.R;
import com.apps.darkone.redpitayascope.menu.CustomButtonMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DarkOne on 11.12.15.
 */
public class ChannelMenu extends CustomButtonMenu implements CustomButtonMenu.IOnCustomMenuButtonAction {


    private List<Map<Drawable,Integer>> mButtonTupleList;
    private Context mContext;
    private IOnChannelMenuListener mChannelMenuListener;
    private final String CHANNEL_MENU_TAG = "ChannelMenu";


    private final int GAIN_TAG = 0;
    private final int PROBE_ATT_TAG = 1;
    private final int OFFSET_TAG = 2;
    private final int SCALE_TAG = 3;


    @Override
    public void onButtonPressed(Integer buttonTag) {

        if(this.mChannelMenuListener != null) {
            switch (buttonTag) {
                case GAIN_TAG:
                    Log.d(CHANNEL_MENU_TAG, "Gain button pressed");
                    this.mChannelMenuListener.onGainMenuButtonClick();
                    break;
                case PROBE_ATT_TAG:
                    Log.d(CHANNEL_MENU_TAG, "Probe att. pressed");
                    this.mChannelMenuListener.onProbeAttMenuButtonClick();
                    break;
                case OFFSET_TAG:
                    Log.d(CHANNEL_MENU_TAG, "Offset pressed");
                    this.mChannelMenuListener.onChannelOffsetMenuButtonClick();
                    break;
                case SCALE_TAG:
                    Log.d(CHANNEL_MENU_TAG, "Scale pressed");
                    this.mChannelMenuListener.onChannelScaleMenuClick();
                    break;
                default:
                    break;
            }
        }
        else
        {
            Log.d(CHANNEL_MENU_TAG, "ChannelMenuListener isn't setted!");
        }
    }

    public interface IOnChannelMenuListener {
        public void onGainMenuButtonClick();

        public void onProbeAttMenuButtonClick();

        public void onChannelOffsetMenuButtonClick();

        public void onChannelScaleMenuClick();
    }


    public ChannelMenu(Context context, LinearLayout layout, int menuColor) {

        super(context, layout, menuColor);

        this.mContext = context;
        this.mButtonTupleList = new ArrayList<>();
        this.mChannelMenuListener = null;

        Map<Drawable, Integer> gainButtonMap = new HashMap<>();
        Map<Drawable, Integer> probeAttButtonMap = new HashMap<>();
        Map<Drawable, Integer> offsetButtonMap = new HashMap<>();
        Map<Drawable, Integer> scaleButtonMap = new HashMap<>();


        gainButtonMap.put(this.mContext.getResources().getDrawable(R.drawable.ic_hv), GAIN_TAG);
        probeAttButtonMap.put(this.mContext.getResources().getDrawable(R.drawable.ic_1x), PROBE_ATT_TAG);
        offsetButtonMap.put(this.mContext.getResources().getDrawable(R.drawable.ic_swap_vert_black_24dp), OFFSET_TAG);
        scaleButtonMap.put(this.mContext.getResources().getDrawable(R.drawable.ic_add), SCALE_TAG);

        this.mButtonTupleList.add(gainButtonMap);
        this.mButtonTupleList.add(probeAttButtonMap);
        this.mButtonTupleList.add(offsetButtonMap);
        this.mButtonTupleList.add(scaleButtonMap);

        this.setCustomMenuPressedListener(this);

        this.createMenu(this.mButtonTupleList);
    }


    public void setOnChannelMenuListener(IOnChannelMenuListener iOnChannelMenuListener) {
        this.mChannelMenuListener = iOnChannelMenuListener;
    }


    public void deleteOnChannelMenuListener() {
        this.mChannelMenuListener = null;
    }


}
