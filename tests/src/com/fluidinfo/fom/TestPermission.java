package com.fluidinfo.fom;

import static org.junit.Assert.*;
import org.junit.*;
import com.fluidinfo.utils.Policy;

/**
 * Checking all methods are exercised appropriately (attempting close to 100% coverage)
 *
 * @author ntoll
 *
 */
public class TestPermission extends Permission{
    
    /**
     * Silly constructor to make this test class work
     */
    public TestPermission() {
        super(Policy.OPEN, new String[]{"foo", "bar", "baz"});
    }
    
    @Test
    public void testGetPolicy() {
        Permission p = new Permission(Policy.OPEN, new String[]{});
        assertEquals(Policy.OPEN, p.GetPolicy());
    }
    
    @Test
    public void testGetExceptions() {
        Permission p = new Permission(Policy.CLOSED, new String[]{"foo"});
        assertEquals("foo", p.GetExceptions()[0]);
    }

}
