package vinna.template;

import liquidrods.Config;
import liquidrods.Liquidrods;
import vinna.VinnaContext;
import vinna.response.ResponseBuilder;

import javax.servlet.ServletOutputStream;
import java.io.*;

public class LiquidrodsResponse extends ResponseBuilder {
    private final String view;
    private final Object model;
    private final String prefix;

    private LiquidrodsResponse(String view, Object model) {
        super(200);
        type("text/html");
        this.view = view;
        this.model = model;
        this.prefix = VinnaContext.get().vinna.getBasePackage().replace(".", "/") + "/views/";
    }

    public static LiquidrodsResponse liquid(String view, Object model) {
        return new LiquidrodsResponse(view, model);
    }

    @Override
    protected void writeBody(ServletOutputStream out) throws IOException {

        Config config = new Config();
        config.templateLoader(new Config.TemplateLoader() {
            @Override
            public Reader load(String name) {
                return getTemplateReader(name);
            }
        });


        final OutputStreamWriter writer = new OutputStreamWriter(out);
        Liquidrods.parse(view).render(model, writer);
        writer.flush();
    }


    private Reader getTemplateReader(String name) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(prefix + name);
        if (stream == null) {
            stream = getClass().getClassLoader().getResourceAsStream(view);
        }
        try {
            return new InputStreamReader(stream, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
