package cn.hadcn.keyboard_example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.hadcn.keyboard.utils.imageloader.EmoticonLoader;

/**
 * SimpleChatAdapter
 * Created by 90Chris on 2015/11/24.
 */
public class SimpleChatAdapter extends BaseAdapter {

    Context mContext;
    private List<ChatBean> mChatBeans = new ArrayList<>();

    public SimpleChatAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void addItem(ChatBean bean) {
        mChatBeans.add(bean);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mChatBeans.size();
    }

    @Override
    public ChatBean getItem(int i) {
        return mChatBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private static class ViewHolder {
        ImageView ivEmoticon;
        TextView tvTextMsg;

        public ViewHolder(View view) {
            ivEmoticon = (ImageView)view.findViewById(R.id.item_image);
            tvTextMsg = (TextView)view.findViewById(R.id.item_text);
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if ( view == null ) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_list, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }
        if ( getItem(i).getEmoticonUri() != null ) {
            viewHolder.ivEmoticon.setVisibility(View.VISIBLE);
            viewHolder.ivEmoticon.setImageDrawable(EmoticonLoader.getInstance(mContext).getDrawable(getItem(i).getEmoticonUri()));
        } else {
            viewHolder.ivEmoticon.setVisibility(View.GONE);
        }

        if ( getItem(i).getTextMsg() != null ) {
            viewHolder.tvTextMsg.setVisibility(View.VISIBLE);
            viewHolder.tvTextMsg.setText(getItem(i).getTextMsg());
        } else {
            viewHolder.tvTextMsg.setVisibility(View.GONE);
        }

        return view;
    }
}
