package uz.pdp.conf.security;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class CustomAbstractSecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
    @Override
    protected boolean enableHttpSessionEventPublisher() {
        return true;
    }
}