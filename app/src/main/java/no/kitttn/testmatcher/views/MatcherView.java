package no.kitttn.testmatcher.views;

import no.kitttn.testmatcher.model.Person;

/**
 * @author kitttn
 */
public interface MatcherView {
	void loading();
	void stopLoading();
	void showProfile(Person person);
	void like();
	void dislike();
}
