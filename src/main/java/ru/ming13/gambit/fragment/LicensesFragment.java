package ru.ming13.gambit.fragment;

import android.os.Bundle;
import android.webkit.WebViewFragment;

import ru.ming13.gambit.util.Assets;

public class LicensesFragment extends WebViewFragment
{
	public static LicensesFragment newInstance() {
		return new LicensesFragment();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpLicenses();
	}

	private void setUpLicenses() {
		getWebView().loadUrl(Assets.getLicensesUri());
	}
}
