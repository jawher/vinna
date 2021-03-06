package vinna.template;

import liquidrods.Config;
import liquidrods.Liquidrods;
import liquidrods.Template;
import vinna.Session;
import vinna.Validation;
import vinna.VinnaContext;
import vinna.response.ResponseBuilder;

import javax.servlet.ServletOutputStream;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LiquidrodsView extends ResponseBuilder {
    protected Reader templateReader;
    protected String templateName;
    protected Object model;
    protected Validation validation;
    private final Config config;

    public LiquidrodsView() {
        super(200);
        type("text/html");
        final String prefix = VinnaContext.get().vinna.getBasePackage().replace(".", "/") + "/" + VinnaContext.get().vinna.getConfig().get("views-package") + "/";

        config = new Config();
        config.templateLoader(new Config.TemplateLoader() {
            @Override
            public Reader load(String name) {
                return getTemplateReader(prefix, name);
            }
        });

    }

    @Override
    protected void writeBody(ServletOutputStream out) throws IOException {

        final Template template;
        if (templateReader != null) {
            template = Liquidrods.parse(templateReader, config);
        } else {
            if (templateName == null) {
                templateName = getClass().getSimpleName().toLowerCase();
                Matcher m = Pattern.compile("(.+?)view").matcher(templateName);
                if (m.matches()) {
                    templateName = m.group(1);
                }
                templateName = templateName + ".html";
            }
            template = Liquidrods.parse(templateName, config);
        }

        final OutputStreamWriter writer = new OutputStreamWriter(out);
        template.render(this, writer);
        writer.flush();
    }

    private Reader getTemplateReader(String prefix, String name) {
        String viewPackage = getClass().getPackage().getName().replace(".", "/") + "/";

        InputStream stream = getClass().getClassLoader().getResourceAsStream(viewPackage + name);
        if (stream == null) {
            stream = getClass().getClassLoader().getResourceAsStream(prefix + name);
        }
        if (stream == null) {
            stream = getClass().getClassLoader().getResourceAsStream(name);
        }
        if (stream == null) {
            throw new RuntimeException("Can't find a template for the view class " + getClass() + ": Tried " + (viewPackage + name) + ", " + (prefix + name) + " and " + name);
        }
        try {
            return new InputStreamReader(stream, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, List<String>> errors() {
        return validation == null ? Collections.<String, List<String>>emptyMap() : validation.getErrors();
    }

    public Map<String, String> firstErrors() {
        return validation == null ? Collections.<String, String>emptyMap() : validation.getFirstErrors();
    }

    public boolean hasErrors() {
        return validation != null && validation.hasErrors();
    }

    public void validation(Validation validation) {
        this.validation = validation;
    }

    public Session session() {
        return VinnaContext.get().session;
    }

    public String contextPath() {
        return VinnaContext.get().servletContext.getContextPath();
    }
}
