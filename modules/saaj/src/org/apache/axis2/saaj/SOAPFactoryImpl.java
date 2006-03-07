/*                                                                             
 * Copyright 2004,2005 The Apache Software Foundation.                         
 *                                                                             
 * Licensed under the Apache License, Version 2.0 (the "License");             
 * you may not use this file except in compliance with the License.            
 * You may obtain a copy of the License at                                     
 *                                                                             
 *      http://www.apache.org/licenses/LICENSE-2.0                             
 *                                                                             
 * Unless required by applicable law or agreed to in writing, software         
 * distributed under the License is distributed on an "AS IS" BASIS,           
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    
 * See the License for the specific language governing permissions and         
 * limitations under the License.                                              
 */
package org.apache.axis2.saaj;

import org.apache.axis2.om.DOOMAbstractFactory;
import org.apache.axis2.om.impl.dom.ElementImpl;
import org.apache.axis2.om.impl.dom.factory.OMDOMFactory;
import org.apache.axis2.soap.impl.dom.soap11.SOAP11FaultDetailImpl;
import org.apache.axis2.soap.impl.dom.soap11.SOAP11FaultImpl;
import org.apache.ws.commons.om.OMElement;
import org.apache.ws.commons.om.OMNamespace;
import org.apache.ws.commons.soap.SOAPFaultDetail;
import org.apache.ws.commons.soap.SOAPFault;

import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

/**
 * 
 */
public class SOAPFactoryImpl extends SOAPFactory {

    /**
     * Create a <code>SOAPElement</code> object initialized with the
     * given <code>Name</code> object.
     *
     * @param name a <code>Name</code> object with the XML name for
     *             the new element
     * @return the new <code>SOAPElement</code> object that was
     *         created
     * @throws javax.xml.soap.SOAPException if there is an error in creating the
     *                                      <code>SOAPElement</code> object
     */
    public SOAPElement createElement(Name name) throws SOAPException {
        String localName = name.getLocalName();
        String prefix = name.getPrefix();
        String uri = name.getURI();
        OMElement omElement = DOOMAbstractFactory.getOMFactory().createOMElement(localName, uri, prefix);
        return new SOAPElementImpl((ElementImpl) omElement);
    }

    /**
     * Create a <code>SOAPElement</code> object initialized with the
     * given local name.
     *
     * @param localName a <code>String</code> giving the local name for
     *                  the new element
     * @return the new <code>SOAPElement</code> object that was
     *         created
     * @throws javax.xml.soap.SOAPException if there is an error in creating the
     *                                      <code>SOAPElement</code> object
     */
    public SOAPElement createElement(String localName) throws SOAPException {
        OMDOMFactory omdomFactory = (OMDOMFactory) DOOMAbstractFactory.getOMFactory();

        OMNamespace ns = omdomFactory.createOMNamespace(null, null);
        OMElement omElement = omdomFactory.createOMElement(localName, ns);
        return new SOAPElementImpl((ElementImpl) omElement);
    }

    /**
     * Create a new <code>SOAPElement</code> object with the given
     * local name, prefix and uri.
     *
     * @param localName a <code>String</code> giving the local name
     *                  for the new element
     * @param prefix    the prefix for this <code>SOAPElement</code>
     * @param uri       a <code>String</code> giving the URI of the
     *                  namespace to which the new element belongs
     * @return the new <code>SOAPElement</code> object that was
     *         created
     * @throws javax.xml.soap.SOAPException if there is an error in creating the
     *                                      <code>SOAPElement</code> object
     */
    public SOAPElement createElement(String localName, String prefix, String uri) throws SOAPException {
        OMElement omElement = DOOMAbstractFactory.getOMFactory().createOMElement(localName, uri, prefix);
        return new SOAPElementImpl((ElementImpl) omElement);
    }

    /**
     * Creates a new <code>Detail</code> object which serves as a container
     * for <code>DetailEntry</code> objects.
     * <p/>
     * This factory method creates <code>Detail</code> objects for use in
     * situations where it is not practical to use the <code>SOAPFault</code>
     * abstraction.
     *
     * @return a <code>Detail</code> object
     * @throws javax.xml.soap.SOAPException if there is a SOAP error
     */
    public Detail createDetail() throws SOAPException { 
        return new DetailImpl(DOOMAbstractFactory.getSOAP11Factory().createSOAPFaultDetail());
    }

    /**
     * Creates a new <code>Name</code> object initialized with the
     * given local name, namespace prefix, and namespace URI.
     * <p/>
     * This factory method creates <code>Name</code> objects for use in
     * situations where it is not practical to use the <code>SOAPEnvelope</code>
     * abstraction.
     *
     * @param localName a <code>String</code> giving the local name
     * @param prefix    a <code>String</code> giving the prefix of the namespace
     * @param uri       a <code>String</code> giving the URI of the namespace
     * @return a <code>Name</code> object initialized with the given
     *         local name, namespace prefix, and namespace URI
     * @throws javax.xml.soap.SOAPException if there is a SOAP error
     */
    public Name createName(String localName, String prefix, String uri) throws SOAPException {
        return new PrefixedQName(uri, localName, prefix);
    }

    /**
     * Creates a new <code>Name</code> object initialized with the
     * given local name.
     * <p/>
     * This factory method creates <code>Name</code> objects for use in
     * situations where it is not practical to use the <code>SOAPEnvelope</code>
     * abstraction.
     *
     * @param localName a <code>String</code> giving the local name
     * @return a <code>Name</code> object initialized with the given
     *         local name
     * @throws javax.xml.soap.SOAPException if there is a SOAP error
     */
    public Name createName(String localName) throws SOAPException {
        return new PrefixedQName(null, localName, null);
    }
}
