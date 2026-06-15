package backend.ptit.security.jwt;

import backend.ptit.security.CustomUserDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret",
                "TestSecretKeyDuKy64KyTuChoHmacSha256Algorithm0123456789abcdefghijklmnop");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 60_000);
    }

    private Authentication mockAuth(String username) {
        CustomUserDetail principal = new CustomUserDetail(
                1L, username, username + "@test.local", "pwd", List.of());
        return new UsernamePasswordAuthenticationToken(principal, null, List.of());
    }

    @Test
    void generateAndValidate_validToken_returnsUsername() {
        String token = jwtUtils.generateJwtToken(mockAuth("alice"));

        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals("alice", jwtUtils.getAllUserFromJwtToken(token));
    }

    @Test
    void validate_garbageToken_returnsFalse() {
        assertFalse(jwtUtils.validateJwtToken("not.a.real.token"));
        assertFalse(jwtUtils.validateJwtToken(""));
    }

    @Test
    void validate_expiredToken_returnsFalse() {
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -1000); // het han ngay
        String token = jwtUtils.generateJwtToken(mockAuth("bob"));

        assertFalse(jwtUtils.validateJwtToken(token));
    }
}
