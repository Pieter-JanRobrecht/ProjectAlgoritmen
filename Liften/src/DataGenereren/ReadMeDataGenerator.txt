INFO Datagenerator

LEVELS
- #Verdiepen => triviaal
- Percentage vloeren dat een lift gemiddeld aandoet => Wanneer dit 100% is zal elke lift op alle verdiepen stoppen, 
 indien dit bvb 80% is zal elke lift 'gemiddeld' op 80 percent stoppen (lift hopping nodig). 
 Er zal sowieso voor gezorgd worden dat elk verdiep met minstens 1 lift kan bereikt worden. 
 
USERS
- #Users => triviaal
- Percentage minder mobiele mensen => Dit stelt het percentage 65+-ers of kleine kinderen voor,
 die meer tijd nodig hebben om de lift in en uit te gaan. Arbitrair gekozen dat dit gemiddeld 2 keer zo traag verloopt als bij fitte mensen.
- Geduldpercentage => Dit is het algemeen geduldpercentage, indien dit 100 is zullen alle mensen de volledig verwachte time-out hebben,
 indien dit 80% is dan zullen alle mensen het slechts 80 % van deze time-out uithouden. 
- Tempo van toekomen=> Dit is de tijd die er maximaal tussen 2 opeenvolgende users zal zijn.
 Hierop wordt het afwijkingspercentage (zie onderaan) ook toegepast. Bvb. stel dat het tempo van toekomen op 20 eenheden staat en de afwijking op 10%,
 dan is een maximale tijd van 22 eenheden mogelijk*.
 -Wanneer komen het meeste mensen toe? => Indien een andere optie dan "gelijktijdig verdeeld over de dag" wordt geselecteerd, 
 dan zal een bepaalde groep van users gemiddeld dubbel zo snel toekomen als de rest. 
 Bvb. wanneer "'s ochtends" geslecteerd wordt zullen dit de eerste 33% van de gebruikers zijn.
 
ELEVATORS
- #Liften=> triviaal
- Percentage defecte liften => dit stelt het gemiddelde percentage defecte liften voor 
 waar er een technisch mankement aan is, hierbij sluit de lift gemiddeld 5 keer trager
- Liftcapaciteit => triviaal

VARIABELEN
- Deze spreken voor zich, let er wel op dat bovenstaande parameters (Percentage defecte liften, minder mobiele mensen) hier een invloed op hebben.
- Afwijkingspercentage => Deze bepaalt de maximale afwijking op de variabelen a.d.h.v. (VARIABELE*(1-offsetPercentage+2*offsetPercentage*random.nextDouble())). 

* Hierbij kan alleen een positieve afwijking bijgeteld worden, dit om te zorgen dat alle gebruikers chronologisch toekomen.


