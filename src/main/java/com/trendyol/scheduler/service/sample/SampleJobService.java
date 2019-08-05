package com.trendyol.scheduler.service.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SampleJobService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleJobService.class);

    public void invoke() {
        LOGGER.info("Hurray! Sample job service invoke method activated!");
    }

}
