package com.necom.entity.inventory;

import com.necom.entity.product.Variant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "docket_variant")
public class DocketVariant {
    @EmbeddedId
    private DocketVariantKey docketVariantKey = new DocketVariantKey();

    @ManyToOne
    @MapsId("docketId")
    @JoinColumn(name = "docket_id", nullable = false)
    private Docket docket;

    @ManyToOne
    @MapsId("variantId")
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
