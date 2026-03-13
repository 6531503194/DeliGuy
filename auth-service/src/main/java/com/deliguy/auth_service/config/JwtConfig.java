// package com.deliguy.auth_service.config;


// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Primary;
// import org.springframework.security.oauth2.jwt.*;

// import com.nimbusds.jose.JWSAlgorithm;
// import com.nimbusds.jose.jwk.JWKSet;
// import com.nimbusds.jose.jwk.OctetSequenceKey;
// import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
// import com.nimbusds.jose.jwk.source.JWKSource;
// import com.nimbusds.jose.proc.SecurityContext;

// import javax.crypto.SecretKey;
// import javax.crypto.spec.SecretKeySpec;
// import java.nio.charset.StandardCharsets;

// @Configuration
// public class JwtConfig {

//     private static final String SECRET = "my-super-secret-key-for-jwt-authentication-12345";

//     private SecretKey secretKey() {
//         return new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
//     }

//     @Bean
//     @Primary 
//     public JwtEncoder jwtEncoder() {
//         OctetSequenceKey jwk = new OctetSequenceKey.Builder(secretKey())
//             .algorithm(JWSAlgorithm.HS256)  
//          .build();
//         JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
//         return new NimbusJwtEncoder(jwkSource);
//     }

//     @Bean
//     public JwtDecoder jwtDecoder() {
//         return NimbusJwtDecoder.withSecretKey(secretKey()).build();
//     }
// }