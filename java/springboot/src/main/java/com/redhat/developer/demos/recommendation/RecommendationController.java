package com.redhat.developer.demos.recommendation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Random;

@RestController
public class RecommendationController {

    private static final String RESPONSE_STRING_FORMAT = "recommendation v2 from '%s': %d\n";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Counter to help us see the lifecycle
     */
    private int count = 0;

    /**
     * Flag for throwing a 503 when enabled
     */
    private boolean misbehave = false;

    private static final String HOSTNAME = parseContainerIdFromHostname(
            System.getenv().getOrDefault("HOSTNAME", "unknown"));

    static String parseContainerIdFromHostname(String hostname) {
        return hostname.replaceAll("recommendation-v\\d+-", "");
    }

    @RequestMapping("/")
    public ResponseEntity<Recommendation> getRecommendations(HttpServletRequest httpServletRequest) {
        count++;
        logger.debug(String.format("recommendation request from %s: %d", HOSTNAME, count));

        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String headerKey = headerNames.nextElement();
            System.out.println(headerKey +" -> "+httpServletRequest.getHeader(headerKey));
        }

        logger.debug("recommendation service ready to return");
        if (misbehave) {
            timeout();
        }

        Recommendation recommendation = new Recommendation();
        Random rand = new Random();
        Integer id = rand.nextInt(1000000);
        recommendation.setId(id);
        recommendation.setComment(String.format(RecommendationController.RESPONSE_STRING_FORMAT, HOSTNAME, count));
        boolean isOk = id % 2 == 0 ? Boolean.TRUE: Boolean.FALSE;
        recommendation.setOk(isOk);

        return ResponseEntity.ok(recommendation);
    }

    private void timeout() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.info("Thread interrupted");
        }
    }

    private ResponseEntity<String> doMisbehavior() {
        count = 0;
        misbehave = false;
        logger.debug(String.format("Misbehaving %d", count));
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(String.format("recommendation misbehavior from '%s'\n", HOSTNAME));
    }

    @RequestMapping("/misbehave")
    public ResponseEntity<String> flagMisbehave() {
        this.misbehave = true;
        logger.debug("'misbehave' has been set to 'true'");
        return ResponseEntity.ok("Next request to / will return a 503\n");
    }

}
