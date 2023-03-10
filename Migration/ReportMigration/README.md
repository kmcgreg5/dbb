# Report Migration Sample
### Overview
This sample provides a script to migrate DBB Build Reports containing javascript to static versions. This script is a combination of Unix shell scripts and Apache Groovy scripts.

### Prerequisites
* DBB Toolkit
    * DBB_HOME environment variable must be set
* IBM Java v8 64bit
    * JAVA_HOME environment variable must be set

### Folder Content
* bin - Contains the shell scripts that drive the migration process
* groovy - Contains Groovy/DBB scripts that are invoked by the shell scripts
* tests - Contains JUnit functional test cases for the sample
* tests/samples - Contains sample data for the test cases

### Migration Process
The report migration is a two step process that includes scanning the Metadata Store for the requested collections to create a list of Build Reports to be migrated, and processing the list to generate static Build Reports.

#### Create Migration List
The first step is performed by invoking the create-migration-list.sh located in the bin directory. 
```
usage: create-migration-list.sh <json-list> [options] [--help]
Using DBB version 2.0.0
 -debug,--debug              Enables DBB logging and prints groups that
                             are skipped.
 -grp,--grp <arg>            A comma seperated list of groups.
 -grpf,--grpf <arg>          A file containing groups seperated by new
                             lines.
 -help,--help                Prints this message.
 -id,--id <arg>              Db2 Metadata Store user id.
 -props,--properties <arg>   Db2 Metadata Store connection properties.
 -pw,--pw <arg>              Encrypted Db2 Metadata Store password.
 -pwFile,--pwFile <arg>      Db2 Metadata Store password file.
 -url,--url <arg>            Db2 Metadata Store URL. Example:
                             jdbc:db2:<Db2 server location>
```