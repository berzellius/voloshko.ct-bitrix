package com.voloshko.ctbitrix.service;

import com.voloshko.ctbitrix.dmodel.BitrixRefreshAccess;
import org.springframework.stereotype.Service;

/**
 * Created by berz on 12.03.2016.
 */
@Service
public interface BitrixRefreshAccessService {
    public BitrixRefreshAccess getLastRefreshAccess();

    void newRefreshAccess(BitrixRefreshAccess bitrixRefreshAccessNew);
}
