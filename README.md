# sherlock-io

This repository, sherlock-io, is for uploading and managing an implementation of knowledge-based protocol inference (SeM2Bit and modified L* algorithm) and its two case studies (SLP and MQTT). Now we are trying to make the original source code readible and the refined version will be uploaded once the refinement job is done. 

It is a JAVA project and implemented on IntelliJ IDEA. For its execution, it is recommended to use 'Build Artifacts' operations. Especially, for SLP case study, a direct execution in IDEA causes a 'Permission denied' error.

For SLP case study, two main classes should be built:
* ch.ethz.iks.slp.test.Registration.java
* heesuk.sem2bit.main.SDPMain.java

Registration.java is for registering an SLPv2 service agent and SDPMain.java is for adapting an SLPv1 message to interact with the registered SLPv2 SA. 

For MQTT case study, other two main classes should be built:
* io.moquette.server.Server.java
* heesuk.sem2bit.main.MQTTMain.java

Server.java is for running an MQTTv5 compatible broker and MQTTMain.java is for adapting an MQTTv3.1.1 message to connect to the running MQTTv5 broker.

To run the extracted jar program, the config file should be customized according to the local environment setups.
