# Extracting Card Information

This project is a set of adhoc extraction utilities for generating json card data
from different sources: 
1. Legends API (external site)
2. A combination of SparkyPants data and legends-decks.

Not very tested as there's lots of manual work in between to be done,
and the model classes are copied out of the old SDK, to make reading
the old API work.

# Legends API

This is the data maintained by https://docs.elderscrollslegends.io/

Unfortunately, due to this data not being updated very frequently, this project
was spawned to allow for faster turn arounds on errors in data and new cards.

APICardExtractor will read all cards (using code copied from my other Java SDK)
and export them in TESL SDK format (with fixes to the old data where known errors
exist).

# Sparky Pants Data + Legends Decks

The oblivion cards were import by using the file "allCards.csv".

This was originally given by SparkyPants to get all the 2 letter codes for cards.
This file should be update with any new cards that are created (the first was
Horse Armor).

The raw data for each card is scraped from Legends-Decks website, but
in order to find the card from SP list, we need to find out its LD code.

This is done as follows:

#### Generating Legends Decks Name To Code CSV File
To generate nameToLDCode.csv:

```bash
# 25 is the number of pages in the "show all cards" list on legends decks
for f in $(seq 1 25); do 
  curl 'https://www.legends-decks.com/cards/all/name-up/'$f'/list?f-search=&f-type=all&f-quality=all&f-race=all&f-attack-min=&f-attack-max=&f-health-min=&f-health-max=&f-magicka-min=&f-magicka-max=&f-collectible=&f-set=all&filters=Filter' > ld-$f.html
done

grep -h tooltipcard *.html | cut -d\" -f4 | awk -F/ '{printf("%s,%d\n", $6, $5)}' | sort > nameToLDCode-data.csv

# Create a header to the file of "name,code"
echo "name,code" > nameToLDCode-head.csv

# Create the final output and clean up
cat nameToLDCode-head.csv nameToLDCode-data.csv > nameToLDCode.csv
rm nameToLDCode-data.csv nameToLDCode-head.csv
```

### Generating TESL card data
