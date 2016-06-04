package com.ihxsf.gtd.View;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.ihxsf.gtd.View.interfaces.ItemTouchHelperViewHolder;
import com.ihxsf.gtd.data.Suff;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

/**
 * Created by hxsf on 16－05－26.
 */
public class SuffItemTouchHelper extends ItemTouchHelper.Callback {
    public static final float ALPHA_FULL = 1.0f;

    private final SuffListAdapter mAdapter;

    public SuffItemTouchHelper(SuffListAdapter adapter) {
        mAdapter = adapter;
    }
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        if (source.getItemViewType() != target.getItemViewType()) {
            return false;
        }

        // Notify the adapter of the move
        mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        final Suff suff = ((SuffListAdapter.MyViewHolder)viewHolder).data;
        final int id = suff.getId();
        final int postion = viewHolder.getAdapterPosition();
        Log.i("swiped", "onSwiped: "+direction+" right= "+ItemTouchHelper.RIGHT);
        String text;
        // 判断方向，进行不同的操作
        if (direction == ItemTouchHelper.END || direction == ItemTouchHelper.RIGHT) {
            text = "完成一项";
            mAdapter.onItemDismiss(postion);

            Snackbar.make(viewHolder.itemView, text, Snackbar.LENGTH_LONG)
                    .setAction("撤销", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAdapter.onItemRollback(suff.getId(), postion);
                            mAdapter.notifyDataSetChanged();
                        }
                    }).show();
        } else if (direction == ItemTouchHelper.START || direction == ItemTouchHelper.LEFT) {
            final Calendar time = Calendar.getInstance();
            if (suff.getTime() != null) {
                time.setTime(suff.getTime());
            }
            DatePickerDialog.newInstance(
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                            time.set(year,monthOfYear,dayOfMonth);
                            TimePickerDialog dpd = TimePickerDialog.newInstance(
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                                            time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            time.set(Calendar.MINUTE, minute);
                                            mAdapter.changeItemTime(postion, time.getTime());
                                        }
                                    },
                                    time.get(Calendar.HOUR_OF_DAY),
                                    time.get(Calendar.MINUTE),
                                    true
                            );
                            dpd.show(mAdapter.getActivity().getFragmentManager(), "To Pick A Time");
                        }
                    },
                    time.get(Calendar.YEAR),
                    time.get(Calendar.MONTH),
                    time.get(Calendar.DAY_OF_MONTH)
            ).show(mAdapter.getActivity().getFragmentManager(), "To Pick A Date");
        }
    }
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // 释放View时回调，清除背景颜色，隐藏图标
        // 默认是操作ViewHolder的itemView，这里调用ItemTouchUIUtil的clearView方法传入指定的view
        getDefaultUIUtil().clearView(((SuffListAdapter.MyViewHolder) viewHolder).mainview);
        ((SuffListAdapter.MyViewHolder) viewHolder).background.setBackgroundColor(Color.TRANSPARENT);
        ((SuffListAdapter.MyViewHolder) viewHolder).vschedule.setVisibility(View.GONE);
        ((SuffListAdapter.MyViewHolder) viewHolder).vdone.setVisibility(View.GONE);

        viewHolder.itemView.setAlpha(ALPHA_FULL);

        if (viewHolder instanceof ItemTouchHelperViewHolder) {
            // Tell the view holder it's time to restore the idle state
            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemClear();
        }
    }
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        getDefaultUIUtil().onSelected(((SuffListAdapter.MyViewHolder)viewHolder).mainview);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
//            ((SuffListAdapter.MyViewHolder) viewHolder).title.setAlpha(alpha);
//            ((SuffListAdapter.MyViewHolder) viewHolder).time.setAlpha(alpha);
//            ((SuffListAdapter.MyViewHolder) viewHolder).location.setAlpha(alpha);
            ((SuffListAdapter.MyViewHolder) viewHolder).mainview.setTranslationX(dX);
            if (dX > 0) {
                ((SuffListAdapter.MyViewHolder) viewHolder).background.setBackgroundColor(Color.parseColor("#00ff00"));
                ((SuffListAdapter.MyViewHolder) viewHolder).vschedule.setVisibility(View.GONE);
                ((SuffListAdapter.MyViewHolder) viewHolder).vdone.setVisibility(View.VISIBLE);
            } else {
                ((SuffListAdapter.MyViewHolder) viewHolder).background.setBackgroundColor(Color.parseColor("#ffff00"));
                ((SuffListAdapter.MyViewHolder) viewHolder).vschedule.setVisibility(View.VISIBLE);
                ((SuffListAdapter.MyViewHolder) viewHolder).vdone.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        // We only want the active item to change
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ItemTouchHelperViewHolder) {
                // Let the view holder know that this item is being moved or dragged
                ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
                itemViewHolder.onItemSelected();
            }
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        getDefaultUIUtil().onDrawOver(c, recyclerView, ((SuffListAdapter.MyViewHolder)viewHolder).mainview,  dX, dY, actionState, isCurrentlyActive);
    }

}
