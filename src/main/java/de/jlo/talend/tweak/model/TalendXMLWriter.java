package de.jlo.talend.tweak.model;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class TalendXMLWriter extends XMLWriter {

	/** buffer used when escaping strings */
    private StringBuffer buffer = new StringBuffer();

	public TalendXMLWriter(OutputStream out, OutputFormat format) throws UnsupportedEncodingException {
		super(out, format);
	}
	
    /**
     * This will take the pre-defined entities in XML 1.0 and convert their
     * character representation to the appropriate entity reference, suitable
     * for XML attributes.
     * 
     * @param text
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
	protected String escapeAttributeEntities(String text) {
        char quote = getOutputFormat().getAttributeQuoteCharacter();

        char[] block = null;
        int i;
        int last = 0;
        int size = text.length();

        for (i = 0; i < size; i++) {
            String entity = null;
            char c = text.charAt(i);

            switch (c) {
                case '<':
                    entity = "&lt;";

                    break;

                case '>':
                    entity = "&gt;";

                    break;

                case '\'':

                    if (quote == '\'') {
                        entity = "&apos;";
                    }

                    break;

                case '\"':

                    if (quote == '\"') {
                        entity = "&quot;";
                    }

                    break;

                case '&':
                    entity = "&amp;";

                    break;

                case '\t':
                    entity = "&#x9;";

                    break;

                case '\n':
                    entity = "&#xA;";

                    break;

                case '\r':
                    entity = "&#xD;";

                    break;

                default:

                    if ((c < 32) || shouldEncodeChar(c)) {
                        entity = "&#" + (int) c + ";";
                    }

                    break;
            }

            if (entity != null) {
                if (block == null) {
                    block = text.toCharArray();
                }

                buffer.append(block, last, i - last);
                buffer.append(entity);
                last = i + 1;
            }
        }

        if (last == 0) {
            return text;
        }

        if (last < size) {
            if (block == null) {
                block = text.toCharArray();
            }

            buffer.append(block, last, i - last);
        }

        String answer = buffer.toString();
        buffer.setLength(0);

        return answer;
    }

}
