# Notes of deploy

## Build Scripts
```bash
# To skip tests
# -DskipTests=true

# Compile for deploy to Central Maven
mvn clean deploy -Pscala_2_10 -Prelease  -P sonatype-oss-release -Dgpg.skip

### Deploy in office
mvn clean deploy -Pscala_2_10 -Prelease -Pcvnavi-nexus
```
## Build wiki from documents
