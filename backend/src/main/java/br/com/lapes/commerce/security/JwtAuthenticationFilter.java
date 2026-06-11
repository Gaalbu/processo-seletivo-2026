package br.com.lapes.commerce.security;

import br.com.lapes.commerce.auth.TokenBlacklist;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.JwtException;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserPrincipalService userPrincipalService;
  private final TokenBlacklist tokenBlacklist;

  public JwtAuthenticationFilter(
      JwtService jwtService,
      UserPrincipalService userPrincipalService,
      TokenBlacklist tokenBlacklist) {
    this.jwtService = jwtService;
    this.userPrincipalService = userPrincipalService;
    this.tokenBlacklist = tokenBlacklist;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authorization = request.getHeader("Authorization");
    if (authorization == null || !authorization.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = authorization.substring(7);

    if (tokenBlacklist.isInvalid(token)) {
      SecurityContextHolder.clearContext();
      filterChain.doFilter(request, response);
      return;
    }

    String email;
    try {
      email = jwtService.extractSubject(token);
    } catch (JwtException | IllegalArgumentException exception) {
      SecurityContextHolder.clearContext();
      filterChain.doFilter(request, response);
      return;
    }

    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails user = userPrincipalService.loadUserByUsername(email);
      if (user instanceof AuthenticatedUser authenticatedUser && jwtService.isValid(token, authenticatedUser)) {
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    filterChain.doFilter(request, response);
  }
}
