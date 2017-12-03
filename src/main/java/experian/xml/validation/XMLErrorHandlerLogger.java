package experian.xml.validation;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitesh.jain on 23-05-2017.
 */
public class XMLErrorHandlerLogger implements ErrorHandler {
    private List<SAXParseException> exceptions = new ArrayList<>();

    public List<SAXParseException> getExceptions() {
        return exceptions;
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        addException(exception);
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        addException(exception);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        addException(exception);
    }

    private void addException(SAXParseException exception) {
        if (!exceptions.contains(exception)) {
            exceptions.add(exception);
        }
    }

    public void clearExceptions() {
        exceptions.clear();
    }
}
