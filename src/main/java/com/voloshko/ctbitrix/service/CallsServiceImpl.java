package com.voloshko.ctbitrix.service;

import com.voloshko.ctbitrix.dmodel.LeadFromSite;
import com.voloshko.ctbitrix.dmodel.Site;
import com.voloshko.ctbitrix.dto.site.evrika.Lead;
import com.voloshko.ctbitrix.dto.site.evrika.Result;
import com.voloshko.ctbitrix.repository.CallRepository;
import com.voloshko.ctbitrix.repository.LeadFromSiteRepository;
import com.voloshko.ctbitrix.repository.SiteRepository;
import com.voloshko.ctbitrix.specifications.CallSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by berz on 27.09.2015.
 */
@Service
@Transactional
public class CallsServiceImpl implements CallsService {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    CallRepository callRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    LeadFromSiteRepository leadFromSiteRepository;

    @Override
    public Long callsAlreadyLoaded() {
        return callRepository.count(CallSpecifications.byDates(new Date(), new Date()));
    }

    @Override
    public Long callsAlreadyLoaded(Integer projectId) {
        return callsAlreadyLoaded(projectId, null, null);
    }

    @Override
    public Long callsAlreadyLoaded(Integer project, Date from, Date to) {
        Long count = callRepository.count(
                Specifications.where(CallSpecifications.byDates((from != null)? from : new Date(), (to != null)? to : new Date()))
                        .and(CallSpecifications.byProjectId(project))
        );
        return count;
    }

    @Override
    public Result newLeadFromSite(List<Lead> leads, String url, String password) {

        List<Site> sites = siteRepository.findByUrlAndPassword(url, password);
        if(sites.size() == 0){
            return new Result("error");
        }

        Site site = sites.get(0);

        List<LeadFromSite> leadFromSiteList = new ArrayList<>();
        for(Lead lead : leads){
            LeadFromSite leadFromSite = new LeadFromSite();
            leadFromSite.setSite(site);
            leadFromSite.setLead(lead);
            leadFromSite.setState(LeadFromSite.State.NEW);

            leadFromSiteList.add(leadFromSite);
        }

        leadFromSiteRepository.save(leadFromSiteList);
        return new Result("success");
    }
}
