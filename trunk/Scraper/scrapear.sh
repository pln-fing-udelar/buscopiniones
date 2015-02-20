#!/bin/bash
#
# cambio el directorio a donde tengo que estar
cd /home/buscopiniones/Scraper/dist/
export JAVA_HOME=/home/buscopiniones/jdk1.7.0_45
export PATH=${JAVA_HOME}/bin:${PATH}
export LANG="es_UY.UTF-8"
while true; do 
  java -jar Scraper.jar < parametros.txt
  cd /home/buscopiniones/ProyGrado/htmlprocesado
  java -jar reductorXML.jar
  find -size -1k -delete
  find -size +500M -delete
  cd /home/buscopiniones/solr-4.1.0/example
  for noNoticias in $(find /home/buscopiniones/ProyGrado/htmlprocesado -name "*.xml" | grep -v ".*Noticias.*")
  do
    java -Durl=http://localhost:8983/solr/update -jar post.jar $noNoticias
  done
  for noticias in $(find /home/buscopiniones/ProyGrado/htmlprocesado -name "*.xml" | grep ".*Noticias.*")
  do
    java -Durl=http://localhost:8984/solr/update -jar post.jar $noticias
  done
  sleep 1800
done
