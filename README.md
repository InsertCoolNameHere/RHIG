# RIG

RIG is a framework designed for the ROOTS-Subterra project that handles the identification of data blocks against a spatiotemporal user query and manages automated download/update with inbuilt data-integrity check. For more details about the project, click [here].

The SILO framework consists of the following components:
1)	A Front-end Visualization Framework [Link]
2)	Back-end Distributed Storage System for Pre-processing, Storage & Retrieval
3)	Command-based Customizable Data Retrieval for the user with Integrity Check [Link]

## Introduction

This repository contains the source code and deployment instructions for the RIG. 


### Commands

#### Startup & Initialize
Downloads paths for IRODS data-blocks and initializes the RIG graph.

```
init_rig [filesystem_name]
```
#### Query for Data-Blocks

Identifies relevant data-blocks for download - fetches them in the specified location and validates their integrity.

```
query_rig [fsName] [date=\"yyyy-MM-dd\"] [plotID=p1,p2,p3] [sensor=SENSOR_NAME] [path=PATH_TO_DOWNLOAD]
```


#### Shutdown

```
exit
```


# License
Copyright (c) 2020, Computer Science Department, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
