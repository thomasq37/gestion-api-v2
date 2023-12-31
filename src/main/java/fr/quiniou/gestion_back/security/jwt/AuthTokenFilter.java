package fr.quiniou.gestion_back.security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import fr.quiniou.gestion_back.security.CustomUtilisateurDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger authLogger = LoggerFactory.getLogger(AuthTokenFilter.class);
    
    private final JwtUtils jwtUtils;
    private final CustomUtilisateurDetailsService customUtilisateurDetailsService;

    // Injectez les dépendances via le constructeur
    public AuthTokenFilter(JwtUtils jwtUtils, CustomUtilisateurDetailsService customUtilisateurDetailsService) {
        this.jwtUtils = jwtUtils;
        this.customUtilisateurDetailsService = customUtilisateurDetailsService;
    }
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUsernameFromJwtToken(jwt);

                UserDetails userDetails = customUtilisateurDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            authLogger.error("Impossible de définir l'authentification de l'utilisateur", e);

            // Modifier la réponse HTTP en cas d'exception
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Erreur d'authentification : " + e.getMessage());

            // Pas besoin de continuer avec la chaîne de filtres
            return;
        }

        // Continuer avec la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}
