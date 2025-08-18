package com.example.paymybuddy.controller.view;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LandingPageUserView implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // / => /dashboard (redirect)
        registry.addRedirectViewController("/", "/landingPageUser");
        // /dashboard => rend templates/dashboard.html (aucun controller, pas de Model)
        registry.addViewController("/landingPageUser").setViewName("landingPageUser");
    }
}
