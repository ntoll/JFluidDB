/*
 * Copyright (c) 2009 Nicholas H.Tollervey (ntoll) and others
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *  
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package com.fluidinfo.fom;

import java.io.IOException;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import com.fluidinfo.*;
import com.fluidinfo.utils.*;

/**
 * 
 * Base class for all other Fluid Object Model (FOM) classes
 * <p>
 * Provides shared / common functionality
 * 
 * @author ntoll
 *
 */
public abstract class BaseFOM implements FOMInterface {
	
	/**
	 * The connection to FluidDB
	 */
	protected FluidConnector fdb = null;
	
	/**
	 * The id of the object in FluidDB that corresponds to the thing represented by the 
	 * instance.
	 */
	protected String id = "";
	
	/**
	 * The instance's "root" path (namespaces, objects, tags etc)
	 */
	protected String rootPath = "";
	
	/**
	 * The instance's path within FluidDB underneath the root path
	 */
	protected String path = "";

	/**
	 * Constructor
	 * @param fdb - the connection to FluidDB to be used with this instance
	 * @param id - the id of the object in FluidDB that corresponds to the instance
	 */
	public BaseFOM(FluidConnector fdb, String id){
		this.fdb = fdb;
		this.id = id;
	}
	
	/**
	 * Return the FluidDB object id corresponding this instance
	 * @return the FluidDB object id corresponding this instance
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * Return's the instance's path in FluidDB (/objects/xyz..../ etc)
	 * @return the instance's path in FluidDB
	 */
	public String getPath(){
		String[] paths = {this.rootPath, this.path};
		return StringUtil.URIJoin(paths);
	}
	
	/**
	 * Given a path will return the name of the thing referenced (the last item in the path)
	 * @param path The path to the item
	 * @return The name of the thing referenced by the path (the last item in the path)
	 * @throws FOMException If the method cannot determine the name from the path
	 */
	protected String GetNameFromPath(String path) throws FOMException{
		// Test / normalize the path
		while (path.endsWith("/")){
			// cut any trailing "/" off the end
			path=path.substring(0, path.lastIndexOf("/"));
		}
		if(path.length()>0) {
			return path.substring(path.lastIndexOf("/")+1);
		} else {
			throw new FOMException("Cannot determine the name from the supplied path.");
		}
	}
	
	/**
	 * Used to call to the FluidDB instance
	 * @param m the HTTP method for the call
	 * @param expectedReturnCode the expected return code for a successful call
	 * @param body a String representation of the json based body
	 * @return the result from FluidDB
	 * @throws FluidException
	 * @throws IOException
	 */
	protected FluidResponse Call(final Method m, int expectedReturnCode, final String body) throws FluidException, IOException {
		return this.Call(m, expectedReturnCode, body, new Hashtable<String, String>());
	}
	
	/**
	 * 
	 * Used to call to the FluidDB instance
	 * @param m the HTTP method for the call
	 * @param expectedReturnCode the expected return code for a successful call
	 * @param body a String representation of the json based body
	 * @param args an argument dictionary to append to the end of the call URL
	 * @return the result from FluidDB
	 * @throws FluidException
	 * @throws IOException
	 */
	protected FluidResponse Call(final Method m, int expectedReturnCode, final String body, final Hashtable<String, String> args) throws FluidException, IOException{
		String callPath;
		if(this.path=="" || this.path==null)
		{
			callPath = this.rootPath;
		} else {
			String[] fluidPath = {this.rootPath, this.path};
			callPath = StringUtil.URIJoin(fluidPath);
		}
		return this.Call(m, expectedReturnCode, body, args, callPath);
	}
	
	/**
	 * Used to call to the FluidDB instance
	 * @param m the HTTP method for the call
	 * @param expectedReturnCode the expected return code for a successful call
	 * @param body a String representation of the json based body
	 * @param args an argument dictionary to append to the end of the call URL
	 * @param callPath the URI to call in FluidDB
	 * @return the result from FluidDB
	 * @throws FluidException
	 * @throws IOException
	 */
	protected FluidResponse Call(final Method m, int expectedReturnCode, final String body, final Hashtable<String, String> args, String callPath) throws FluidException, IOException{
	    return this.Call(m, expectedReturnCode, body, args, callPath, "application/json; charset=utf-8");
	}
	
