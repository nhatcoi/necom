package com.necom.controller.client;

import com.necom.constant.AppConstants;
import com.necom.dto.client.ClientRewardLogResponse;
import com.necom.dto.client.ClientRewardResponse;
import com.necom.entity.reward.RewardLog;
import com.necom.mapper.client.ClientRewardLogMapper;
import com.necom.repository.reward.RewardLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/client-api/rewards")
@AllArgsConstructor
@CrossOrigin(AppConstants.FRONTEND_HOST)
public class ClientRewardController {

    private RewardLogRepository rewardLogRepository;
    private ClientRewardLogMapper clientRewardLogMapper;

    @GetMapping
    public ResponseEntity<ClientRewardResponse> getReward(Authentication authentication) {
        String username = authentication.getName();

        int totalScore = rewardLogRepository.sumScoreByUsername(username);
        List<ClientRewardLogResponse> logs = clientRewardLogMapper
                .entityToResponse(rewardLogRepository
                        .findByUserUsername(username)
                        .stream()
                        .sorted(Comparator.comparing(RewardLog::getId).reversed())
                        .collect(Collectors.toList()));

        ClientRewardResponse clientWishResponse = new ClientRewardResponse();
        clientWishResponse.setRewardTotalScore(totalScore);
        clientWishResponse.setRewardLogs(logs);

        return ResponseEntity.status(HttpStatus.OK).body(clientWishResponse);
    }

}
