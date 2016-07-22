package com.voloshko.ctbitrix.repository;

import com.voloshko.ctbitrix.dmodel.LeadFromSite;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 27.03.2016.
 */
@Transactional
public interface LeadFromSiteRepository extends CrudRepository<LeadFromSite, Long> {
}
