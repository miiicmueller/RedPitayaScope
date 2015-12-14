package com.apps.darkone.redpitayascope.menu;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.apps.darkone.redpitayascope.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DarkOne on 07.12.15.
 */
public class CustomButtonMenu {


    private List<FloatingActionButton> mBtnMenuList;
    private Context mContext;
    private boolean mIsMenuShowed;
    private Toolbar mToolBar;
    private int mMenuColor;

    public interface IOnCustomMenuPressed
    {
        public void onButtonPressed();
    }


    public CustomButtonMenu(Context context, Toolbar toolBar, int menuColor) {
        this.mBtnMenuList = new ArrayList<>();
        this.mContext = context;
        this.mToolBar = toolBar;
        this.mMenuColor = menuColor;
    }

    protected void createMenu(List<Drawable> buttonImageList)
    {
        for(Drawable image : buttonImageList)
        {
            FloatingActionButton btn = createNewFloatingActionButton(this.mMenuColor);
            btn.setImageDrawable(image);
            this.mBtnMenuList.add(btn);
        }


        RelativeLayout layout = (RelativeLayout) this.mToolBar.findViewById(R.id.toolbar_bottom_layer_fab);


        for (FloatingActionButton btn : this.mBtnMenuList) {
            layout.addView(btn);
            btn.show();
            btn.setClickable(true);
            btn.setX(-TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, this.mContext.getResources().getDisplayMetrics())); // we hide the buttons
        }

        mIsMenuShowed = false;
    }

    private FloatingActionButton createNewFloatingActionButton(int menuColor) {
        FloatingActionButton btn = new FloatingActionButton(this.mContext);
        btn.setBackgroundTintList(ColorStateList.valueOf(this.mMenuColor));
        btn.setRippleColor(this.mMenuColor + 0xFF);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btn.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 4, this.mContext.getResources().getDisplayMetrics()));
        }
        return btn;
    }


    public void showMenu() {

        int i = 0;

        this.mIsMenuShowed = true;

        for (FloatingActionButton btn : this.mBtnMenuList) {

            btn.animate().translationX(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (16) * (i + 1) + 56 * i, this.mContext.getResources().getDisplayMetrics())).setInterpolator(new DecelerateInterpolator(1.0f)).start();
            btn.animate().rotationBy(360.f).setInterpolator(new DecelerateInterpolator(1.0f)).start();
            i++;
        }
    }


    public void hideMenu() {

        this.mIsMenuShowed = false;

        for (FloatingActionButton btn : this.mBtnMenuList) {
            btn.animate().translationX(-TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, this.mContext.getResources().getDisplayMetrics())).setInterpolator(new AccelerateInterpolator(1.0f)).start();
            btn.animate().rotationBy(-360.f).setInterpolator(new AccelerateInterpolator(1.0f)).start();
        }
    }


    public boolean isMenuShowing() {
        return this.mIsMenuShowed;
    }


    public void destroyMenu()
    {
        RelativeLayout layout = (RelativeLayout) this.mToolBar.findViewById(R.id.toolbar_bottom_layer_fab);
        layout.removeAllViews();
    }
}
