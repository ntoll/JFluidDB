package com.fluidinfo.fom;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Hashtable;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.*;

import com.fluidinfo.FluidConnector;
import com.fluidinfo.FluidDB;
import com.fluidinfo.FluidException;
import com.fluidinfo.FluidResponse;
import com.fluidinfo.TestUtils;
import com.fluidinfo.utils.Method;
import com.fluidinfo.utils.Policy;
import com.fluidinfo.utils.StringUtil;

/**
 * Checking all methods are exercised appropriately (attempting close to 100% coverage)
 * 
 * By using annotations and extending BaseFOM I get access to the protected methods ;-)
 * 
 * @author ntoll
 *
 */
public class TestBaseFOM extends BaseFOM {
	
	/**
	 * Silly constructor to make this test class work
	 */
	public TestBaseFOM() {
		super(null, "testID");
	}

	@Test
	public void testGetNameFromPathWorkingExamples() throws FOMException {
		// Lets make sure we get the name from the path
		String path1 = "/foo/bar/baz";
		String path2 = "/foo";
		String path3 = "foo";
		String path4 = "/foo/bar/baz/";
		
		assertEquals("baz", this.GetNameFromPath(path1));
		assertEquals("foo", this.GetNameFromPath(path2));
		assertEquals("foo", this.GetNameFromPath(path3));
		assertEquals("baz", this.GetNameFromPath(path4));
	}
	
	@Test(expected=FOMException.class)
	public void testGetNameFromPathNoPath() throws FOMException {
		// Can't get the name from this path
		this.GetNameFromPath("");
	}
	
	@Test(expected=FOMException.class)
	public void testGetNameFromPathOnlySlashes() throws FOMException {
		// Can't get the name from this path either!
		this.GetNameFromPath("//");
	}
	
	@Test
	public void testGetPath() {
		this.rootPath="/tags";
		this.path="foo";
		assertEquals("/tags/foo", this.getPath());
	}
	
	@Test
	public void testGetId() {
		assertEquals("testID", this.getId());
	}
	
	@Test
	public void testGetJsonObjectWorks() throws JSONException, FOMException {
		String jsonInput = "{ \"foo\": \"bar\"}";
		FluidResponse fR = new FluidResponse(200, "", "application/json", jsonInput);
		JSONObject jObj = this.getJsonObject(fR);
		assertEquals("bar", jObj.get("foo"));
	}
	
	@Test(expected=FOMException.class)
	public void testGetJsonObjectException() throws JSONException, FOMException {
		// Lets make it throw the exception because of the wrong response content type
		String jsonInput = "{ \"foo\": \"bar\"}";
		FluidResponse fR = new FluidResponse(200, "", "plain/text", jsonInput);
		this.getJsonObject(fR); // will throw the expected exception
	}
	
	@Test
	public void testCall() throws Exception {
		this.fdb = TestUtils.getFluidConnectionWithSettings();
		// lets get the information about the current user - shortest call signature
		this.rootPath="/users";
		this.path=this.fdb.getUsername();
		FluidResponse r = this.Call(Method.GET, 200, "");
		assertEquals(200, r.getResponseCode());
		// lets send a body in a put to the user's root namespace
		this.rootPath="/namespaces";
		JSONObject body = StringUtil.getJsonObjectFromString("{\"description\":\"a test\"}");
		r = this.Call(Method.PUT, 204, body.toString());
		// lets get the user's root namespace's information  - call sig includes the Hashtable of args
		Hashtable<String, String> args = new Hashtable<String, String>();
		args.put("returnDescription", "True");
		r = this.Call(Method.GET, 200, "", args);
		assertEquals(200, r.getResponseCode());
		JSONObject jsonResult = this.getJsonObject(r);
		assertEquals("a test", jsonResult.getString("description"));
		// Finally, lets call with the final sig: with the path specified
		String[] callPath = {"/users", this.path}; // remember the rootPath currently = "/namespaces"
		r = this.Call(Method.GET, 200, "", new Hashtable<String, String>(), StringUtil.URIJoin(callPath));
		assertEquals(200, r.getResponseCode());
	}
	
