# RHIG

RHIG is a framework designed for the ROOTS-Subterra project that handles the identification of data blocks against a spatiotemporal user query and manages automated download/update with inbuilt data-integrity check. For more details about the project, click [here].

The RADIX+ framework consists of the following components:
1)	A Front-end Visualization Framework [Link]
2)	Back-end Distributed Storage System for Pre-processing, Storage & Retrieval
3)	Command-based Customizable Data Retrieval for the user with Integrity Check [Link]

This repository contains the source code and deployment instructions for the RHIG. 

## Setup

Please set the environment variables *IRODS_USER* and *IRODS_PASSWORD* as your irods username and password for the application to connect to CyVerse properly. You may also hardcode these values insode *RIG/src/irods/IRODSManager.java* at line 100 and then rebuild the jar(explained below).

### Containerization
If you wish to deploy a dockerized container running RHIG, please update the *RIG/Dockerfile* with your CyVerse username and password. Navigate to *RIG* project root and run the following commands from your terminal:
```
docker build -t rhig .
```
Run your container using the following command:
```
docker run -t -i rhig
```

Following this, you can run RHIG commands from your container's terminal (see Commands section).

## Deployment
To run your own local SUBTERRA repository monitored by RHIG, run the **RHIG-1.0.jar** file by navigating to the **RIG/dist** directory. Startup the RHIG application by running the **RHIG-1.0.jar** file through the following command:
```
java -cp RHIG-1.0.jar node.RIGMonitor
```

To update and build, go to the project's root directory and run the command: 

```
ant package
```
The built jar can be found at **RIG/dist/RHIG-1.0.jar**

### Commands

#### Startup & Initialize
Downloads paths for IRODS data-blocks and initializes the RHIG graph.

```
init_rhig [filesystem_name] [use_local](optional)(boolean) [local_rhig_log_directory]

local_rhig_log_directory is a path where temporary/log data is stored by the application

Example> init_rhig roots-arizona-2018 true /tmp/rhig_demo



```
#### Query for Data-Blocks

Identifies relevant data-blocks for download - fetches them in the specified location and validates their integrity.

```
query_rhig [fsName] [date=\"yyyy-MM-dd\"](optional) [plotID=p1,p2,p3](optional) [sensor=SENSOR_NAME](optional) [path=PATH_TO_LOCAL_REPOSITORY]
```

#### Update existing Data-Repository

Identifies relevant data-blocks for download - fetches them in the specified location and validates their integrity.

```
query_rhig [fsName] [date=\"yyyy-MM-dd\"](optional) [plotID=p1,p2,p3](optional) [sensor=SENSOR_NAME](optional) [path=PATH_TO_LOCAL_REPOSITORY]

Example> update_rhig roots-arizona-2018 plotID=20525,20526 sensor=lidar,irt path=/s/chopin/e/proj/sustain/sapmitra/arizona/rhig_repo
```

#### Shutdown

```
exit
```


# License
CopyRHIGht (c) 2020, Computer Science Department, Colorado State University
All RHIGhts reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyRHIGht notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyRHIGht notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyRHIGht holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyRHIGht holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
