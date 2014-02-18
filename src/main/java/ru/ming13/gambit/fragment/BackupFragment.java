package ru.ming13.gambit.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.ming13.gambit.R;

public class BackupFragment extends Fragment
{
	public static BackupFragment newInstance() {
		return new BackupFragment();
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup fragmentContainer, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_backup, fragmentContainer, false);
	}
}
