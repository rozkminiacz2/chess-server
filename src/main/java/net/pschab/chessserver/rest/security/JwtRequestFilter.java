package net.pschab.chessserver.rest.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtTokenService jwtTokenService;

    @Autowired
    private final JwtUserDetailsService jwtUserDetailsService;
    public JwtTokenService getJwtTokenService() {
        return jwtTokenService;
    }

    public JwtUserDetailsService getJwtUserDetailsService() {
        return jwtUserDetailsService;
    }

    public JwtRequestFilter(JwtTokenService jwtTokenService, UserDetailsService userDetailsService) {
        this.jwtTokenService = jwtTokenService;
        this.jwtUserDetailsService = (JwtUserDetailsService) userDetailsService;
    }

    @Override
    public void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                 final FilterChain chain) throws ServletException, IOException {

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isBearerNotDefined(header)) {
            chain.doFilter(request, response);
            return;
        }

        final String username = getUserName(header);
        if (username == null) {
            chain.doFilter(request, response);
            return;
        }

        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
        final UsernamePasswordAuthenticationToken authentication = createAuthenticationToken(request, userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }

    private boolean isBearerNotDefined(String header) {
        return header == null || !header.startsWith("Bearer ");
    }

    private String getUserName(String header) {
        final String token = header.substring(7);
        return jwtTokenService.validateTokenAndGetUsername(token);
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(HttpServletRequest request, UserDetails userDetails) {
        final UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authentication;
    }
}
