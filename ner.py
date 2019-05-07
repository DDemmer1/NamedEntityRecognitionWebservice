#!/usr/bin/python
import spacy
import sys
import json

nlp = spacy.load(sys.argv[2])

text = (sys.argv[1])
doc = nlp(text)

entitylist = []
for ent in doc.ents:
    x = {
        "text": ent.text,
        "start": ent.start_char,
        "end": ent.end_char,
        "label": ent.label_
    }
    entitylist.append(x)

y = json.dumps(entitylist ,ensure_ascii=False)
print(y)




