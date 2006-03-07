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

import org.apache.axis2.om.impl.dom.DocumentImpl;
import org.apache.axis2.om.impl.dom.NodeImpl;
import org.apache.axis2.om.impl.dom.ElementImpl;
import org.apache.axis2.soap.impl.dom.SOAPBodyImpl;
import org.apache.axis2.soap.impl.dom.SOAPEnvelopeImpl;
import org.apache.ws.commons.om.OMContainer;
import org.apache.ws.commons.om.OMElement;
import org.apache.ws.commons.om.OMException;
import org.apache.ws.commons.om.OMFactory;
import org.apache.ws.commons.om.OMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.Text;
import org.w3c.dom.Comment;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

/**
 * A representation of a node (element) in a DOM representation of an XML document
 * that provides some tree manipulation methods.
 * This interface provides methods for getting the value of a node, for
 * getting and setting the parent of a node, and for removing a node.
 */
public abstract class NodeImplEx extends NodeImpl implements Node {

    /**
     * @param ownerDocument
     * @param factory
     */
    protected NodeImplEx(OMFactory factory) {
        super(factory);
    }

    protected SOAPElement parentElement;
    static final String SAAJ_NODE = "saaj.node";

    /**
     * Removes this <code>Node</code> object from the tree. Once
     * removed, this node can be garbage collected if there are no
     * application references to it.
     */
    public void detachNode() {
        this.detach();
    }

    public OMNode detach() {
        parentElement = null;
        return null;
    }

    /**
     * Removes this <code>Node</code> object from the tree. Once
     * removed, this node can be garbage collected if there are no
     * application references to it.
     */
    public SOAPElement getParentElement() {
        return this.parentElement;
    }

    public OMContainer getParent() {
        return (OMContainer) this.parentElement;
    }

    /* public OMNode getOMNode() {
        return omNode;
    }*/

    /**
     * Returns the the value of the immediate child of this <code>Node</code>
     * object if a child exists and its valu e is text.
     *
     * @return a <code>String</code> with the text of the immediate child of
     *         this <code>Node</code> object if (1) there is a child and
     *         (2) the child is a <code>Text</code> object;
     *         <code>null</code> otherwise
     */
    public String getValue() {
        if (this.getNodeType() == Node.TEXT_NODE) {
            return this.getNodeValue();
        } else if (this.getNodeType() == Node.ELEMENT_NODE) {
            return ((NodeImplEx) (((OMElement) this).getFirstOMChild())).getValue();
        }
        return null;
    }

    /**
     * Notifies the implementation that this <code>Node</code>
     * object is no longer being used by the application and that the
     * implementation is free to reuse this object for nodes that may
     * be created later.
     * <p/>
     * Calling the method <code>recycleNode</code> implies that the method
     * <code>detachNode</code> has been called previously.
     */
    public void recycleNode() {
        // No corresponding implementation in OM
        // There is no implementation in Axis 1.2 also
    }

    /**
     * Sets the parent of this <code>Node</code> object to the given
     * <code>SOAPElement</code> object.
     *
     * @param parent the <code>SOAPElement</code> object to be set as
     *               the parent of this <code>Node</code> object
     * @throws SOAPException if there is a problem in setting the
     *                       parent to the given element
     * @see #getParentElement() getParentElement()
     */
    public void setParentElement(SOAPElement parent) throws SOAPException {
        this.parentElement = parent;
    }

    public void setType(int nodeType) throws OMException {
        throw new UnsupportedOperationException("TODO");
    }

    public int getType() {
        return this.getNodeType();
    }

