package jp.sf.ssoproxy.handler.html;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import jp.sf.ssoproxy.handler.element.DefaultElementHandler;
import jp.sf.ssoproxy.handler.element.ElementHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HtmlHandler extends DefaultHandler {
    private static final String ELEMENT_HANDLER = "ElementHandler";

    private static final String KEY_SEPARATOR = "-";

    private static final String DEFAULT_ELEMENT_HANDLER = "defaultElementHandler";

    private boolean writable;

    private Writer writer;

    private Map<String, Object> properties;

    private Map<String, Object> elementHanlders;

    private final ElementHandler defaultElementHander;

    public HtmlHandler() {
        this(new HashMap<String, Object>());
    }

    public HtmlHandler(final Map<String, Object> elementHanlders) {
        writable = true;
        writer = null;
        properties = new HashMap<String, Object>();
        this.elementHanlders = elementHanlders;
        defaultElementHander = new DefaultElementHandler();
    }

    public void write(final String str) {
        if (writer == null) {
            writer = new StringWriter();
        }
        try {
            writer.write(str);
        } catch (final IOException e) {
            //TODO what should i do?
        }
    }

    @Override
    public String toString() {
        if (writer == null) {
            return "";
        }
        return writer.toString();
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(final char[] ch, final int start, final int length)
            throws SAXException {
        if (isWritable()) {
            final String value = new String(ch, start, length);
            write(value);
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endDocument()
     */
    @Override
    public void endDocument() throws SAXException {
        //TODO
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String uri, final String localName,
            final String name) throws SAXException {
        final String tagName = name.toLowerCase();
        getElementHandler(tagName).endElement(this, uri, localName, name);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    @Override
    public void startDocument() throws SAXException {
        //TODO
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String uri, final String localName,
            final String name, final Attributes attributes) throws SAXException {
        final String tagName = name.toLowerCase();
        getElementHandler(tagName).startElement(this, uri, localName, name,
                attributes);
    }

    protected ElementHandler getElementHandler(final String name) {
        final StringBuilder handlerName = new StringBuilder(name);
        handlerName.append(ELEMENT_HANDLER);
        ElementHandler elementHandler = (ElementHandler) getElementHanlders()
                .get(handlerName.toString());
        if (elementHandler != null) {
            return elementHandler;
        }

        elementHandler = (ElementHandler) getElementHanlders().get(
                DEFAULT_ELEMENT_HANDLER);
        if (elementHandler != null) {
            return elementHandler;
        }

        return defaultElementHander;
    }

    /**
      * @return the writable
      */
    public boolean isWritable() {
        return writable;
    }

    /**
     * @param writable the writable to set
     */
    public void setWritable(final boolean writable) {
        this.writable = writable;
    }

    /**
     * @return the writer
     */
    public Writer getWriter() {
        return writer;
    }

    /**
     * @param writer the writer to set
     */
    public void setWriter(final Writer writer) {
        this.writer = writer;
    }

    /**
       * @return the properties
       */
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(final Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * @return the elementHanlders
     */
    public Map<String, Object> getElementHanlders() {
        return elementHanlders;
    }

    /**
     * @param elementHanlders the elementHanlders to set
     */
    public void setElementHanlders(final Map<String, Object> elementHanlders) {
        this.elementHanlders = elementHanlders;
    }

}
