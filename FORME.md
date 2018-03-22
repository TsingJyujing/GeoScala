# Notes of deploy

## Maven deploy to central
```bash
mvn clean deploy -P sonatype-oss-release -Dgpg.skip -Darguments="my password" 
```
## Build wiki from documents
