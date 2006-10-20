package org.apache.axis2.rpc.receivers;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.engine.ObjectSupplier;
import org.apache.axis2.databinding.typemapping.SimpleTypeMapper;
import org.apache.axis2.databinding.utils.BeanUtil;
import org.apache.axis2.util.StreamWrapper;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import java.lang.reflect.Method;
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
*
*
*/

public class RPCUtil {

    private static String RETURN_WRAPPER = "return";

    public static void processResponse(SOAPFactory fac, Object resObject,
                                       OMElement bodyContent,
                                       OMNamespace ns,
                                       SOAPEnvelope envelope,
                                       Method method, boolean qualified) {
        if (resObject != null) {
            //simple type
            if (resObject instanceof OMElement) {
                OMElement result = (OMElement) resObject;
                bodyContent = fac.createOMElement(
                        method.getName() + "Response", ns);
                OMElement resWrapper;
                if (qualified) {
                    resWrapper = fac.createOMElement(RETURN_WRAPPER, ns.getNamespaceURI(),
                            ns.getPrefix());
                } else {
                    resWrapper = fac.createOMElement(RETURN_WRAPPER, null);
                }
                resWrapper.addChild(result);
                bodyContent.addChild(resWrapper);
            } else if (SimpleTypeMapper.isSimpleType(resObject)) {
                bodyContent = fac.createOMElement(
                        method.getName() + "Response", ns);
                OMElement child;
                if (qualified) {
                    child = fac.createOMElement(RETURN_WRAPPER, ns);
                } else {
                    child = fac.createOMElement(RETURN_WRAPPER, null);
                }
                child.addChild(fac.createOMText(child, SimpleTypeMapper.getStringValue(resObject)));
                bodyContent.addChild(child);
            } else {
                bodyContent = fac.createOMElement(
                        method.getName() + "Response", ns);
                // Java Beans
                QName returnWrapper;
                if (qualified) {
                    returnWrapper = new QName(ns.getNamespaceURI(), RETURN_WRAPPER, ns.getPrefix());
                } else {
                    returnWrapper = new QName(RETURN_WRAPPER);
                }
                XMLStreamReader xr = BeanUtil.getPullParser(resObject,
                        returnWrapper);
                StAXOMBuilder stAXOMBuilder =
                        OMXMLBuilderFactory.createStAXOMBuilder(
                                OMAbstractFactory.getOMFactory(), new StreamWrapper(xr));
                OMElement documentElement = stAXOMBuilder.getDocumentElement();
                if (documentElement != null) {
                    bodyContent.addChild(documentElement);
                }
            }
        }
        if (bodyContent != null) {
            envelope.getBody().addChild(bodyContent);
        }
    }

    public static Object[] processRequest(OMElement methodElement,
                                          Method method  , ObjectSupplier objectSupplier) throws AxisFault {
        Class[] parameters = method.getParameterTypes();
        return BeanUtil.deserialize(methodElement, parameters  , objectSupplier);
    }

    public static OMElement getResponseElement(QName resname, Object [] objs, boolean qualified) {
        if (qualified) {
            return BeanUtil.getOMElement(resname, objs,
                    new QName(resname.getNamespaceURI(), RETURN_WRAPPER, resname.getPrefix()));
        } else {
            return BeanUtil.getOMElement(resname, objs,
                    new QName(RETURN_WRAPPER));
        }
    }

    public static OMElement getResponseElementForArray(QName resname, Object [] objs, boolean qualified) {
        if (qualified) {
            return BeanUtil.getOMElement(resname, objs,
                    new QName(resname.getNamespaceURI(), RETURN_WRAPPER, resname.getPrefix()));
        } else {
            return BeanUtil.getOMElement(resname, objs,
                    new QName(RETURN_WRAPPER));
        }
    }

}
