package br.com.lapes.commerce.auth;

import br.com.lapes.commerce.domain.Cart;
import br.com.lapes.commerce.domain.User;
import br.com.lapes.commerce.repository.CartRepository;
import br.com.lapes.commerce.repository.UserRepository;
import br.com.lapes.commerce.security.AuthenticatedUser;
import br.com.lapes.commerce.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final CartRepository cartRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public AuthService(
      UserRepository userRepository,
      CartRepository cartRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwtService jwtService) {
    this.userRepository = userRepository;
    this.cartRepository = cartRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    String email = request.email().trim().toLowerCase();
    if (userRepository.existsByEmail(email)) {
      throw new EmailAlreadyRegisteredException();
    }

    User user = User.customer(request.name().trim(), email, passwordEncoder.encode(request.password()));
    User savedUser = userRepository.save(user);
    cartRepository.save(Cart.forUser(savedUser));

    return toAuthResponse(new AuthenticatedUser(savedUser));
  }

  public AuthResponse login(LoginRequest request) {
    try {
      var authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  request.email().trim().toLowerCase(), request.password()));
      return toAuthResponse((AuthenticatedUser) authentication.getPrincipal());
    } catch (BadCredentialsException exception) {
      throw new InvalidCredentialsException();
    }
  }

  private AuthResponse toAuthResponse(AuthenticatedUser user) {
    return new AuthResponse(
        jwtService.generateToken(user),
        new AuthResponse.UserResponse(user.id(), user.name(), user.getUsername(), user.role()));
  }
}
