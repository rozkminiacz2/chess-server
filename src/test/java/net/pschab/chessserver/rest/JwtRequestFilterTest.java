package net.pschab.chessserver.rest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import net.pschab.chessserver.rest.security.JwtRequestFilter;
import net.pschab.chessserver.rest.security.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private UserDetailsService userDetailsService;

    private final MockHttpServletRequest request = new MockHttpServletRequest();

    private final MockHttpServletResponse response = new MockHttpServletResponse();

    private final FilterChain filterChain = mock(FilterChain.class);

    private final JwtRequestFilter jwtRequestFilter = new JwtRequestFilter(jwtTokenService, userDetailsService);

    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup().addFilter(jwtRequestFilter).build();

    @Test
    void shouldNotAuthenticateIfAuthorizationHeaderIsNotPresent() throws ServletException, IOException {
        // given
        request.addHeader("Accept", "application/json");
        // when
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        // then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtTokenService, userDetailsService);
    }

    @Test
    void shouldNotAuthenticateIfAuthorizationHeaderDoesNotStartWithBearer() throws ServletException, IOException {
        // given
        request.addHeader("Authorization", "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
        // when
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        // then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtTokenService, userDetailsService);
    }

    @Test
    void shouldNotAuthenticateIfUsernameIsNotValid() throws ServletException, IOException {
        // given
        final String authorizationHeader = "Bearer token";
        request.addHeader("Authorization", authorizationHeader);
        when(jwtTokenService.validateTokenAndGetUsername("token")).thenReturn(null);
        // when
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        // then
        verify(jwtTokenService).validateTokenAndGetUsername("token");
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void shouldAuthenticateIfUsernameIsValid() throws ServletException, IOException {
        // given
        final String authorizationHeader = "Bearer token";
        request.addHeader("Authorization", authorizationHeader);
        when(jwtTokenService.validateTokenAndGetUsername("token")).thenReturn("user");
        final UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user");
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
        // when
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        // then
        verify(jwtTokenService).validateTokenAndGetUsername("token");
        verify(userDetailsService).loadUserByUsername("user");
        verify(filterChain).doFilter(request, response);
        verify(userDetails).getUsername();
        verify(userDetails).getAuthorities();
        verifyNoMoreInteractions(jwtTokenService, userDetailsService, userDetails);
        final UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(userDetails, authentication.getPrincipal());
        assertNull(authentication.getCredentials());
        assertEquals(authorizationHeader, authentication.getDetails());
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionIfUserDetailsNotFound() throws ServletException, IOException {
        // given
        final String authorizationHeader = "Bearer token";
        request.addHeader("Authorization", authorizationHeader);
        when(jwtTokenService.validateTokenAndGetUsername("token")).thenReturn("user");
        when(userDetailsService.loadUserByUsername("user")).thenThrow(UsernameNotFoundException.class);
        // when, then
        assertThrows(UsernameNotFoundException.class,
                () -> jwtRequestFilter.doFilterInternal(request, response, filterChain));
        verify(jwtTokenService).validateTokenAndGetUsername("token");
        verify(userDetailsService).loadUserByUsername("user");
        verifyNoMoreInteractions(jwtTokenService, userDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

}
