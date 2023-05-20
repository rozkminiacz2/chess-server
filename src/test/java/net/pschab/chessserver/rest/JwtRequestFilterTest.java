package net.pschab.chessserver.rest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.pschab.chessserver.rest.security.JwtRequestFilter;
import net.pschab.chessserver.rest.security.JwtTokenService;
import net.pschab.chessserver.rest.security.JwtUserDetailsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class JwtRequestFilterTest {

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private JwtUserDetailsService jwtUserDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtRequestFilter jwtRequestFilter;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        jwtRequestFilter = new JwtRequestFilter(jwtTokenService, jwtUserDetailsService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    void shouldSkipFilterIfHeaderIsNull() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtTokenService);
        verifyNoInteractions(jwtUserDetailsService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldSkipFilterIfHeaderDoesNotStartWithBearer() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic abc");

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtTokenService);
        verifyNoInteractions(jwtUserDetailsService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldSkipFilterIfUsernameIsNull() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer xyz");
        when(jwtTokenService.validateTokenAndGetUsername("xyz")).thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtTokenService).validateTokenAndGetUsername("xyz");
        verifyNoInteractions(jwtUserDetailsService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionIfUserDetailsNotFound() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token");
        when(jwtTokenService.validateTokenAndGetUsername("token")).thenReturn("user");
        when(jwtUserDetailsService.loadUserByUsername("user")).thenThrow(UsernameNotFoundException.class);

        assertThatThrownBy(() -> jwtRequestFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(UsernameNotFoundException.class);
        verify(jwtTokenService).validateTokenAndGetUsername("token");
        verify(jwtUserDetailsService).loadUserByUsername("user");
        verifyNoMoreInteractions(jwtTokenService, jwtUserDetailsService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldAuthenticateIfUsernameIsValid() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token");
        when(jwtTokenService.validateTokenAndGetUsername("token")).thenReturn("user");
        final UserDetails userDetails = mock(UserDetails.class);
        when(jwtUserDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user");
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(jwtTokenService).validateTokenAndGetUsername("token");
        verify(jwtUserDetailsService).loadUserByUsername("user");
        verify(filterChain).doFilter(request, response);
        verify(userDetails).getAuthorities();
        final UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(userDetails).isEqualTo(authentication.getPrincipal());
        assertThat(authentication.getCredentials()).isNull();
    }
}