    public TypeInfo getSchemaTypeInfo() {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public void setIdAttribute(String name, boolean isId) throws DOMException {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Converts or extracts the SAAJ node from the given DOM Node (domNode)
     *
     * @param domNode
     * @return the SAAJ Node corresponding to the domNode
     */
    javax.xml.soap.Node toSAAJNode(org.w3c.dom.Node domNode) {
        if (domNode == null) {
            return null;
        }
        if (domNode instanceof org.w3c.dom.Text || domNode instanceof org.w3c.dom.Comment) {
            org.w3c.dom.Node prevSiblingDOMNode = domNode.getPreviousSibling();
            org.w3c.dom.Node nextSiblingDOMNode = domNode.getNextSibling();

            TextImplEx saajTextNode = (TextImplEx) ((NodeImpl) domNode).getUserData(SAAJ_NODE);
            if (saajTextNode == null){
                // if SAAJ node has not been set in userData, try to construct it
                return toSAAJNode2(domNode);
            }
            saajTextNode.setPreviousSibling(prevSiblingDOMNode);
            saajTextNode.setNextSibling(nextSiblingDOMNode);
            return saajTextNode;
        }
        Node saajNode = (Node) ((NodeImpl) domNode).getUserData(SAAJ_NODE);
        if (saajNode == null) {  // if SAAJ node has not been set in userData, try to construct it
            return toSAAJNode2(domNode);
        }
        return saajNode;
    }

    private javax.xml.soap.Node toSAAJNode2(org.w3c.dom.Node domNode) {
        if (domNode == null) {
            return null;
        }
        if (domNode instanceof org.w3c.dom.Text) {
            Text text = (Text) domNode;
            org.w3c.dom.Node prevSiblingDOMNode = text.getPreviousSibling();
            org.w3c.dom.Node nextSiblingDOMNode = text.getNextSibling();
            SOAPElementImpl parent = new SOAPElementImpl((ElementImpl) domNode.getParentNode());
            TextImplEx saajTextNode =
                    new TextImplEx(text.getData(), parent, prevSiblingDOMNode, nextSiblingDOMNode);
            ((NodeImpl) domNode).setUserData(SAAJ_NODE, saajTextNode, null);
            return saajTextNode;
        } else if (domNode instanceof org.w3c.dom.Comment) {
            Comment comment = (Comment) domNode;
            org.w3c.dom.Node prevSiblingDOMNode = comment.getPreviousSibling();
            org.w3c.dom.Node nextSiblingDOMNode = comment.getNextSibling();
            SOAPElementImpl parent = new SOAPElementImpl((ElementImpl) domNode.getParentNode());
            TextImplEx saajTextNode = new TextImplEx("<!--" +
                                                     comment.getData() +
                                                     "-->",
                                                     parent, prevSiblingDOMNode, nextSiblingDOMNode);
            ((NodeImpl) domNode).setUserData(SAAJ_NODE, saajTextNode, null);
            return saajTextNode;
        } else if (domNode instanceof org.apache.axis2.soap.impl.dom.SOAPBodyImpl) {
            org.apache.axis2.soap.impl.dom.SOAPBodyImpl doomSOAPBody = (SOAPBodyImpl) domNode;
            javax.xml.soap.SOAPBody saajSOAPBody =
                    new org.apache.axis2.saaj.SOAPBodyImpl(doomSOAPBody);
            doomSOAPBody.setUserData(SAAJ_NODE, saajSOAPBody, null);
            return saajSOAPBody;
        } else if (domNode instanceof org.apache.axis2.soap.impl.dom.SOAPEnvelopeImpl) {
            org.apache.axis2.soap.impl.dom.SOAPEnvelopeImpl doomSOAPEnv
                    = (SOAPEnvelopeImpl) domNode;
            javax.xml.soap.SOAPEnvelope saajEnvelope
                    = new org.apache.axis2.saaj.SOAPEnvelopeImpl(doomSOAPEnv);
            doomSOAPEnv.setUserData(SAAJ_NODE, saajEnvelope, null);
            return saajEnvelope;
        } else if (domNode instanceof org.apache.axis2.soap.impl.dom.SOAPFaultNodeImpl) {
            org.apache.axis2.soap.impl.dom.SOAPFaultNodeImpl doomSOAPFaultNode
                    = (org.apache.axis2.soap.impl.dom.SOAPFaultNodeImpl) domNode;
            javax.xml.soap.SOAPFaultElement saajSOAPFaultEle
                    = new org.apache.axis2.saaj.SOAPFaultElementImpl(doomSOAPFaultNode);
            doomSOAPFaultNode.setUserData(SAAJ_NODE, saajSOAPFaultEle, null);
            return saajSOAPFaultEle;
        } else if (domNode instanceof org.apache.axis2.soap.impl.dom.SOAPFaultDetailImpl) {
            org.apache.axis2.soap.impl.dom.SOAPFaultDetailImpl doomSOAPFaultDetail
                    = (org.apache.axis2.soap.impl.dom.SOAPFaultDetailImpl) domNode;
            javax.xml.soap.Detail saajDetail
                    = new org.apache.axis2.saaj.DetailImpl(doomSOAPFaultDetail);
            doomSOAPFaultDetail.setUserData(SAAJ_NODE, saajDetail, null);
            return saajDetail;
        } else if (domNode instanceof org.apache.axis2.soap.impl.dom.SOAPFaultImpl) {
            org.apache.axis2.soap.impl.dom.SOAPFaultImpl doomSOAPFault
                    = (org.apache.axis2.soap.impl.dom.SOAPFaultImpl) domNode;
            javax.xml.soap.SOAPFault saajSOAPFault
                    = new org.apache.axis2.saaj.SOAPFaultImpl(doomSOAPFault);
            doomSOAPFault.setUserData(SAAJ_NODE, saajSOAPFault, null);
            return saajSOAPFault;
        } else if (domNode instanceof org.apache.axis2.soap.impl.dom.SOAPHeaderBlockImpl) {
            org.apache.axis2.soap.impl.dom.SOAPHeaderBlockImpl doomSOAPHeaderBlock
                    = (org.apache.axis2.soap.impl.dom.SOAPHeaderBlockImpl) domNode;
            javax.xml.soap.SOAPHeaderElement saajSOAPHeaderEle
                    = new org.apache.axis2.saaj.SOAPHeaderElementImpl(doomSOAPHeaderBlock);
            doomSOAPHeaderBlock.setUserData(SAAJ_NODE, saajSOAPHeaderEle, null);
            return saajSOAPHeaderEle;
        } else if (domNode instanceof org.apache.axis2.soap.impl.dom.SOAPHeaderImpl) {
            org.apache.axis2.soap.impl.dom.SOAPHeaderImpl doomSOAPHeader
                    = (org.apache.axis2.soap.impl.dom.SOAPHeaderImpl) domNode;
            javax.xml.soap.SOAPHeader saajSOAPHeader
                    = new org.apache.axis2.saaj.SOAPHeaderImpl(doomSOAPHeader);
            doomSOAPHeader.setUserData(SAAJ_NODE, saajSOAPHeader, null);
            return saajSOAPHeader;
        } else { // instanceof org.apache.axis2.om.impl.dom.ElementImpl
            ElementImpl doomElement = (ElementImpl) domNode;
            SOAPElementImpl saajSOAPElement = new SOAPElementImpl(doomElement);
            doomElement.setUserData(SAAJ_NODE, saajSOAPElement, null);
            return saajSOAPElement;
        }
    }
}
