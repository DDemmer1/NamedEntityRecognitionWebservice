package de.demmer.dennis.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.demmer.dennis.model.ner.NamedEntityList;
import de.demmer.dennis.service.NamedEntityRecognitionService;
import de.demmer.dennis.service.NamedEntityXmlBuilderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
            default:
                out = ner.eval(text, "en");
                break;

        }

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(out).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

//        log.info("IN: " + text);
//        log.info("OUT: ");
//        log.info(prettyJson);


        NamedEntityList entityList = gson.fromJson(out, NamedEntityList.class);

        if(format.equals("json")){
            return prettyJson;
        } else if(format.equals("xml")){
            return new NamedEntityXmlBuilderService().buildXML(Arrays.asList(entityList.getEntities()), text);
        }

        return "Format '" + format + "' unknown. Only 'xml' and 'json' supported";
    }





}
