package com.trendyol.scheduler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/")
@ApiIgnore
public class IndexController {

    private static final String PATH_SWAGGER_UI = "/swagger-ui.html";

    @GetMapping
    public RedirectView redirectToSwaggerUi() {
        return new RedirectView(PATH_SWAGGER_UI);
    }
}