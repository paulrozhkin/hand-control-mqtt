# Building

Use *mvn clean install* 

In case if you have troubles with generated proto files:

Right click on module -> Maven -> Reimport 


# In proto file

For LoginRequest should include imei, login and pasword

For ClientRequest should include imei and request message(in which specify command and args).

# Postgres

Just ont simple table "credentials" with id, varchar(255) login and varchar(255) hash.

Entity: com.handcontrol.server.entity.Credentials

Repository: com.handcontrol.server.repository.CredentialsRepository
