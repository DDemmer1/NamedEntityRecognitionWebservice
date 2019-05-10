package de.demmer.dennis.model.ner;

import java.util.ArrayList;
import java.util.List;

public class NERPostProcessing {

    public static List<NamedEntity> filterEntitys(List<NamedEntity> nerList) {

        List<NamedEntity> toRemove = new ArrayList<>();

        for (NamedEntity ne : nerList) {
            //filter all named entitys which consists of only special chars
            if (ne.getText().matches("[^A-Za-z0-9]")) {
                toRemove.add(ne);
            }

            //Filter line seperators in entitys
            ne.setText(ne.getText().replace(System.lineSeparator()," "));
            ne.setText(ne.getText().replace("»",""));
            ne.setText(ne.getText().replace("«",""));

            //filter common scientific abbreviations
            if(ne.getText().equals("c’") || ne.getText().contains("S.") || ne.getText().contains("Anm.") || ne.getText().contains("Bd.") || ne.getText().contains("Vol.")){
                toRemove.add(ne);
            }
        }
        nerList.removeAll(toRemove);
        return nerList;
    }

}
