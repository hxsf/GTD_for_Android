package com.ihxsf.gtd.View.interfaces;

/**
 * Created by hxsf on 16－05－26.
 */
public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
