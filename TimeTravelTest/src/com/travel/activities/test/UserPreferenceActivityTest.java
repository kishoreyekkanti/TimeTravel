/**
 * 
 */
package com.travel.activities.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.travel.activities.UserPreferenceActivity;

/**
 * @author ykkuumar
 *
 */
public class UserPreferenceActivityTest extends
		ActivityInstrumentationTestCase2<UserPreferenceActivity> {
	private Solo solo;
	public UserPreferenceActivity mActivity;

	public UserPreferenceActivityTest(String name) {
		super("com.travel.activities",UserPreferenceActivity.class);
		setName(name);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			this.solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		this.getActivity().finish();
		super.tearDown();
	}
	
	public void testUserSettingsUI(){
		String acitivityName = solo.getCurrentActivity().getClass().getSimpleName();
		solo.assertCurrentActivity("Expected User settings Activity", acitivityName);
		solo.clickOnMenuItem("Username");
		solo.clearEditText(0);
		solo.enterText(0, "testtimetravel");
		solo.sendKey(Solo.ENTER);
	}

}
