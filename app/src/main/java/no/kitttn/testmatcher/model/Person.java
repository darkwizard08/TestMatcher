package no.kitttn.testmatcher.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author kitttn
 */
public class Person extends RealmObject {
	@PrimaryKey
	private int id;
	private String location;
	private String photo;
	private String status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
