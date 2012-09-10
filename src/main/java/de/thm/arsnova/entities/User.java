package de.thm.arsnova.entities;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.scribe.up.profile.facebook.FacebookProfile;
import org.scribe.up.profile.google.Google2Profile;
import org.scribe.up.profile.twitter.TwitterProfile;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

public class User {
	
	private String username;

	public User(Google2Profile profile) {
		setUsername(profile.getEmail());
	}

	public User(TwitterProfile profile) {
		setUsername(profile.getScreenName());
	}

	public User(FacebookProfile profile) {
		setUsername(profile.getLink());
	}

	public User(AttributePrincipal principal) {
		setUsername(principal.getName());
	}

	public User(AnonymousAuthenticationToken token) {
		setUsername("anonymous");
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}