package com.minhnhatlpx.simplephdl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import java.util.*;
import android.text.*;

/**
 * Created by javierg on 25/01/2017.
 */

public class SingleAdapter extends BaseAdapter implements SelectedIndex {

    private final Context mContext;
    private List<Pornhub> mList = new ArrayList<>();
    private int mSelectedIndex = -1;

	public SingleAdapter(Context mContext, List<Pornhub> mList)
	{
		this.mContext = mContext;
		this.mList = mList;
	}

    @Override
    public void setSelectedIndex(int position) {
        mSelectedIndex = position;
    }

    static class ViewHolder {
        TextView mTextView;
        RadioButton mRadioButton;
    }

    
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item, null);

			
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) rowView.findViewById(R.id.list_item_text);
            viewHolder.mRadioButton = (RadioButton) rowView.findViewById(R.id.list_item_check_button);

            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
		
		if(TextUtils.isEmpty(mList.get(position).getUrl()))
		{
			holder.mTextView.setAlpha(0.5f);
			holder.mRadioButton.setAlpha(0.5f);
		}else
		{
			holder.mTextView.setAlpha(1);
			holder.mRadioButton.setAlpha(1);
		}
		
		holder.mTextView.setText(mList.get(position).getText());
		
		if (mSelectedIndex == position) {
			holder.mRadioButton.setChecked(true);
		} else {
			holder.mRadioButton.setChecked(false);
		}
		
		
		
        return rowView;
    }

	@Override
	public boolean isEnabled(int position)
	{
		if(TextUtils.isEmpty(mList.get(position).getUrl()))
		{
			return false;
		}
		else
		{
			return true;
		}
		
	}
    
}
