package com.apps.darkone.redpitayascope.menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.apps.darkone.redpitayascope.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by DarkOne on 07.12.15.
 */
public class CustomButtonMenu {


    private List<FloatingActionButton> mBtnMenuList;
    private Context mContext;
    private boolean mIsMenuShowed;
    private Toolbar mToolBar;
    private int mMenuColor;
    private IOnCustomMenuButtonAction mIOnCustomMenuButtonAction;
    private IOnCustomMenuStateChange mIOnCustomMenuStateChange;
    private RelativeLayout mLayout;


    public interface IOnCustomMenuButtonAction {
        public void onButtonPressed(Integer buttonTag);
    }


    public interface IOnCustomMenuStateChange {
        public void onNewState(boolean isHidden);
    }


    public CustomButtonMenu(Context context, Toolbar toolBar, int menuColor) {
        this.mBtnMenuList = new ArrayList<>();
        this.mContext = context;
        this.mToolBar = toolBar;
        this.mMenuColor = menuColor;
        this.mIOnCustomMenuButtonAction = null;
        this.mIOnCustomMenuStateChange = null;
    }


    protected void setCustomMenuPressedListener(IOnCustomMenuButtonAction iOnCustomMenuButtonAction) {
        this.mIOnCustomMenuButtonAction = iOnCustomMenuButtonAction;
    }

    protected void deleteCustomMenuPressedListener() {
        this.mIOnCustomMenuButtonAction = null;
    }


    public void setIOnCustomMenuStateChange(IOnCustomMenuStateChange iOnCustomMenuStateChange) {
        this.mIOnCustomMenuStateChange = iOnCustomMenuStateChange;
    }

    protected void createMenu(List<Map<Drawable, Integer>> buttonTupleList) {

        for (Map<Drawable, Integer> buttonTuple : buttonTupleList) {
            for (Drawable btnImage : buttonTuple.keySet()) {
                CustomMenuButton btn = createNewFloatingActionButton(this.mMenuColor, buttonTuple.get(btnImage));

                //Set the button image
                btn.setImageDrawable(btnImage);

                // Retreive the tag
                final Integer tag = btn.getTag();

                // Add a callbacklistener
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mIOnCustomMenuButtonAction) {
                            mIOnCustomMenuButtonAction.onButtonPressed(tag);
                        }
                    }
                });
                this.mBtnMenuList.add(btn);
            }
        }


        this.mLayout = (RelativeLayout) this.mToolBar.findViewById(R.id.toolbar_bottom_layer_fab);
        this.mLayout.setVisibility(RelativeLayout.GONE);

        for (FloatingActionButton btn : this.mBtnMenuList) {
            mLayout.addView(btn);
            btn.show();
            btn.setClickable(true);
            btn.setX(-TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, this.mContext.getResources().getDisplayMetrics())); // we hide the buttons
        }

        mIsMenuShowed = false;
    }

    private CustomMenuButton createNewFloatingActionButton(int menuColor, Integer tag) {
        CustomMenuButton btn = new CustomMenuButton(this.mContext, tag);
        btn.setBackgroundTintList(ColorStateList.valueOf(this.mMenuColor));
        btn.setRippleColor(this.mMenuColor - 0xFF00);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btn.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 4, this.mContext.getResources().getDisplayMetrics()));
        }
        return btn;
    }


    public void showMenu() {

        int i = 0;

        mLayout.setVisibility(RelativeLayout.VISIBLE);


        for (FloatingActionButton btn : this.mBtnMenuList) {
            ViewPropertyAnimator linInterpolator = btn.animate().translationX(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (16) * (i + 1) + 56 * i, this.mContext.getResources().getDisplayMetrics())).setInterpolator(new DecelerateInterpolator(1.0f));

            linInterpolator.setListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {

                    if (!mIsMenuShowed) {
                        mIsMenuShowed = true;
                        if (mIOnCustomMenuStateChange != null) {
                            mIOnCustomMenuStateChange.onNewState(false);
                        }
                    }

                }

            });


            linInterpolator.start();

            btn.animate().rotationBy(360.f).setInterpolator(new DecelerateInterpolator(1.0f)).start();
            i++;
        }
    }


    public void hideMenu() {

        for (FloatingActionButton btn : this.mBtnMenuList) {
            ViewPropertyAnimator linInterpolator = btn.animate().translationX(-TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, this.mContext.getResources().getDisplayMetrics())).setInterpolator(new AccelerateInterpolator(1.0f));

            linInterpolator.setListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {

                    if (mIsMenuShowed) {
                        mIsMenuShowed = false;
                        if (mIOnCustomMenuStateChange != null) {
                            mIOnCustomMenuStateChange.onNewState(true);
                        }
                    }

                }

            });

            linInterpolator.start();

            btn.animate().rotationBy(-360.f).setInterpolator(new AccelerateInterpolator(1.0f)).start();
        }
    }

    public void hideLayout() {
        mLayout.setVisibility(RelativeLayout.GONE);
    }


    public boolean isMenuShowing() {
        return this.mIsMenuShowed;
    }


    public void destroyMenu() {
        RelativeLayout layout = (RelativeLayout) this.mToolBar.findViewById(R.id.toolbar_bottom_layer_fab);
        layout.removeAllViews();
    }


    private class CustomMenuButton extends FloatingActionButton {

        private Integer mButtonTag;

        public CustomMenuButton(Context context, Integer tag) {
            super(context);
            this.mButtonTag = tag;
        }

        public CustomMenuButton(Context context, AttributeSet attrs, Integer tag) {
            super(context, attrs);
            this.mButtonTag = tag;
        }

        public CustomMenuButton(Context context, AttributeSet attrs, int defStyleAttr, Integer tag) {
            super(context, attrs, defStyleAttr);
            this.mButtonTag = tag;
        }

        public Integer getTag() {
            return this.mButtonTag;
        }
    }
}
