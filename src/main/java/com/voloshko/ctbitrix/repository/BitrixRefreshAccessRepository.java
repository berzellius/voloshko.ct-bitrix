package com.voloshko.ctbitrix.repository;

import com.voloshko.ctbitrix.dmodel.BitrixRefreshAccess;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by berz on 12.03.2016.
 */
@Service
@Transactional
public interface BitrixRefreshAccessRepository extends CrudRepository<BitrixRefreshAccess, Long>, JpaSpecificationExecutor<BitrixRefreshAccess> {
}
