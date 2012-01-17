package com.tinyspeck.android;

// Implement this in the activity that will be receiving session (login success / login failed / logout) information
public interface GlitchSessionDelegate {
	
	// Called when login is successful
    public void glitchLoginSuccess();
    
    // Called when login isn't successful
    public void glitchLoginFail();
    
    // Called when the user logs out
    public void glitchLoggedOut();
    
    public void glitchConnectionError();
    
}
