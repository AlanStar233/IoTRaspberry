package com.alanstar.iotraspberry.utils;

import android.view.View;

import com.qmuiteam.qmui.widget.QMUITopBar;

/**
 * TopBar 控制器
 */
public class TopBarController implements View.OnClickListener {

    // class 私有元素
    private int state;
    private QMUITopBar mTopBar;

    // 清除 TopBar 所有元素
    public void clearTopBar(QMUITopBar mTopBar) {
        mTopBar.removeAllLeftViews();
        mTopBar.removeAllRightViews();
        mTopBar.removeCenterViewAndTitleView();
    }


    // Light: TopBar 状态值 set 和 get
    public void setTopBarRightBtnValue(int state)
    {
        this.state = state;
    }
    public int getTopBarRightBtnValue()
    {
        return state;
    }

    // Light: TopBar self set 和 get
    public void setTopBarSelf(QMUITopBar mTopBar)
    {
        this.mTopBar = mTopBar;
    }
    public QMUITopBar getTopBarSelf()
    {
        return mTopBar;
    }

    @Override
    public void onClick(View v) {
        // 此处可以不需要 TopBar 监听器
    }
}
