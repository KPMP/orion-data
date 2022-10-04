package org.kpmp.dmd;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DluPackageInventoryRepository extends CrudRepository<DluPackageInventory, Integer> {

}
