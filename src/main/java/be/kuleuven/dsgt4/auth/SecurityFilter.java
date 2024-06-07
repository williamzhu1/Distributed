import java.util.Lvpackage be.kuleuven.dsgt4.auth;

import be.kuleuven.dsgt4.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer " prefix

            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                String email = decodedToken.getEmail();
                String role = (String) decodedToken.getClaims().get("role");

                var user = new User(email, role);
                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(new FirebaseAuthentication(user));
            } catch (Exception e) {
                // Handle token verification error
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return !path.startsWith("/api");
    }

    private static class FirebaseAuthentication implements Authentication {
        private final User user;
        private boolean authenticated = true;

        FirebaseAuthentication(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            if (user.isManager()) {
                return List.of(new SimpleGrantedAuthority("ROLE_MANAGER"));
            } else {
                return new ArrayList<>();
            }
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public User getPrincipal() {
            return this.user;
        }

        @Override
        public boolean isAuthenticated() {
            return this.authenticated;
        }

        @Override
        public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
            this.authenticated = authenticated;
        }

        @Override
        public String getName() {
            return user.getEmail();
        }
    }
}


