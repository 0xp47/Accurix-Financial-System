package com.accounting.util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for BCrypt password hashing and validation.
 */
public class PasswordUtilTest {

    @Test
    public void testHashAndVerifySuccess() {
        String rawPassword = "admin123";
        String hashed = PasswordUtil.hashPassword(rawPassword);

        assertNotNull(hashed);
        assertTrue(hashed.startsWith("$2a$10$") || hashed.startsWith("$2b$10$") || hashed.startsWith("$2y$10$"));
        assertTrue(PasswordUtil.checkPassword(rawPassword, hashed));
    }

    @Test
    public void testVerifyIncorrectPassword() {
        String rawPassword = "password123";
        String hashed = PasswordUtil.hashPassword(rawPassword);

        assertFalse(PasswordUtil.checkPassword("wrongpassword", hashed));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHashNullPassword() {
        PasswordUtil.hashPassword(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHashEmptyPassword() {
        PasswordUtil.hashPassword("");
    }

    @Test
    public void testCheckNullOrMalformedHash() {
        assertFalse(PasswordUtil.checkPassword("password", null));
        assertFalse(PasswordUtil.checkPassword(null, "somehash"));
        assertFalse(PasswordUtil.checkPassword("password", "invalid-hash-string"));
    }

    @Test
    public void testDefaultAccountCredentials() {
        String adminHash = "$2a$10$eEb1kdfA976Hdk6PEA3Qh.NvQr0Lwu8Gw722slfqscuJ/UiHeF3/2";
        String accountantHash = "$2a$10$eQ3ekID568jC/vppifHc1eVXfVy.y6rYYlEKNK.NRDOhS1Gx2K2Em";

        assertTrue("admin123 must match default admin hash", PasswordUtil.checkPassword("admin123", adminHash));
        assertTrue("password123 must match default accountant hash", PasswordUtil.checkPassword("password123", accountantHash));
    }
}
