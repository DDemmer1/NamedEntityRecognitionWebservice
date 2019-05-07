package de.demmer.dennis.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class NamedEntityRecognitionService {

    public String eval(String text, String language) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("{\"entities\":");

        try {

            String[] cmd = {
                    "python3",
                    "ner.py",
                    text,
                    language
            };
            Process p = Runtime.getRuntime().exec(cmd);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String s;
            while ((s = stdInput.readLine()) != null) {
                stringBuffer.append(s);
            }
            stringBuffer.append("}");


        } catch (IOException e) {
            e.printStackTrace();
        }


        return stringBuffer.toString();
    }

}

