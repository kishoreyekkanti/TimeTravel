package com.travel.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class UserPreferenceActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

}
