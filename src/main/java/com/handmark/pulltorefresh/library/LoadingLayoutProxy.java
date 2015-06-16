package com.handmark.pulltorefresh.library;

import java.util.HashSet;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.handmark.pulltorefresh.library.internal.LoadingLayout;

public class LoadingLayoutProxy implements ILoadingLayout {

	private final HashSet<LoadingLayout> mLoadingLayouts;

	LoadingLayoutProxy() {
		mLoadingLayouts = new HashSet<LoadingLayout>();
	}

	/**
	 * This allows you to add extra LoadingLayout instances to this proxy. This
	 * is only necessary if you keep your own instances, and want to have them
	 * included in any
	 * {@link PullToRefreshBase#createLoadingLayoutProxy(boolean, boolean)
	 * createLoadingLayoutProxy(...)} calls.
	 * 
	 * @param layout - LoadingLayout to have included.
	 */
	public void addLayout(LoadingLayout layout) {
		if (null != layout) {
			mLoadingLayouts.add(layout);
		}
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setLastUpdatedLabel(label);
		}
	}

	@Override
	public void setLoadingDrawable(Drawable drawable) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setLoadingDrawable(drawable);
		}
	}

	@Override
	public void setRefreshingLabel(CharSequence refreshingLabel) {
		for (LoadingLayout layout : mLoadingLayouts) {
			//if(layout.type == null || (layout.type != null && !layout.type.equals(LoadingLayout.TYPE_FOOTER)))
				layout.setRefreshingLabel(refreshingLabel);
		}
	}

	@Override
	public void setPullLabel(CharSequence label) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setPullLabel(label);
		}
	}

	@Override
	public void setReleaseLabel(CharSequence label) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setReleaseLabel(label);
		}
	}

	public void setTextTypeface(Typeface tf) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setTextTypeface(tf);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.handmark.pulltorefresh.library.ILoadingLayout#setEndLabel(java.lang.CharSequence)
	 */
	@Override
	public void setFooterLabel(CharSequence releaseLabel) {
		for (LoadingLayout layout : mLoadingLayouts) {
			if(layout.type != null){
				if(layout.type.equals(LoadingLayout.TYPE_FOOTER))
						layout.setRefreshingLabel(releaseLabel);
			}else{
				layout.setRefreshingLabel(releaseLabel);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.handmark.pulltorefresh.library.ILoadingLayout#setHeadLabel(java.lang.CharSequence)
	 */
	@Override
	public void setHeaderLabel(CharSequence releaseLabel) {
		for (LoadingLayout layout : mLoadingLayouts) {
			if(layout.type != null){
				if(layout.type.equals(LoadingLayout.TYPE_HEADER))
					layout.setRefreshingLabel(releaseLabel);
			}else{
				layout.setRefreshingLabel(releaseLabel);
			}
		}
	}
}
