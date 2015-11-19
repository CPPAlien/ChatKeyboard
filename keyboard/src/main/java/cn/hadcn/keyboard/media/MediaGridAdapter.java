package cn.hadcn.keyboard.media;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.hadcn.keyboard.R;


public class MediaGridAdapter extends BaseAdapter {
	private ArrayList<MediaBean> mediaModels;
	Context mContext;

    /**
     * MediaGridAdapter
     * @param context context
     * @param mediaModels data
     */
	public MediaGridAdapter(Context context, ArrayList<MediaBean> mediaModels ) {
        this.mContext = context;
		this.mediaModels = mediaModels;
	}

	public View getView(final int position, View convertView, ViewGroup parent){
		View v = convertView;
		if (v == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.media_item, parent, false);
        }

        ImageView image = (ImageView) v.findViewById(R.id.media_item_image);
		image.setImageDrawable(ContextCompat.getDrawable(mContext, getItem(position).getDrawableId()));

        TextView text = (TextView) v.findViewById(R.id.media_item_text);
        text.setText(getItem(position).getText());

		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                getItem(position).getMediaListener().onMediaClick(getItem(position).getId());
			}
		});

		return v;
	}
	
	@Override
	public int getCount() {		
		return mediaModels.size();
	}
	
	@Override
	public MediaBean getItem(int position) {
		return mediaModels.get(position);
	}
	
	@Override
	public long getItemId(int position) {		
		return position;
	}
}
