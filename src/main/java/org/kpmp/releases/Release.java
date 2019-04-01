package org.kpmp.releases;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "releases")
public class Release {

	private String version;
	private String date;
	private String desc;
	private Map<String, Object> typeSpecificNotes;

	public String getVersion() {
		return version;
	}

	public String getDate() {
		return date;
	}

	public String getDesc() {
		return desc;
	}

	public Map<String, Object> getTypeSpecificNotes() {
		return typeSpecificNotes;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setTypeSpecificNotes(Map<String, Object> typeSpecificNotes) {
		this.typeSpecificNotes = typeSpecificNotes;
	}

	@Override
	public String toString() {
		return String.format("[Release version %s date %s desc \"%s\" typeSpecificNotes %s]", getVersion(), getDate(),
				getDesc(), getTypeSpecificNotes());
	}
}
