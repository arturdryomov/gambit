package ru.ming13.gambit.fragment;

import android.webkit.WebViewFragment;

import ru.ming13.gambit.util.Assets;

public class LicensesFragment extends WebViewFragment
{
	public static LicensesFragment newInstance() {
		return new LicensesFragment();
	}

	@Override
	public void onStart() {
		super.onStart();

		setUpLicenses();
	}

	private void setUpLicenses() {
		getWebView().loadUrl(Assets.getLicensesUri());
	}
}
