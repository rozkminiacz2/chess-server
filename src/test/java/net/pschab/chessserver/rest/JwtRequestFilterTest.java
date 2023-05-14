package net.pschab.chessserver.rest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.pschab.chessserver.rest.security.JwtRequestFilter;
import net.pschab.chessserver.rest.security.JwtTokenService;
import net.pschab.chessserver.rest.security.JwtUserDetailsService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;

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

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        jwtRequestFilter = new JwtRequestFilter(jwtTokenService, jwtUserDetailsService);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should skip filter if header is null")
    void shouldSkipFilterIfHeaderIsNull() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtTokenService);
        verifyNoInteractions(jwtUserDetailsService);
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Should skip filter if header does not start with Bearer")
    void shouldSkipFilterIfHeaderDoesNotStartWithBearer() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic abc");

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtTokenService);
        verifyNoInteractions(jwtUserDetailsService);
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Should skip filter if username is null")
    void shouldSkipFilterIfUsernameIsNull() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer xyz");
        when(jwtTokenService.validateTokenAndGetUsername("xyz")).thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtTokenService).validateTokenAndGetUsername("xyz");
        verifyNoInteractions(jwtUserDetailsService);
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}