package com.fluidinfo.utils;

import static org.junit.Assert.*;

import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.*;
import com.fluidinfo.fom.FOMException;

/**
 * Checking the string utils (currently only join) work correctly
 * 
 * @author ntoll
 *
 */
public class TestStringUtil {
	
	@Test
	public void testJoin(){
		// Single item in the path
		Vector<String> paths = new Vector<String>();
		paths.add("/foo");
		assertEquals("/foo", StringUtil.join(paths.toArray(new String[0]), "/"));
		// Empty path
		paths.clear();
		assertEquals("", StringUtil.join(paths.toArray(new String[0]), "/"));
		// two elements in the path
		paths.add("/foo");
		paths.add("bar");
		assertEquals("/foo/bar", StringUtil.join(paths.toArray(new String[0]), "/"));
		assertEquals("/foo/bar", StringUtil.join(paths, "/")); // make sure it works with a collection
	}
	
	@Test
	public void testURIJoin() {
		Vector<String> paths = new Vector<String>();
		// elements that already contain the delimiter
		paths.add("/foo");
		paths.add("bar/");
		paths.add("/baz");
		assertEquals("/foo/bar/baz", StringUtil.URIJoin(paths.toArray(new String[0])));
		assertEquals("/foo/bar/baz", StringUtil.URIJoin(paths)); // make sure it works with a collection
	}
	
	@Test
    public void testGetStringArrayFromJSONArray() throws JSONException {
        String jsonInput = "{\"foo\": [ \"bar\", \"baz\"]}";
        JSONObject jObj = StringUtil.getJsonObjectFromString(jsonInput);
        String[] result = StringUtil.getStringArrayFromJSONArray(jObj.getJSONArray("foo"));
        assertEquals(2, result.length);
        assertEquals("bar", result[0]);
        assertEquals("baz", result[1]);
    }
	
	@Test
    public void testGetJsonObjectWorks() throws JSONException, FOMException {
        // With a single String
        String jsonInput = "{ \"foo\": \"bar\"}";
        JSONObject jObj = StringUtil.getJsonObjectFromString(jsonInput);
        assertEquals("bar", jObj.get("foo"));
    }
	
	@Test
	public void testValidatePath() {
	    // lets start with a good case
	    String path = "foo/bar/baz";
	    assertEquals(true, StringUtil.validatePath(path));
	    // another form of a good case
	    path = "/foo/bar/baz";
	    assertEquals(true, StringUtil.validatePath(path));
	    // null case
	    assertEquals(false, StringUtil.validatePath(null));
	    // empty case
	    assertEquals(false, StringUtil.validatePath(""));
	    // bad case at end
	    path = "foo/bar!";
	    assertEquals(false, StringUtil.validatePath(path));
	    // bad case at start
	    path ="!foo/bar";
	    assertEquals(false, StringUtil.validatePath(path));
	    // bad case in middle
	    path = "foo/b@r";
        assertEquals(false, StringUtil.validatePath(path));
	    // several bad cases at once
	    path = "foo/&a$";
	    assertEquals(false, StringUtil.validatePath(path));
	    // good chars but just too long (len>233 chars)
	    // this is length=234
	    path = "foo/bar/baz/ham/and/eggs/cheese/and/pickle/spam/spam/spam/spam/foo/bar/baz/ham/and/eggs/cheese/and/pickle/spam/spam/spam/spam/foo/bar/baz/ham/and/eggs/cheese/and/pickle/spam/spam/spam/spam/foo/bar/baz/ham/and/eggs/cheese/and/pickle/ok";
	    assertEquals(false, StringUtil.validatePath(path));
	    // this is length=233 (so will pass)
	    path = "foo/bar/baz/ham/and/eggs/cheese/and/pickle/spam/spam/spam/spam/foo/bar/baz/ham/and/eggs/cheese/and/pickle/spam/spam/spam/spam/foo/bar/baz/ham/and/eggs/cheese/and/pickle/spam/spam/spam/spam/foo/bar/baz/ham/and/eggs/cheese/and/pickle/a";
        assertEquals(true, StringUtil.validatePath(path));
	}
}
