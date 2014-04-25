package by.deniotokiari.core.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BasePagerFragment extends Fragment {

	protected PagerAdapter mAdapter;
	protected ViewPager mViewPager;
	protected List<FragmentPageInfo> mPagesInfo;

	protected abstract PagerAdapter getAdapter(List<FragmentPageInfo> pagesInfo);

	protected abstract ViewPager getViewPager(View view);

	protected abstract List<FragmentPageInfo> getPagesInfo();

	protected abstract View getLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return getLayout(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mPagesInfo = getPagesInfo();
		mAdapter = getAdapter(mPagesInfo);
		mViewPager = getViewPager(view);

		mViewPager.setAdapter(mAdapter);
	}

	protected void setPage(int page) {
		mViewPager.setCurrentItem(page);
	}

}
