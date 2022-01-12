# Deployment Notes

Run tests:

```bash
./scripts/test.sh
```

Run tests, cut a new JAR, and deploy to Clojars:

```bash
./scripts/deploy.sh
Enter your Clojars username (not email): buc***
Enter your Clojars token: ***
Deploying...
```

The updated JAR will deploy to `com.teachbanzai/inspecter`. (com.teachbanzai is our 
newly verified group ID with Clojars.)

