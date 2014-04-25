package by.deniotokiari.core.adapter.fragment;

import java.util.List;

import by.deniotokiari.core.fragment.FragmentPageInfo;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class FragmentPagerAdapter extends FragmentStatePagerAdapter {

	private Context mContext;
	private List<FragmentPageInfo> mPages;
	private String[] mPagesName;

	public FragmentPagerAdapter(Context context,
			FragmentManager fragmentManager, List<FragmentPageInfo> pages,
			String[] pagesName) {
		super(fragmentManager);

		mContext = context;
		mPages = pages;
		mPagesName = pagesName;
	}

	@Override
	public Fragment getItem(int position) {
		FragmentPageInfo pageInfo = mPages.get(position);
        return Fragment.instantiate(mContext, pageInfo.getClazz()
                .getName(), pageInfo.getBundle());
	}

	@Override
	public int getCount() {
		return mPages.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		if (mPages.get(position).getTitle() != null) {
			return mPages.get(position).getTitle();
		} else if (mPagesName != null) {
			return mPagesName[position];
		} else {
			return super.getPageTitle(position);
		}
	}
	
}
