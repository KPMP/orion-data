package org.kpmp.packages;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
class UniversalIdGenerator {

	public String generateUniversalId() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

}
