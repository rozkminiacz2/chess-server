package net.pschab.chessserver.rest.security;

import net.pschab.chessserver.model.Player;
import net.pschab.chessserver.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    public static final String USER = "USER";
    public static final String ROLE_USER = "ROLE_" + USER;

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) {
        final Player player = playerRepository.findById(username).orElseThrow(
                () -> new UsernameNotFoundException("User " + username + " not found"));
        return new JwtUserDetails(player.getName(), String.valueOf(player.getPassword()), Collections.singletonList(new SimpleGrantedAuthority(ROLE_USER)));
    }

}
