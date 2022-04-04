./mvnw clean package
Uses java 11.
Uses in memory h2 database if started as web application.



Can be extended easily to rest endpoint/micro service.
All inputs as per pdf are covered in test inputs.
Feeding test data as sql files didnt work as expected in junit5/jpa.
Follow comments in FareService.


CLARIFICATIONS/TODOs
---------------------------
Logic goes wrong for people who travel only single day per week.
Code assumes input Trips is ordered.
Daily multiple limits applied?
Database entries duplicated for 1-2 and 2-1 zones.
Input should be based on datetimes rather than days of week.

The cap that is applicable for a day is based on the farthest journey in a day. -> not clear
What is all journeys are within same zone?
Why 1->2 is farthest, 2->1 isnt?