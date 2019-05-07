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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

@Log4j2
@Controller
public class NerGuiController {

    @Autowired
    NamedEntityRecognitionService ner;


    @GetMapping(value = "/")
    public String ner(@RequestParam(value = "lg", required = false) String language, @RequestParam(value = "text", required = false) String text, @RequestParam(value = "format", required = false) String format, Model model) {


        if (text == null) {
            model.addAttribute("text", "Das ist ein Test von Dennis Demmer aus Köln vom Institut für Digital Humanities.");
            model.addAttribute("output", "{\n" +
                    "  \"entities\": [\n" +
                    "    {\n" +
                    "      \"text\": \"Dennis Demmer\",\n" +
                    "      \"end\": 34,\n" +
                    "      \"label\": \"PER\",\n" +
                    "      \"start\": 21\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"text\": \"Köln\",\n" +
                    "      \"end\": 43,\n" +
                    "      \"label\": \"LOC\",\n" +
                    "      \"start\": 39\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"text\": \"Institut für Digital Humanities\",\n" +
                    "      \"end\": 79,\n" +
                    "      \"label\": \"ORG\",\n" +
                    "      \"start\": 48\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}");
            return "index";
        }

        if (format == null) {
            format = "json";
        }

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

        System.out.println(out);
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(out).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);


        NamedEntityList entityList = gson.fromJson(out, NamedEntityList.class);


        String xmlString = new NamedEntityXmlBuilderService().buildXML(Arrays.asList(entityList.getEntities()), text);

        if (format.equals("xml")) {
            if (xmlString != null)
                model.addAttribute("output", xmlString);
        } else if (format.equals("json")) {
            model.addAttribute("output", prettyJson);

        }

        model.addAttribute("text", text);

        return "index";
    }

}
