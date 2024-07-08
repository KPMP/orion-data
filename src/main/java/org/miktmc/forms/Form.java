package org.miktmc.forms;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "dynamicForms")
public class Form {

	private String id;
	private Double version;
	private Map<String, Object> standardFields;
	private List<Map<String, Object>> typeSpecificElements;

	@Field("_id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Map<String, Object>> getTypeSpecificElements() {
		return typeSpecificElements;
	}

	public void setTypeSpecificElements(List<Map<String, Object>> typeSpecificElements) {
		this.typeSpecificElements = typeSpecificElements;
	}

	public Map<String, Object> getStandardFields() {
		return standardFields;
	}

	public void setStandardFields(Map<String, Object> standardFields) {
		this.standardFields = standardFields;
	}

	@Override
	public String toString() {
		return "typeSpecificElements " + typeSpecificElements.toString() + ", standardFields "
				+ standardFields;
	}

	public Double getVersion() {
		return version;
	}

	public void setVersion(Double version) {
		this.version = version;
	}
}
