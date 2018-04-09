package com.hedvig.botService.security;

import com.auth0.jwk.GuavaCachedJwkProvider;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class JWTAuthFilter implements Filter {

    private JWTVerifier googleVerifier;
    private JWTVerifier internalVerifier;
    private String certUrl;
    private String jwtSecret;

    public JWTAuthFilter(String certUrl, String jwtSecret) {
        this.certUrl = certUrl;
        this.jwtSecret = jwtSecret;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        JwkProvider provider;
        try {
            JwkProvider http = new UrlJwkProvider(new URL(certUrl));
            provider = new GuavaCachedJwkProvider(http);
        } catch (MalformedURLException e) {
            throw new ServletException(e);
        }
        Algorithm googleAlgorithm = Algorithm.RSA256(new KeyProvider(provider));
        googleVerifier = JWT.require(googleAlgorithm).build();

        try {
            Algorithm internalAlgorithm = StringUtils.isBlank(jwtSecret)
                    ? Algorithm.none() : Algorithm.HMAC256(jwtSecret);
            internalVerifier = JWT.require(internalAlgorithm).build();
        } catch (UnsupportedEncodingException e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        try {
            DecodedJWT jwt = JWT.decode(token);
            if (jwt.getIssuer().equals("back-office") && request.getMethod().equals("GET")) {
                internalVerifier.verify(token);
            } else {
                googleVerifier.verify(token);
            }
        } catch (JWTVerificationException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader("content-type", MediaType.TEXT_PLAIN_VALUE);
            response.getWriter().write(e.getMessage());
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    private static class KeyProvider implements RSAKeyProvider {

        private final JwkProvider provider;

        public KeyProvider(JwkProvider provider) {
            this.provider = provider;
        }

        @Override
        public RSAPublicKey getPublicKeyById(String keyId) {
            try {
                Jwk jwk = provider.get(keyId);
                return (RSAPublicKey) jwk.getPublicKey();
            } catch (JwkException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public RSAPrivateKey getPrivateKey() {
            return null;
        }

        @Override
        public String getPrivateKeyId() {
            return null;
        }
    }

}
