package de.demmer.dennis.controller.buzzword;


import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.demmer.dennis.model.ner.NERPostProcessing;
import de.demmer.dennis.model.ner.NamedEntity;
import de.demmer.dennis.model.ner.NamedEntityList;
import de.demmer.dennis.service.NamedEntityRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class BuzzwordTextController {

    @Autowired
    NamedEntityRecognitionService ner;



    @GetMapping(value = "/buzzword")
    public String decision(){
        return "buzzword-decision";
    }




    @GetMapping(value = "/buzzword/text")
    public String buzzword(@RequestParam(value = "number", required = false) String number, @RequestParam(value = "language", required = false) String language, @RequestParam(value = "text", required = false) String text, Model model){

        if (text == null) {
            model.addAttribute("text", "Das ist ein Test von Dennis Demmer aus Köln vom Institut für Digital Humanities.");
            return "buzzword-text";
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


//        JsonParser parser = new JsonParser();
//        JsonObject json = parser.parse(out).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String prettyJson = gson.toJson(json);


        NamedEntityList entityList = gson.fromJson(out, NamedEntityList.class);

        List<NamedEntity> entities = new ArrayList<>(Arrays.asList(entityList.getEntities()));

        NERPostProcessing.filterEntitys(entities);

        Multiset<String> multiset = HashMultiset.create();

        entities.forEach(ent -> multiset.add(ent.getText()));


        model.addAttribute("text" , text);
        ImmutableList<Multiset.Entry<String>> sortedByCount = sortedByCount(multiset);

        StringBuffer buffer = new StringBuffer();

        int numberOfBuzzwords;
        switch (number) {
            case "five":
                model.addAttribute("five",true);
                numberOfBuzzwords = 5;
                break;
            case "ten":
                model.addAttribute("ten",true);
                numberOfBuzzwords = 10;
                break;
            case "all":
                model.addAttribute("all",true);
                numberOfBuzzwords = sortedByCount.size();
                break;
            default:
                numberOfBuzzwords = 5;
        }


        for (int i = 0; i < numberOfBuzzwords; i++) {
            if (sortedByCount.size() > i) {
                buffer.append(i+1 + ". ");
                buffer.append(sortedByCount.get(i).getElement());
                buffer.append("\n");
            }
        }


       if(buffer.length()>0) buffer.deleteCharAt(buffer.length()-1);

        model.addAttribute("output" , buffer.toString());


        return "buzzword-text";
    }



    public <T> ImmutableList<Multiset.Entry<T>> sortedByCount(Multiset<T> multiset) {
        Ordering<Multiset.Entry<T>> countComp = new Ordering<Multiset.Entry<T>>() {
            public int compare(Multiset.Entry<T> e1, Multiset.Entry<T> e2) {
                return e2.getCount() - e1.getCount();
            }
        };
        return countComp.immutableSortedCopy(multiset.entrySet());
    }


}
