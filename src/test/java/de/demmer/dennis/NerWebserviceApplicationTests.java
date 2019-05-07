package de.demmer.dennis;

import com.google.gson.Gson;
import de.demmer.dennis.model.ner.NamedEntityList;
import de.demmer.dennis.service.NamedEntityRecognitionService;
import de.demmer.dennis.service.NamedEntityXmlBuilderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NerWebserviceApplicationTests {

    @Autowired
    NamedEntityRecognitionService ner;

    @Test
    public void nerTest() {

        String input = "Das ist ein Test von Dennis Demmer";
        String out = ner.eval(input,"de");

        Gson g = new Gson();
        NamedEntityList ner = g.fromJson(out, NamedEntityList.class);

        System.out.println(out);

        NamedEntityXmlBuilderService builder = new NamedEntityXmlBuilderService();

        String xmlDoc = builder.buildXML(Arrays.asList(ner.getEntities()), input);

        System.out.println(xmlDoc);

    }

}
