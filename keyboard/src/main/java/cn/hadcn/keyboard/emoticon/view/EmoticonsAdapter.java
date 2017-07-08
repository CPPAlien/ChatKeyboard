package cn.hadcn.keyboard.emoticon.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.hadcn.keyboard.R;
import cn.hadcn.keyboard.emoticon.EmoticonBean;
import cn.hadcn.keyboard.utils.EmoticonLoader;

/**
 * @author chris
 */
public class EmoticonsAdapter extends BaseAdapter {
    private Context mContext;
    private List<EmoticonBean> data;
    private int mItemHeight = 0;
    private int mImgHeight = 0;
    private boolean isDisplayName = false;

    public EmoticonsAdapter(Context context, List<EmoticonBean> list, boolean isDisplayName) {
        this.mContext = context;
        this.data = list;
        this.isDisplayName = isDisplayName;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public EmoticonBean getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.keyboard_emoticon_item, parent,
                    false);
            convertView.setLayoutParams(new AbsListView.LayoutParams(RelativeLayout.LayoutParams
                    .MATCH_PARENT, mItemHeight));

            viewHolder = new ViewHolder(convertView);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mImgHeight,
                    mImgHeight);
            viewHolder.ivEmoticon.setLayoutParams(params);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final EmoticonBean emoticonBean = getItem(position);
        if (emoticonBean != null) {   // exists some empty block
            viewHolder.ivEmoticon.setBackgroundResource(R.drawable.emoticon_bg);
            if (isDisplayName) {
                viewHolder.tvName.setVisibility(View.VISIBLE);
                viewHolder.tvName.setText(getItem(position).getName());
            } else {
                viewHolder.tvName.setVisibility(View.GONE);
            }
            viewHolder.ivEmoticon.setImageDrawable(EmoticonLoader.getInstance(mContext)
                    .getDrawable(emoticonBean.getIconUri()));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemListener != null) {
                        mOnItemListener.onItemClick(emoticonBean);
                    }
                }
            });
        }

        return convertView;
    }

    static class ViewHolder {
        public ImageView ivEmoticon;
        public TextView tvName;

        public ViewHolder(View view) {
            ivEmoticon = (ImageView) view.findViewById(R.id.emoticon_item_image);
            tvName = (TextView) view.findViewById(R.id.emoticon_item_text);
        }
    }

    public void setHeight(int height, int padding) {
        mItemHeight = height;
        mImgHeight = mItemHeight - padding;
        notifyDataSetChanged();
    }

    private EmoticonsListener mOnItemListener;

    void setOnItemListener(EmoticonsListener listener) {
        this.mOnItemListener = listener;
    }

    public interface EmoticonsListener {
        void onItemClick(EmoticonBean bean);

        void onPageChangeTo(int position);
    }
}