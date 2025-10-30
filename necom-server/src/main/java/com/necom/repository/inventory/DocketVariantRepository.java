package com.necom.repository.inventory;

import com.necom.entity.inventory.DocketVariant;
import com.necom.entity.inventory.DocketVariantKey;
import com.necom.entity.product.Product;
import com.necom.entity.product.Variant;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Join;
import java.util.List;

public interface DocketVariantRepository extends JpaRepository<DocketVariant, DocketVariantKey>,
        JpaSpecificationExecutor<DocketVariant> {

    default List<DocketVariant> findByProductId(Long productId) {
        Specification<DocketVariant> spec = (root, query, cb) -> {
            Join<DocketVariant, Variant> variant = root.join("variant");
            Join<Variant, Product> product = variant.join("product");

            query.where(cb.equal(product.get("id"), productId));
            query.orderBy(cb.desc(root.get("docket").get("id")));

            return query.getRestriction();
        };

        return findAll(spec);
    }

    default List<DocketVariant> findByVariantId(Long variantId) {
        Specification<DocketVariant> spec = (root, query, cb) -> query
                .where(cb.equal(root.get("variant").get("id"), variantId))
                .orderBy(cb.desc(root.get("docket").get("id")))
                .getRestriction();

        return findAll(spec);
    }

}
