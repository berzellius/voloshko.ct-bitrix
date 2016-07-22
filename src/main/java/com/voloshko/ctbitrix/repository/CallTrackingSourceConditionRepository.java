package com.voloshko.ctbitrix.repository;

import com.voloshko.ctbitrix.dmodel.CallTrackingSourceCondition;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 01.03.2016.
 */
@Transactional
public interface CallTrackingSourceConditionRepository extends CrudRepository<CallTrackingSourceCondition, Long>, JpaSpecificationExecutor<CallTrackingSourceCondition> {

}
