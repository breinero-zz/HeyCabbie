# HeyCabbie
Demo application of a Lamda Architecture

Watch the video presentation of this demo application
http://experience.mongodb.com/spotlight/lambda-architectures-and-streaming-data-processing-with-mongodb-543572

This project uses code instrumentation packages in one of my other projects, Firehose https://github.com/breinero/Firehose. This dependency is not yet handled in the gradle file. That is on my TODO

The following example application uses the epfl/mobility dataset which  contains mobility traces of taxi cabs in San Francisco, USA. YOu may download the dataset here https://s3-us-west-2.amazonaws.com/bi-connector-docs/logs.dsv.gz.
This dataset contains GPS coordinates of approximately 500 taxis collected over 30 days in the San Francisco Bay Area. You may download the dataset for your own testing from the Community Resource for Archiving Wireless Data At Dartmouth (CRAWDAD).

Attribution 
Michal Piorkowski, Natasa Sarafijanovic‑Djukic, Matthias Grossglauser, CRAWDAD dataset epfl/mobility (v. 2009/02/24), downloaded from http://crawdad.org/epfl/mobility/20090224, doi:10.15783/C7J010, Feb 2009.

BUILD this Demo 
from the command line run
./gradlew jettRun
Useage

Web Interface 

Display cab rides in progess at given time in the past
http://localhost:8081/web/fare?ts=[some timestamp bewteen 1211018404 and 1213089934]

Report on total Distance each taxi has driven at given time in the past
http://localhost:8081/web/dist?ts=[some timestamp bewteen 1211018404 and 1213089934]

Report on this application's performance, displaying latency in servicing requests
http://localhost:8081/web/stats
