package net.pschab.chessserver.rest.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class JwtUserDetails extends User {

    public final String name;

    public JwtUserDetails(final String name, final String hash,
                          final Collection<? extends GrantedAuthority> authorities) {
        super(name, hash, authorities);
        this.name = name;
    }

}
