package com.ihxsf.gtd.View;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ihxsf.gtd.MainActivity;
import com.ihxsf.gtd.R;
import com.ihxsf.gtd.SuffDetailActivity;
import com.ihxsf.gtd.View.interfaces.ItemTouchHelperAdapter;
import com.ihxsf.gtd.View.interfaces.ItemTouchHelperViewHolder;
import com.ihxsf.gtd.data.Suff;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


/**
 * Created by hxsf on 16－05－25.
 */
public class SuffListAdapter extends RealmRecyclerViewAdapter<Suff, SuffListAdapter.MyViewHolder> implements ItemTouchHelperAdapter {

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
        notifyItemRemoved(position);
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        public TextView title;
        public Suff data;
        public View.OnClickListener onClickLitener;

        public void setOnClickLitener(View.OnClickListener onClickLitener) {
            this.onClickLitener = onClickLitener;
            itemView.setOnClickListener(onClickLitener);
        }

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(onClickLitener);
            title = (TextView) view.findViewById(R.id.title);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }


        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}