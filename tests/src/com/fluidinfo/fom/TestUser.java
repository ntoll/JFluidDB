package com.fluidinfo.fom;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.*;
import org.json.JSONException;
import com.fluidinfo.*;
import com.fluidinfo.utils.Policy;
import com.fluidinfo.utils.StringUtil;
import com.fluidinfo.TestUtils;

/**
 * Exercise the User class
 * 
 * @author ntoll
 *
 */
public class TestUser extends User {
	
	/**
	 * Constructor
	 * 
	 * @throws Exception
	 */
	public TestUser() throws Exception {
		super(TestUtils.getFluidConnectionWithSettings(), "id", TestUtils.getFluidConnectionWithSettings().getUsername());
	}
	
	@Test
	public void testConstructor() {
		// using the information in the overridden ctor defined above
		assertEquals("id", this.getId());
		String[] path = {"/users", this.fdb.getUsername()};
		assertEquals(StringUtil.URIJoin(path), this.getPath());
		assertEquals(this.fdb.getUsername(), this.getUsername());
	}
	
	@Test
	public void testGetItem() throws Exception {
		User testUser = new User(this.fdb, "", this.fdb.getUsername());
		// There isn't anything there
		assertEquals("", testUser.getId());
		assertEquals("", testUser.getName());
		// Apart from the username
		assertEquals(this.fdb.getUsername(), testUser.getUsername());
		// Lets call FluidDB and populate the fields... now there should be an ID
		testUser.getItem();
		assertEquals(true, testUser.getId().length()>0);
		assertEquals(true, testUser.getName().length()>0);
	}
	
	@Test
	public void testGetUserName() throws Exception {
		User testUser = new User(this.fdb, "", this.fdb.getUsername());
		testUser.getItem();
		// get the test user's name from the credentials.json file.
		assertEquals(this.fdb.getUsername(), testUser.getUsername());
	}
	
	@Test
    public void testGetName() throws Exception {
        User testUser = new User(this.fdb, "", this.fdb.getUsername());
        testUser.getItem();
        assertEquals(TestUtils.getUserRealName(), testUser.getName());
    }
	
	@Test
	public void testGetNamespace() throws FOMException, FluidException, JSONException, IOException {
		// Lets get the root namespace
		this.getItem();
		Namespace rootNamespace = this.RootNamespace();
		String[] path = {"/namespaces", this.fdb.getUsername()};
		assertEquals(StringUtil.URIJoin(path), rootNamespace.getPath());
	}
	
	@Test
    public void testGetSetNamespacePolicy() throws Exception {
	    // we'll use the test account in the sandbox so we don't munge up a "real" user's policy details
	    this.fdb.setUsername("test");
        this.fdb.setPassword("test");
        FluidDB f = new FluidDB(FluidConnector.SandboxURL);
        f.Login("test", "test");
        User u = f.getLoggedInUser();
        Permission p = new Permission(Policy.CLOSED, new String[]{"fluidDB", this.fdb.getUsername()});
        u.setNamespacePolicy(Namespace.Actions.CREATE, p);
        // lets just check that went through ;-)
        Permission checkP = u.getNamespacePolicy(Namespace.Actions.CREATE);
        assertEquals(Policy.CLOSED, checkP.GetPolicy());
        String[] exceptions = checkP.GetExceptions();
        assertEquals(2, exceptions.length);
        assertEquals(true, (exceptions[0].equals(this.fdb.getUsername()) || exceptions[1].equals(this.fdb.getUsername())));
        // Right then, lets update the permission so we can observe a change with get
        p = new Permission(Policy.CLOSED, new String[]{this.fdb.getUsername()});
        u.setNamespacePolicy(Namespace.Actions.CREATE, p);
        checkP = u.getNamespacePolicy(Namespace.Actions.CREATE);
        assertEquals(Policy.CLOSED, checkP.GetPolicy());
        exceptions = checkP.GetExceptions();
        assertEquals(1, exceptions.length);
        assertEquals(true, exceptions[0].equals(this.fdb.getUsername()));
	}
	
	@Test
    public void testGetSetTagPolicy() throws Exception {
        // we'll use the test account in the sandbox so we don't munge up a "real" user's policy details
        this.fdb.setUsername("test");
        this.fdb.setPassword("test");
        FluidDB f = new FluidDB(FluidConnector.SandboxURL);
        f.Login("test", "test");
        User u = f.getLoggedInUser();
        Permission p = new Permission(Policy.CLOSED, new String[]{"fluidDB", this.fdb.getUsername()});
        u.setTagPolicy(Tag.TagActions.UPDATE, p);
        // lets just check that went through ;-)
        Permission checkP = u.getTagPolicy(Tag.TagActions.UPDATE);
        assertEquals(Policy.CLOSED, checkP.GetPolicy());
        String[] exceptions = checkP.GetExceptions();
        assertEquals(2, exceptions.length);
        assertEquals(true, (exceptions[0].equals(this.fdb.getUsername()) || exceptions[1].equals(this.fdb.getUsername())));
        // Right then, lets update the permission so we can observe a change with get
        p = new Permission(Policy.CLOSED, new String[]{this.fdb.getUsername()});
        u.setTagPolicy(Tag.TagActions.UPDATE, p);
        checkP = u.getTagPolicy(Tag.TagActions.UPDATE);
        assertEquals(Policy.CLOSED, checkP.GetPolicy());
        exceptions = checkP.GetExceptions();
        assertEquals(1, exceptions.length);
        assertEquals(true, exceptions[0].equals(this.fdb.getUsername()));
    }
	
	@Test
    public void testGetSetTagValuePolicy() throws Exception {
        // we'll use the test account in the sandbox so we don't munge up a "real" user's policy details
        this.fdb.setUsername("test");
        this.fdb.setPassword("test");
        FluidDB f = new FluidDB(FluidConnector.SandboxURL);
        f.Login("test", "test");
        User u = f.getLoggedInUser();
        Permission p = new Permission(Policy.CLOSED, new String[]{"fluidDB", this.fdb.getUsername()});
        u.setTagValuePolicy(Tag.TagValueActions.UPDATE, p);
        // lets just check that went through ;-)
        Permission checkP = u.getTagValuePolicy(Tag.TagValueActions.UPDATE);
        assertEquals(Policy.CLOSED, checkP.GetPolicy());
        String[] exceptions = checkP.GetExceptions();
        assertEquals(2, exceptions.length);
        assertEquals(true, (exceptions[0].equals(this.fdb.getUsername()) || exceptions[1].equals(this.fdb.getUsername())));
        // Right then, lets update the permission so we can observe a change with get
        p = new Permission(Policy.CLOSED, new String[]{this.fdb.getUsername()});
        u.setTagValuePolicy(Tag.TagValueActions.UPDATE, p);
        checkP = u.getTagValuePolicy(Tag.TagValueActions.UPDATE);
        assertEquals(Policy.CLOSED, checkP.GetPolicy());
        exceptions = checkP.GetExceptions();
        assertEquals(1, exceptions.length);
        assertEquals(true, exceptions[0].equals(this.fdb.getUsername()));
    }
}
