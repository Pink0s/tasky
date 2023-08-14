package com.tasky.api.utilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link JwtUtility} class.
 */
@ExtendWith(MockitoExtension.class)
class JwtUtilityTest {

    private JwtUtility underTest;

    @BeforeEach
    void setUp() {
        underTest = new JwtUtility();
        ReflectionTestUtils.setField(underTest,"SECRET_KEY","secret-key-for-testsqdfqsdfqsdfsdfqsfqs");
        ReflectionTestUtils.setField(underTest,"ISSUER","http://issuer-for-test.com");
    }

    /**
     * Tests the {@link JwtUtility#issueToken(String)} method to ensure it can issue a valid token with a subject.
     */
    @Test
    void CanIssueValidTokenWithSubject() {
        // GIVEN
        String subject = "testSubject";

        // WHEN
        String token = underTest.issueToken(subject);

        // THEN
        assertNotNull(token);
        assertTrue(underTest.isTokenValid(token, subject));
    }

    /**
     * Tests the {@link JwtUtility#issueToken(String, String...)} method to ensure it can issue a valid token with a subject and scopes.
     */
    @Test
    void CanIssueValidTokenWithSubjectAndScopes() {
        // GIVEN
        String subject = "testSubject";
        String scope = "read";

        // WHEN
        String token = underTest.issueToken(subject, scope);

        // THEN
        assertNotNull(token);
        assertTrue(underTest.isTokenValid(token, subject));
        assertTrue(underTest.getClaims(token).containsKey("scopes"));
        assertTrue(underTest.getClaims(token).get("scopes").toString().contains(scope));
    }

    /**
     * Tests the {@link JwtUtility#getSubject(String)} method to ensure it retrieves the correct subject from a token.
     */
    @Test
    void canGetCorrectSubject() {
        // GIVEN
        String subject = "testSubject";
        String token = underTest.issueToken(subject);

        // WHEN
        String retrievedSubject = underTest.getSubject(token);

        // THEN
        assertEquals(subject, retrievedSubject);
    }

    /**
     * Tests the {@link JwtUtility#getExpirationDate(String)} method to ensure it sets the correct expiration date for a token.
     */
    @Test
    void canSetCorrectExpirationDate() {
        // GIVEN
        String subject = "testSubject";
        String token = underTest.issueToken(subject);

        // WHEN
        Date expirationDate = underTest.getExpirationDate(token);

        // THEN
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }
}