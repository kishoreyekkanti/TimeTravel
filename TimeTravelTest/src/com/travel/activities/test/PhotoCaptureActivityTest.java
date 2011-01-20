package com.travel.activities.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.SurfaceView;

import com.jayway.android.robotium.solo.Solo;
import com.travel.activities.PhotoCaptureActivity;

public class PhotoCaptureActivityTest extends
		ActivityInstrumentationTestCase2<PhotoCaptureActivity> {
	private Solo solo;
	public PhotoCaptureActivity mActivity;

	public PhotoCaptureActivityTest(String name) {
		super("com.travel.activities.PhotoCatpureActivity",
				PhotoCaptureActivity.class);
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

	public void testActivityLaunchesThePhotoCaptureScreen() {
		mActivity = this.getActivity();
		SurfaceView preview = (SurfaceView) mActivity
				.findViewById(com.travel.activities.R.id.camera_preview);
		assertNotNull(preview);
	}
	
}
