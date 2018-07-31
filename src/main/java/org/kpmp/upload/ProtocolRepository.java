package org.kpmp.upload;

import org.kpmp.dao.Protocol;
import org.springframework.data.repository.CrudRepository;

public interface ProtocolRepository extends CrudRepository<Protocol, Integer> {

	public Protocol findByProtocol(String protocol);
}