	@Test
	public void testGetPermission() throws Exception {
	    this.fdb = TestUtils.getFluidConnectionWithSettings();
	    String[] path = {"/permissions","namespaces", this.fdb.getUsername()};
	    Permission p = this.GetPermission(StringUtil.URIJoin(path), "create");
	    // if we get a permission object back we know it worked
	    assertEquals(false, p==null);
	    // if we don't have permission then the return value will be null
	    path = new String[]{"/permissions","namespaces", "fluiddb"};
        p = this.GetPermission(StringUtil.URIJoin(path), "create");
        // if we get a permission object back we know it worked
        assertEquals(true, p==null);
        // lets try that one more time without an action (for the context of policies)
        path = new String[]{"/policies", this.fdb.getUsername(), "namespaces", "create" };
        p = this.GetPermission(StringUtil.URIJoin(path), "");
        // if we get a permission object back we know it worked
        assertEquals(false, p==null);
	}
	
	@Test
	public void testSetPermission() throws Exception {
	    // Lets create a new namespace and change the permissions on it
	    this.fdb = TestUtils.getFluidConnectionWithSettings();
	    FluidDB f = new FluidDB(FluidConnector.SandboxURL);
	    f.Login(this.fdb.getUsername(), this.fdb.getPassword());
	    Namespace n = f.getLoggedInUser().RootNamespace();
	    String newName = UUID.randomUUID().toString();
	    Namespace newNamespace = n.createNamespace(newName, "For the purposes of testing");
	    String[] namespacePath = {"/permissions", newNamespace.getPath()};
	    // create the new permission
	    Permission p = new Permission(Policy.CLOSED, new String[]{"fluiddb"});
	    // set it against the namespace
	    this.SetPermission(StringUtil.URIJoin(namespacePath), "create", p);
	    // Lets call FluidDB and make sure it worked
	    Permission checkP = this.GetPermission(StringUtil.URIJoin(namespacePath), "create");
	    assertEquals(p.GetPolicy(), checkP.GetPolicy());
	    assertEquals(p.GetExceptions()[0], checkP.GetExceptions()[0]);
	    // Clean up and delete the newly created namespace
	    newNamespace.delete();
	    // lets try that one more time without an action (for the context of policies)
	    // Notice that I'm using the sandbox's test account so we don't inadvertently break a "real"
	    // user's policies. :-/
        f.Logout();
        this.fdb.setUsername("test");
        this.fdb.setPassword("test");
        f.Login("test", "test");
        String[] path = new String[]{"/policies", this.fdb.getUsername(), "namespaces", "create" };
        p = new Permission(Policy.CLOSED, new String[]{"fluidDB", this.fdb.getUsername()});
        // lets set the permission - if we don't get an exception we should assume it worked
        this.SetPermission(StringUtil.URIJoin(path), "", p);
        // but lets just check anyway ;-)
        checkP = this.GetPermission(StringUtil.URIJoin(path), "");
        assertEquals(Policy.CLOSED, checkP.GetPolicy());
        String[] exceptions = checkP.GetExceptions();
        assertEquals(2, exceptions.length);
        assertEquals(true, (exceptions[0].equals(this.fdb.getUsername()) || exceptions[1].equals(this.fdb.getUsername())));
        // Right then, lets update the permission so we can observe a change
        p = new Permission(Policy.CLOSED, new String[]{this.fdb.getUsername()});
        this.SetPermission(StringUtil.URIJoin(path), "", p);
        checkP = this.GetPermission(StringUtil.URIJoin(path), "");
        assertEquals(Policy.CLOSED, checkP.GetPolicy());
        exceptions = checkP.GetExceptions();
        assertEquals(1, exceptions.length);
        assertEquals(true, exceptions[0].equals(this.fdb.getUsername()));
	}
	
	@Test
	public void testCallWithException() throws Exception {
		this.fdb = TestUtils.getFluidConnectionWithSettings();
		this.rootPath="/namespaces";
		this.path="fluiddb"; // we don't have permissions for this namespace - it's the system account
		// Lets try to update the system account's namespace to throw an exception
		JSONObject body = StringUtil.getJsonObjectFromString("{\"description\":\"a test\"}");
		try {
			this.Call(Method.PUT, 204, body.toString());
		} catch (FluidException ex) {
			assertEquals(true, ex.getMessage().startsWith("FluidDB returned the following problematic response: 401 (Unauthorized) TPathPermissionDenied - with the request ID: "));
		}
	}

	@Override
	public void getItem() throws FluidException, IOException, FOMException,
			JSONException {
		// Doesn't do anything in this class but here because BaseFOM implements FOMInterface		
	}
}