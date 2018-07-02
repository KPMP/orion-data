package org.kpmp;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class UniversalIdGenerator {

	public String generateUniversalId() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

}
