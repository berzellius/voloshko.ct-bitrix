package com.voloshko.ctbitrix.service;

import com.voloshko.ctbitrix.dmodel.BitrixRefreshAccess;
import com.voloshko.ctbitrix.repository.BitrixRefreshAccessRepository;
import com.voloshko.ctbitrix.specifications.BitrixRefreshAccessSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Created by berz on 12.03.2016.
 */
@Service
@Transactional
public class BitrixRefreshAccessServiceImpl implements BitrixRefreshAccessService {

    @Autowired
    BitrixRefreshAccessRepository bitrixRefreshAccessRepository;

    @Override
    public BitrixRefreshAccess getLastRefreshAccess() {
        List<BitrixRefreshAccess> bitrixRefreshAccessList = bitrixRefreshAccessRepository.findAll(BitrixRefreshAccessSpecifications.orderFromLastToFirst());

        if(bitrixRefreshAccessList.size() == 0) {
            return null;
        }
        else return bitrixRefreshAccessList.get(0);
    }

    @Override
    public void newRefreshAccess(BitrixRefreshAccess bitrixRefreshAccessNew) {
        bitrixRefreshAccessNew.setDtmCreate(new Date());
        bitrixRefreshAccessRepository.save(bitrixRefreshAccessNew);
    }

    @Override
    public void addTokens(String accessCode, String refreshCode) {
        BitrixRefreshAccess bitrixRefreshAccess = new BitrixRefreshAccess();
        bitrixRefreshAccess.setRefreshToken(refreshCode);
        bitrixRefreshAccess.setAccessToken(accessCode);
        this.newRefreshAccess(bitrixRefreshAccess);
    }
}
