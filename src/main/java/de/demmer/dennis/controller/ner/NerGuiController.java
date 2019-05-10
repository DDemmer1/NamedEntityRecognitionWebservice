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
    public String ner(@RequestParam(value = "language", required = false) String language, @RequestParam(value = "text", required = false) String text, @RequestParam(value = "format", required = false) String format, Model model) {


        if (text == null) {
            model.addAttribute("frSel","true");
            model.addAttribute("text", "Ceci est un texte de Dennis Demmer. Nombreuses salutations de Cologne à Laval.\n" +
                    "Je vous souhaite beaucoup de plaisir en essayant l'application Web.");
            model.addAttribute("output", "{\n" +
                    "  \"entities\": [\n" +
                    "    {\n" +
                    "      \"text\": \"Dennis Demmer\",\n" +
                    "      \"start\": 21,\n" +
                    "      \"label\": \"PER\",\n" +
                    "      \"end\": 34\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"text\": \"Cologne\",\n" +
                    "      \"start\": 62,\n" +
                    "      \"label\": \"LOC\",\n" +
                    "      \"end\": 69\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"text\": \"Laval\",\n" +
                    "      \"start\": 72,\n" +
                    "      \"label\": \"LOC\",\n" +
                    "      \"end\": 77\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"text\": \"Web\",\n" +
                    "      \"start\": 143,\n" +
                    "      \"label\": \"MISC\",\n" +
                    "      \"end\": 146\n" +
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
                model.addAttribute("deSel",true);
                break;
            case "en":
                out = ner.eval(text, language);
                model.addAttribute("enSel",true);
                break;
            case "fr":
                out = ner.eval(text, language);
                model.addAttribute("frSel",true);
                break;
            default:
                out = ner.eval(text, "en");
                model.addAttribute("enSel",true);

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
            if (xmlString != null){
                model.addAttribute("output", xmlString);
                model.addAttribute("xmlSel",true);
            }


        } else if (format.equals("json")) {
            model.addAttribute("output", prettyJson);
            model.addAttribute("jsonSel",true);

        }

        model.addAttribute("text", text);

        return "index";
    }

}
