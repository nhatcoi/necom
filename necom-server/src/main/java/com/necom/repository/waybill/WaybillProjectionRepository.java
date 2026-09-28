package com.necom.repository.waybill;

import com.necom.dto.statistic.StatisticResource;
import com.necom.entity.waybill.Waybill;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.time.Instant;
import java.util.List;

@Repository
@AllArgsConstructor
public class WaybillProjectionRepository {

    private EntityManager em;

    public List<StatisticResource> getWaybillCountByCreateDate() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StatisticResource> query = cb.createQuery(StatisticResource.class);

        Root<Waybill> waybill = query.from(Waybill.class);
        query.select(cb.construct(StatisticResource.class, waybill.get("createdAt").as(Instant.class), cb.count(waybill.get("id"))));
        query.groupBy(waybill.get("createdAt").as(Instant.class));
        query.orderBy(cb.asc(waybill.get("createdAt")));

        return em.createQuery(query).getResultList();
    }

}
