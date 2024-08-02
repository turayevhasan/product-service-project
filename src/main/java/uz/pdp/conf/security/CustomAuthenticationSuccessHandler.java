package uz.pdp.conf.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String redirectUrl = request.getContextPath(); // default context path

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ADMIN")
                    || authority.getAuthority().equals("ROLE_SUPER_ADMIN")
                    || authority.getAuthority().equals("ROLE_MANAGER")) {
                redirectUrl = "/admin/home";
                break;
            } else if (authority.getAuthority().equals("ROLE_USER")) {
                redirectUrl = "/home";
                break;
            }
        }

        response.sendRedirect(redirectUrl);
    }
}
