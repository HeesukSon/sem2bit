# sherlock-io

sherlock-io is an implementation of SeM2Bit and its two case studies: SLP and MQTT. It is a JAVA project and implemented on IntelliJ IDEA. 

For its execution, it is recommended to use 'Build Artifacts' operations. Especially, for SLP case study, a direct execution in IDEA causes a 'Permission denied' error.

For SLP case study, two main classes should be built:
* ch.ethz.iks.slp.test.Registration.java
* heesuk.sem2bit.main.SDPMain.java

Registration.java is for registering an SLPv2 service agent and SDPMain.java is for adapting an SLPv1 message to interact with the registered SLPv2 SA. 

For MQTT case study, other two main classes should be built:
* io.moquette.server.Server.java
* heesuk.sem2bit.main.MQTTMain.java

Server.java is for running an MQTTv5 compatible broker and MQTTMain.java is for adapting an MQTTv3.1.1 message to connect to the running MQTTv5 broker.
