package com.voloshko.ctbitrix.repository;

import com.voloshko.ctbitrix.dmodel.Site;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by berz on 27.03.2016.
 */
@Transactional
public interface SiteRepository extends CrudRepository<Site, Long> {
    public List<Site> findByUrlAndPassword(String url, String password);
}
