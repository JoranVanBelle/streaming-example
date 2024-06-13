# Streaming example

This is a rework of the streaming example I made for my [internship](https://github.com/JoranVanBelle/Stageopdracht), with the gained knowledge applied.

## Changes

During my internship, I used the high level api (also known as the DSL). This application is made with the low level api (also known as the processor api).
I also used spring and spring boot in this example so that configuration is done for me.

The way I approached events is also done differently. Here I chose for one schema per topic, during my internship I used multiple schemas per topic.

## Run locally

This example is also made so that it can be run locally. To do so you have to run `mvn spring-boot:run -Plocal`.<br/>
But be aware that it is necessary to have some credentials from [meetnet vlaamse banken](https://api.meetnetvlaamsebanken.be), or it will not work