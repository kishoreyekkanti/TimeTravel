package com.travel.activities.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.travel.activities.PanoramaTabWidget;
import com.travel.activities.PhotoCaptureActivity;

public class PanoramaTabWidgetTest extends ActivityInstrumentationTestCase2<PanoramaTabWidget>{
	
	private Solo solo;
	public PanoramaTabWidget mActivity;
	public PanoramaTabWidgetTest(String name){
		super("com.travel.activities.PanoramaTabWidget",PanoramaTabWidget.class);
		setName(name);
	}

	@Override
	public void setUp() throws Exception{
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
	
	public void testLaunchesRequiredTabs(){
		mActivity = this.getActivity();
		assertEquals(3,mActivity.getTabWidget().getTabCount());
		assertEquals(0,mActivity.getTabHost().getCurrentTab());
		assertEquals("camera", mActivity.getTabHost().getCurrentTabTag());
	}
	
	public void testPhotoCaptureAndGalleryView() throws Throwable{
		mActivity = this.getActivity();
		String acitivityName = solo.getCurrentActivity().getClass().getSimpleName();
		solo.assertCurrentActivity("Expected Photo Capture activity", acitivityName);
		runTestOnUiThread(new Runnable() {
            public void run() {
            	mActivity.getTabHost().setCurrentTab(2);
            }
        });
		Thread.sleep(500);
		solo.clickOnMenuItem("Username");
		solo.clearEditText(0);
		solo.enterText(0, "testtimetravel");
		solo.clickOnButton("OK");
		solo.clickOnMenuItem("Password");
		solo.clearEditText(0);
		solo.enterText(0, "!abcd1234");
		solo.clickOnButton("OK");

		runTestOnUiThread(new Runnable() {
            public void run() {
            	mActivity.getTabHost().setCurrentTab(0);
            }
        });
		Thread.sleep(500);
		PhotoCaptureActivity photoCaptureActivity = (PhotoCaptureActivity)solo.getCurrentActivity();
		solo.clickOnView(photoCaptureActivity.findViewById(com.travel.activities.R.id.camera_preview));
		Thread.sleep(500);
		solo.enterText(0, "uploading for testing");
		solo.clickOnButton("Upload");

		
	}
}
