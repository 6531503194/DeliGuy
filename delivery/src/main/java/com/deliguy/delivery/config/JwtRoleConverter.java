package com.deliguy.delivery.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Flux;

import java.util.List;

public class JwtRoleConverter
        implements Converter<Jwt, Flux<GrantedAuthority>> {

    @Override
    public Flux<GrantedAuthority> convert(Jwt jwt) {

        // Expecting: "roles": ["CUSTOMER", "ADMIN"]
        List<String> roles = jwt.getClaimAsStringList("roles");

        if (roles == null || roles.isEmpty()) {
            return Flux.empty();
        }

        return Flux.fromIterable(roles)
            .map(role -> (GrantedAuthority)
                new SimpleGrantedAuthority("ROLE_" + role)
            );
    }
}
