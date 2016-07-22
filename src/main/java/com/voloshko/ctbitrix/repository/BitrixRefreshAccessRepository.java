package com.voloshko.ctbitrix.repository;

import com.voloshko.ctbitrix.dmodel.BitrixRefreshAccess;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 12.03.2016.
 */
@Transactional
public interface BitrixRefreshAccessRepository extends CrudRepository<BitrixRefreshAccess, Long>, JpaSpecificationExecutor<BitrixRefreshAccess> {
}
