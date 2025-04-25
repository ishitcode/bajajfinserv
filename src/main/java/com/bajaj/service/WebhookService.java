package com.bajaj.service;

import com.bajaj.model.ResultResponse;
import com.bajaj.model.WebhookRequest;
import com.bajaj.model.WebhookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class WebhookService {

    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook";
    private final WebClient webClient;

    @Autowired
    public WebhookService(WebClient webClient) {
        this.webClient = webClient;
    }

    public void processWebhook() {
        WebhookRequest request = new WebhookRequest();
        request.setName("John Doe");
        request.setRegNo("REG12347");
        request.setEmail("john@example.com");

        webClient.post()
                .uri(GENERATE_WEBHOOK_URL)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(WebhookResponse.class)
                .flatMap(this::processResponse)
                .subscribe();
    }

    private Mono<Void> processResponse(WebhookResponse response) {
        List<List<Integer>> mutualFollowers = findMutualFollowers(response.getData().getUsers());
        
        ResultResponse result = new ResultResponse();
        result.setRegNo("REG12347");
        result.setOutcome(mutualFollowers);

        return sendResultToWebhook(response.getWebhook(), response.getAccessToken(), result);
    }

    private List<List<Integer>> findMutualFollowers(List<WebhookResponse.User> users) {
        Set<List<Integer>> result = new HashSet<>();
        
        for (WebhookResponse.User user : users) {
            for (Integer followedId : user.getFollows()) {
                WebhookResponse.User followedUser = users.stream()
                        .filter(u -> u.getId() == followedId)
                        .findFirst()
                        .orElse(null);
                
                if (followedUser != null && followedUser.getFollows().contains(user.getId())) {
                    List<Integer> pair = new ArrayList<>();
                    pair.add(Math.min(user.getId(), followedId));
                    pair.add(Math.max(user.getId(), followedId));
                    result.add(pair);
                }
            }
        }
        
        return new ArrayList<>(result);
    }

    private Mono<Void> sendResultToWebhook(String webhookUrl, String accessToken, ResultResponse result) {
        return webClient.post()
                .uri(webhookUrl)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyValue(result)
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(4, Duration.ofSeconds(1))
                        .maxBackoff(Duration.ofSeconds(10))
                        .doBeforeRetry(retrySignal -> log.info("Retrying webhook call, attempt: {}", retrySignal.totalRetries() + 1)))
                .doOnError(error -> log.error("Failed to send result to webhook", error))
                .doOnSuccess(v -> log.info("Successfully sent result to webhook"));
    }
} 