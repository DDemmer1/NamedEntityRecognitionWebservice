package de.demmer.dennis.controller;


import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
public class BuzzwordController {

    @Autowired
    NamedEntityRecognitionService ner;

    @GetMapping(value = "/buzzword")
    public String buzzword(@RequestParam(value = "lg", required = false) String language, @RequestParam(value = "text", required = false) String text, Model model){

        if (text == null){
            return "buzzword";
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


        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(out).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);


        NamedEntityList entityList = gson.fromJson(out, NamedEntityList.class);


        List<NamedEntity> entities = Arrays.asList(entityList.getEntities());


        Multiset<String> multiset = HashMultiset.create();

        entities.forEach(ent -> multiset.add(ent.getText()));


        model.addAttribute("text" , text);
        ImmutableList<Multiset.Entry<String>> sortedByCount = sortedByCount(multiset);

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 5; i++) {
            if(sortedByCount.size()>i) {
               buffer.append(sortedByCount.get(i).getElement());
               buffer.append(", ");
            }

        }

        model.addAttribute("output" , buffer.toString());


        return "buzzword";
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
