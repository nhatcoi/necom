package com.necom.repository.reward;

import com.necom.entity.reward.RewardLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RewardLogRepository extends JpaRepository<RewardLog, Long>, JpaSpecificationExecutor<RewardLog> {

    @Query("SELECT COALESCE(SUM(r.score), 0) FROM RewardLog r JOIN r.user u WHERE u.username = :username")
    int sumScoreByUsername(@Param("username") String username);

    List<RewardLog> findByUserUsername(String username);

}
