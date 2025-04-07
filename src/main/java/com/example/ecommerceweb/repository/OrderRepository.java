package com.example.ecommerceweb.repository;


import com.example.ecommerceweb.entity.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.status = :status WHERE o.id = :orderId")
    void updateOrderStatusById(@Param("orderId") Long orderId, @Param("status") String status);

    @Query("""
        SELECT o FROM Order o\s
        WHERE o.user.id = :userId\s
        AND (:status IS NULL OR :status = 'ALL' OR o.status = :status)
        ORDER BY o.orderDate DESC""")
    Page<Order> findAllByStatus(@Param("userId") Long userId,
                                @Param("status") String status,
                                Pageable pageable);
}
