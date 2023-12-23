package fr.quiniou.gestion_back.security.jwt;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		// Log de l'exception
		System.out.println("Erreur d'authentification JWT : " + authException.getMessage());
		// Envoi d'une réponse d'erreur
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Erreur : Non autorisé");
	}
}
