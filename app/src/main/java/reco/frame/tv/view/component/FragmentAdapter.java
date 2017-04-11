package reco.frame.tv.view.component;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class FragmentAdapter extends PagerAdapter{
	private List<Fragment> fragments;
	private FragmentManager fragmentManager;
	private int currentPageIndex = 0;

	public FragmentAdapter(FragmentManager fragmentManager,
			List<Fragment> fragments) {
		this.fragments = fragments;
		this.fragmentManager = fragmentManager;
	}

	public int getCount() {
		return fragments.size();
	}

	public boolean isViewFromObject(View view, Object o) {
		return view == o;
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(fragments.get(position).getView()); // �Ƴ�viewpager����֮���page����
	}

	public Object instantiateItem(ViewGroup container, int position) {
		Fragment fragment = fragments.get(position);
		if (!fragment.isAdded()) {
			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.add(fragment, fragment.getClass().getSimpleName());
			ft.commit();
			fragmentManager.executePendingTransactions();
		}
		
		if (fragment.getView().getParent() == null) {
			container.addView(fragment.getView());
		}
		

		return fragment.getView();
	}


	public int getCurrentPageIndex() {
		return currentPageIndex;
	}

}
