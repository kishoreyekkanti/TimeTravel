package com.travel.services;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.harrison.lee.twitpic4j.TwitPic;
import com.harrison.lee.twitpic4j.TwitPicResponse;

public class TwitpicUpload {

	private String username;
	private String password;
	private boolean includeTweet = false;

	public TwitpicUpload(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		this.username = preferences.getString("username", "");
		this.password =  preferences.getString("password","");
		this.includeTweet = preferences.getBoolean("tweet_always",false);
	}

	public String uploadImageFor(String filePath, String tweetMessage)
			throws Exception {
		Log.d("TwitPicUpload","Authenticating!!");
		if (filePath != null && filePath.length() > 0) {
			File image = new File(filePath);
			if (username == null || username.length() < 1 || password == null|| password.length() < 1) {
				throw new Exception("Twitter credentials are not correctly setup in preferences");
			}
			// Create TwitPic object and allocate TwitPicResponse object
			TwitPic tpRequest = new TwitPic(username, password);
			TwitPicResponse tpResponse = null;
			Log.i("TweetWithImageUpload", "Got the fileData as " + image);
			// Make request and handle exceptions
			try {
				if (includeTweet && tweetMessage != null) {
					tpResponse = tpRequest.uploadAndPost(image, tweetMessage);
				} else {
					tpResponse = tpRequest.upload(image);
					Log.d("Twitpic response", tpResponse.getMediaUrl());
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("TwitPicUpload", e.toString());
			}

			// If we got a response back, print out response variables
			if (tpResponse != null) {
				tpResponse.dumpVars();
				Log.d("Twitpic response", tpResponse.getMediaUrl());
				return tpResponse.getMediaAid();
			}
		}
		return null;
	}
}
