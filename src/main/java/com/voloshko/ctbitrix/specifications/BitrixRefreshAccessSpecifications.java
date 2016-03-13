package com.voloshko.ctbitrix.specifications;

import com.voloshko.ctbitrix.dmodel.BitrixRefreshAccess;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by berz on 12.03.2016.
 */
public class BitrixRefreshAccessSpecifications {
    public static Specification<BitrixRefreshAccess> orderFromLastToFirst(){
        return new Specification<BitrixRefreshAccess>() {
            @Override
            public Predicate toPredicate(Root<BitrixRefreshAccess> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Path<Long> idPath = root.get("id");
                Predicate predicate = criteriaBuilder.isNotNull(idPath);

                Path<Date> dtmCreate = root.get("dtmCreate");

                List<Order> orderList = new LinkedList<>();
                orderList.add(criteriaBuilder.desc(dtmCreate));

                return criteriaQuery.where(predicate).orderBy(orderList).getRestriction();
            }
        };
    }
}
