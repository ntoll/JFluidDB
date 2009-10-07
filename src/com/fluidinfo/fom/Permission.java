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

import com.fluidinfo.utils.Policy;

/**
 * See: {@link http://doc.fluidinfo.com/fluidDB/permissions.html}
 * <p>
 * A simple class used to hold permission information
 * 
 * @author ntoll
 *
 */
public class Permission {
    
    private Policy policy;
    
    private String[] exceptions;
    
    /**
     * Ctor
     * @param policy Open/Closed
     * @param exceptions The names of the users who are exceptions to the policy
     */
    public Permission(Policy policy, String[] exceptions) {
        this.policy = policy;
        this.exceptions = exceptions;
    }
    
    /**
     * The policy (open/closed)
     * @return The policy (open/closed)
     */
    public Policy GetPolicy() {
        return this.policy;
    }
    
    /**
     * The names of the users who are exceptions to the policy
     * @return The names of the users who are exceptions to the policy
     */
    public String[] GetExceptions() {
        return this.exceptions;
    }
}