	/**
	 * Used to call to the FluidDB instance
	 * @param m the HTTP method for the call
	 * @param expectedReturnCode the expected return code for a successful call
	 * @param body a String representation of the json based body
	 * @param content_type the Content-Type header to be sent
	 * @return the result from FluidDB
	 * @throws FluidException
	 * @throws IOException
	 */
	protected FluidResponse Call(final Method m, int expectedReturnCode, final String body, String callPath, String content_type) throws FluidException, IOException{
	    return this.Call(m, expectedReturnCode, body, new Hashtable<String, String>(), callPath, content_type);
    }
	
	/**
	 * Used to call to the FluidDB instance
	 * @param m the HTTP method for the call
	 * @param expectedReturnCode the expected return code for a successful call
	 * @param body a String representation of the json based body
	 * @param args an argument dictionary to append to the end of the call URL
	 * @param callPath the URI to call in FluidDB
	 * @param content_type the Content-Type header to be sent
     * @return the result from FluidDB
	 * @throws FluidException
	 * @throws IOException
	 */
	protected FluidResponse Call(final Method m, int expectedReturnCode, final String body, final Hashtable<String, String> args, String callPath, String content_type) throws FluidException, IOException{
		FluidResponse response = this.fdb.Call(m, callPath, body, args, content_type);
		if(response.getResponseCode()==expectedReturnCode){
			return response;
		} else {
		    String message = this.fdb.BuildExceptionMessageFromResponse(response);
			throw new FluidException(message);
		}
	}
	
	/**
	 * Given a path and action will return the permissions associated with it. Will return null
	 * if the logged in user is not authorized to view the permissions (they don't have the CONTROL
	 * permission
	 * 
	 * @param path e.g. /permissions/namespaces/foo
	 * @param action e.g. create, update, delete, list, control etc...
	 * @return An instance of the Permission class or null
	 * @throws FluidException
	 * @throws IOException
	 * @throws FOMException
	 * @throws JSONException
	 */
	protected Permission GetPermission(String path, String action) throws FluidException, IOException, FOMException, JSONException {
	    Hashtable<String, String> args = new Hashtable<String, String>();
	    if(action==null || action.length()>0) {
	        args.put("action", action);
	    }
	    FluidResponse response = this.fdb.Call(Method.GET, path, "", args);
	    if(response.getResponseCode()==200) {
	        // we have the permissions returned in a JSON object
	        JSONObject jsonResult = this.getJsonObject(response);
	        String policy = jsonResult.getString("policy");
	        String[] exceptions = StringUtil.getStringArrayFromJSONArray(jsonResult.getJSONArray("exceptions"));
	        Policy p;
	        if (policy.equals("open")) {
	            p = Policy.OPEN;
	        } else {
	            p = Policy.CLOSED;
	        }
	        return new Permission(p, exceptions);
	    } else if (response.getResponseCode()==401){
	        // unauthorized so return null
	        return null;
	    } else {
	        // something barfed so raise an informative exception
            String message = this.fdb.BuildExceptionMessageFromResponse(response);
            throw new FluidException(message);
	    }
	}
	
	/**
	 * Given a path, action and instance of the Permission class will attempt to appropriately
	 * update the referenced URI with the appropriate permissions
	 * @param path The path to PUT the permission
	 * @param action The action that is being updated
	 * @param permission An instance of the Permission class containing the policy and exceptions
	 * @throws JSONException
	 * @throws FluidException
	 * @throws IOException
	 */
	protected void SetPermission(String path, String action, Permission permission) throws JSONException, FluidException, IOException {
	    Hashtable<String, String> args = new Hashtable<String, String>();
	    if(action==null || action.length()>0) {
	        args.put("action", action);
	    }
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("exceptions", permission.GetExceptions());
        jsonPayload.put("policy", permission.GetPolicy().toString().toLowerCase());
        FluidResponse response = this.fdb.Call(Method.PUT, path, jsonPayload.toString(), args);
        if(response.getResponseCode()!=204) {
         // something barfed so raise an informative exception
            String message = this.fdb.BuildExceptionMessageFromResponse(response);
            throw new FluidException(message);
        }
	}
	
	/**
	 * Given a FluidResponse object will return an appropriate representation as a JSONObject
	 * @param response The FluidResponse object to process
	 * @return an appropriate representation of the FluidResponse object as a JSONObject
	 * @throws FOMException If the content type of the response is NOT "application/json"
	 * @throws JSONException If there was a problem processing the json content of the response
	 */
	protected JSONObject getJsonObject(FluidResponse response) throws FOMException, JSONException {
		String contentType = response.getResponseContentType();
		if(contentType.equals("application/json")){
			return StringUtil.getJsonObjectFromString(response.getResponseContent());
		} else {
			throw new FOMException("Unable to convert response to json because the content type is "+contentType);
		}
	}
}