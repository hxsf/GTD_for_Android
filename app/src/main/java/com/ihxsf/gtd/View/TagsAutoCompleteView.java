package com.ihxsf.gtd.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ihxsf.gtd.data.Tag;
import com.ihxsf.gtd.R;
import com.tokenautocomplete.TokenCompleteTextView;

/**
 * Created by hxsf on 16－06－03.
 */
public class TagsAutoCompleteView extends TokenCompleteTextView<Tag> {
    public TagsAutoCompleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagsAutoCompleteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TagsAutoCompleteView(Context context) {
        super(context);
    }

    @Override
    protected View getViewForObject(Tag object) {
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.chips_tag, (ViewGroup)TagsAutoCompleteView.this.getParent(), false);
        TextView textView = (TextView)view.findViewById(R.id.chip_tag_name);
        textView.setText(object.getName());
        ImageView imageView = (ImageView) view.findViewById(R.id.chip_tag_color);
        GradientDrawable imageViewBackground = (GradientDrawable) imageView.getBackground();
        imageViewBackground.setColor((int) object.getColor());
        return view;
    }

    @Override
    protected Tag defaultObject(String completionText) {
        if (completionText.length()>0){
            Tag tag = new Tag();
            tag.setName("test");
            tag.setColor(Color.parseColor("#ff0000"));
            return tag;
        } else {
            Tag tag = new Tag();
            tag.setName("empty");
            tag.setColor(Color.parseColor("#cccccc"));
            return tag;
        }
    }
}
