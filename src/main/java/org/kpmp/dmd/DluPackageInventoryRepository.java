package org.kpmp.dmd;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface DluPackageInventoryRepository extends CrudRepository<DluPackageInventory, Integer> {
    DluPackageInventory save(DluPackageInventory dluPackageInventory);
}
