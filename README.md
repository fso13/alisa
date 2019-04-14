###### alisa-station

добавляем внешние фичи

```
repo-add cxf
repo-add pax-jdbc
repo-add pax-jpa
```

устанавливаем необходимое

```
feature:install http http-white board cxf-jaxrs cxf-http-client spring spring-tx spring-orm spring-web pax-jdbc-config pax-jdbc-h2 pax-transx-tm-narayana hibernate-orm blueprint-web
```

добавляем фичи наших бандлов и устанавливаем
```
feature:repo-add mvn:ru.drudenko/alisa-core/LATEST/xml/features
feature:repo-add mvn:ru.drudenko/alisa-yandex-gateway/LATEST/xml/features
feature:repo-add mvn:ru.drudenko/alisa-google-gateway/LATEST/xml/features
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

