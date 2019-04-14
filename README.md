###### alisa-station


```
repo-add cxf
repo-add pax-jdbc
repo-add pax-jpa

feature:install http http-whiteboard cxf-jaxrs cxf-http-client spring spring-tx spring-aspect spring-orm spring-web pax-jdbc-config pax-jdbc-h2 pax-transx-tm-narayana hibernate-orm blueprint-web

feature:repo-add mvn:ru.drudenko/alisa-api/2.0.0/xml/features
feature:repo-add mvn:ru.drudenko/alisa-spi/2.0.0/xml/features
feature:repo-add mvn:ru.drudenko/alisa-core/2.0.0/xml/features
feature:repo-add mvn:ru.drudenko/alisa-yandex-gateway/2.0.0/xml/features
feature:repo-add mvn:ru.drudenko/alisa-google-gateway/2.0.0/xml/features
feature:install alisa-core alisa-yandex-gateway alisa-google-gateway

```

Пример конфига для datasource
```
org.ops4j.datasource-test.cfg
osgi.jdbc.driver.name=H2
databaseName=test
user=sa
password=
dataSourceName=testds-h2
```

