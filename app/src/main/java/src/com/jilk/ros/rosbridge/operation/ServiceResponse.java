/**
 * Copyright (c) 2014 Jilk Systems, Inc.
 * 
 * This file is part of the Java ROSBridge Client.
 *
 * The Java ROSBridge Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Java ROSBridge Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Java ROSBridge Client.  If not, see http://www.gnu.org/licenses/.
 * 
 */
package src.com.jilk.ros.rosbridge.operation;

import src.com.jilk.ros.message.MessageType;
import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.rosbridge.indication.*;

@MessageType(string = "service_response")
public class ServiceResponse extends Operation {
    @Indicator public String service;
    public boolean result;
    @Indicated public Message values;

    public ServiceResponse() {}
    
    public ServiceResponse(String service) {
        this.service = service;
    }    
}
