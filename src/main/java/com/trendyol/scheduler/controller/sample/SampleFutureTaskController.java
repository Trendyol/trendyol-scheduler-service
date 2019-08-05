package com.trendyol.scheduler.controller.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("future-job")
public class SampleFutureTaskController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleFutureTaskController.class);

    @GetMapping("invoke")
    public void invoke() {
        LOGGER.info("Hurray! Sample future job invoke endpoint activated!");
    }

}
