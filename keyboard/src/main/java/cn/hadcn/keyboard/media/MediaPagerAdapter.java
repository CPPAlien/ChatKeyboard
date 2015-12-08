package cn.hadcn.keyboard.media;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import cn.hadcn.keyboard.R;


public class MediaPagerAdapter extends PagerAdapter {
	private static final int MAX_NUM_PER_PAGE = 8;
	Context mContext;
    List<MediaGridAdapter> gridAdapterList = new ArrayList<>();
    int mPageNum = 0;
    int mColumnWidth = 0;

    public MediaPagerAdapter(Context context, List<MediaBean> mediaModels, int columnWidth) {
        mContext = context;
        mColumnWidth = columnWidth;
        mPageNum = (int) Math.ceil((float) mediaModels.size() / MAX_NUM_PER_PAGE);
        for (int i = 0; i < mPageNum; ++i) {
            ArrayList<MediaBean> mediaItems = new ArrayList<>();
            for (int j = i * MAX_NUM_PER_PAGE; j < (i+1) * MAX_NUM_PER_PAGE && j < mediaModels.size(); ++j) {
                mediaItems.add(mediaModels.get(j));
            }
            MediaGridAdapter adapter = new MediaGridAdapter( mContext, mediaItems, mColumnWidth );
            gridAdapterList.add(adapter);
        }
    }

    public int getPageNum() {
        return mPageNum;
    }

	@Override
	public int getCount() {
		return gridAdapterList.size();
	}

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.media_page, container, false);

        GridView grid = (GridView) layout.findViewById(R.id.media_grid);
        grid.setColumnWidth(mColumnWidth);
        grid.setAdapter(gridAdapterList.get(position));

        container.addView(layout);

        return layout;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for (MediaGridAdapter adapter : gridAdapterList) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeAllViews();
    }

    @Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
}