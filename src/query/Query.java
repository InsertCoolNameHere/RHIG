/*
Copyright (c) 2018, Computer Science Department, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/
package query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a Galileo query.
 *
 * @author malensek
 */
public class Query{

	// EXPRESSIONS ARE AND .... OPERATIONS ARE OR
    private List<Operation> operations = new ArrayList<>();
    private String time;
    public Query() { }

    public Query(Operation... operations) {
        for (Operation operation : operations) {
            addOperation(operation);
        }
    }
    
    public void setTime(String time){
    	this.time = time;
    }
    
    public String getTime(){
    	return this.time;
    }
    
    public void addOperation(Operation op) {
        operations.add(op);
    }
    
    public void addAllOperations(List<Operation> ops){
    	operations.addAll(ops);
    }

    public List<Operation> getOperations() {
        return operations;
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < operations.size(); ++i) {
            str += operations.get(i);

            if (i < operations.size() - 1) {
                str += " || ";
            }
        }
        return str;
    }

    
}
