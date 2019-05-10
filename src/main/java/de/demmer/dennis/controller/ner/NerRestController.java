package de.demmer.dennis.controller.ner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.demmer.dennis.model.ner.NamedEntityList;
import de.demmer.dennis.service.NamedEntityRecognitionService;
import de.demmer.dennis.service.NamedEntityXmlBuilderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@Log4j2
@RestController
public class NerRestController {


    @Autowired
    NamedEntityRecognitionService ner;

    @GetMapping(value = "/api")
    public String ner(@RequestParam String text, @RequestParam String format, @RequestParam String language) {

        String out;

        switch (language) {
            case "de":
                out = ner.eval(text, language);
                break;
            case "en":
                out = ner.eval(text, language);
                break;
            case "fr":
                out = ner.eval(text, language);
                break;
            default:
                return "Language '" + language + "' not supported. Please choose between english (en), german (de) and french (fr)";

        }


        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(out).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

//        log.info("IN: " + text);
//        log.info("OUT: ");
//        log.info(prettyJson);


        NamedEntityList entityList = gson.fromJson(out, NamedEntityList.class);

        if (format.equals("json")) {
            return prettyJson;
        } else if (format.equals("xml")) {
            return new NamedEntityXmlBuilderService().buildXML(Arrays.asList(entityList.getEntities()), text);
        }

        return "Format '" + format + "' unknown. Only 'xml' and 'json' supported";
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        System.out.println(name + " parameter is missing");

        switch (name) {
            case "language":
                return name + " parameter is missing. Please choose a supported language. German (de), french (fr) or english (en)";
            case "text":
                return name + " parameter is missing. Please choose a text to analyse";
            case "format":
                return name + " parameter is missing. Please choose 'xml' or 'json'";
            default:
                return name + " parameter is missing.";
        }

    }


}
