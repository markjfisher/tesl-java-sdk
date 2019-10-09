A set of adhoc extraction utilities for generating json card data
from API and legends-decks data.

Not very tested as there's lots of manual work in between to be done,
and the model classes are copied out of the old SDK, to make reading
the old API work.

To generate nameToLDCode.csv:

```bash
for f in $(seq 1 25); do 
  curl 'https://www.legends-decks.com/cards/all/name-up/'$f'/list?f-search=&f-type=all&f-quality=all&f-race=all&f-attack-min=&f-attack-max=&f-health-min=&f-health-max=&f-magicka-min=&f-magicka-max=&f-collectible=&f-set=all&filters=Filter' > ld-$f.html
done

grep -h tooltipcard *.html | cut -d\" -f4 | awk -F/ '{printf("%s,%d\n", $6, $5)}' | sort > nameToLDCode.csv

# Add a header to the file of "name,code"
```