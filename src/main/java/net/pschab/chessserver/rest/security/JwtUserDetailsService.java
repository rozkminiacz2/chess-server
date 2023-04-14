package net.pschab.chessserver.rest.security;

import net.pschab.chessserver.model.Player;
import net.pschab.chessserver.model.Role;
import net.pschab.chessserver.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private PlayerService playerService;

    @Override
    public UserDetails loadUserByUsername(final String username) {
        final Player player = playerService.getPlayerByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
        return getJwtUserDetails(player);
    }

    private JwtUserDetails getJwtUserDetails(Player player) {
        return new JwtUserDetails(player.getName(), player.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(Role.USER.toString())));
    }

}
