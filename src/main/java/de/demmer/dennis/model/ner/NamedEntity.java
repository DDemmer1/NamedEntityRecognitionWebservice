package de.demmer.dennis.model.ner;

import lombok.Data;

@Data
public class NamedEntity {

    private int start;
    private int end;
    private String text;
    private String label;



}
