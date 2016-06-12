package com.ihxsf.gtd.View;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihxsf.gtd.MainActivity;
import com.ihxsf.gtd.R;
import com.ihxsf.gtd.SuffDetailActivity;
import com.ihxsf.gtd.View.interfaces.ItemTouchHelperAdapter;
import com.ihxsf.gtd.View.interfaces.ItemTouchHelperViewHolder;
import com.ihxsf.gtd.data.Suff;
import com.ihxsf.gtd.util.DateCalc;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


/**
 * Created by hxsf on 16－05－25.
 */
public class SuffListAdapter extends RealmRecyclerViewAdapter<Suff, SuffListAdapter.MyViewHolder> implements ItemTouchHelperAdapter {

    public MainActivity getActivity() {
        return activity;
    }

    private final MainActivity activity;

    public SuffListAdapter(MainActivity activity, OrderedRealmCollection<Suff> data) {
        super(activity ,data, true);
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.suff_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Suff obj = getData().get(position);
        holder.data = obj;
        holder.title.setText(obj.getTitle());
        holder.time.setText(DateCalc.fromToday(obj.getTime()));
        holder.location.setText(obj.getEzLocation());
        holder.setOnClickLitener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, SuffDetailActivity.class);
                intent.putExtra("type", 1);//modif
                intent.putExtra("id", obj.getId());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
//        Collections.swap(getData(), fromPosition, toPosition);
        // TODO change suff's level and rank
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        activity.deleteItem(position);
        context.sendBroadcast(new Intent("com.ihxsf.gtd.event.UPDATE_DATA"));
        notifyItemRemoved(position);
    }

    public void onItemRollback(int id, int position) {
        activity.doneRollback(id);
        context.sendBroadcast(new Intent("com.ihxsf.gtd.event.UPDATE_DATA"));
        notifyItemInserted(position);
    }

    public void changeItemTime(int position, Date time) {
        int newpostion = activity.changeItemTime(position, time);
        notifyItemChanged(position);
        context.sendBroadcast(new Intent("com.ihxsf.gtd.event.UPDATE_DATA"));
//        Log.i("move", "from: "+position+" to: "+newpostion);
        notifyItemMoved(position, newpostion);
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        public View mainview;
        public View background;
        public TextView title;
        public TextView time;
        public TextView location;
        public ImageView vdone;
        public ImageView vschedule;
        public Suff data;
        public View.OnClickListener onClickLitener;

        public void setOnClickLitener(View.OnClickListener onClickLitener) {
            this.onClickLitener = onClickLitener;
            mainview.setOnClickListener(onClickLitener);
        }

        public MyViewHolder(View view) {
            super(view);
            mainview = view.findViewById(R.id.suff_item);
            background = view.findViewById(R.id.iv_background);
            vdone = (ImageView)  view.findViewById(R.id.iv_done);
            vschedule = (ImageView)  view.findViewById(R.id.iv_schedule);
            title = (TextView) view.findViewById(R.id.item_title);
            time = (TextView) view.findViewById(R.id.item_time);
            location = (TextView) view.findViewById(R.id.item_location);
        }
        public MyViewHolder(View view, View.OnClickListener onClickLitener) {
            this(view);
            this.onClickLitener = onClickLitener;
            mainview.setOnClickListener(onClickLitener);
        }

        @Override
        public void onItemSelected() {
            mainview.setBackgroundColor(Color.LTGRAY);
        }


        @Override
        public void onItemClear() {
            mainview.setBackgroundColor(Color.WHITE);
        }
    }
}