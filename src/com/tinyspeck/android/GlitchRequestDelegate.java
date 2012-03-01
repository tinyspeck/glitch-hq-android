package com.tinyspeck.android;

// Implement this in the activity that will be receiving request information
// Then pass into the call to get a new request object
public interface GlitchRequestDelegate {
	
	// Called when a request is completed
	// Check the method via request.method and response via request.response
	public void requestFinished(GlitchRequest request);
	
	// Called when a request fails
	public void requestFailed(GlitchRequest request);
}