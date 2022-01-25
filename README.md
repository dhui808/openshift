# OpenShift
OpenShift-related articles, examples and tutorials

1. Deploy Spring Boot application to Openshift from Web Console
[from Web Console](https://github.com/dhui808/openshift/blob/master/from-console/fromconsole.md)
	
2. Deploy Spring Boot application with external configuration to Openshift from Web Console
[from Web Console](https://github.com/dhui808/openshift/blob/master/from-console-with-config/from-console-with-config.md)

3. Deploy Spring Boot application with external configuration to Openshift from oc
[from oc CLI](https://github.com/dhui808/openshift/blob/master/from-oc-to-openshift.txt)

## How to use ssh, cp and docker cp: 
    ssh lab-user@studentvm.4qxbn.example.opentlc.com
    docker cp Dockerfile 18d3a906af76:/tmp/Dockerfile
    oc cp myproject-dev/springbootstarter-w-config-3-fqvwq:/opt/springbootstarter-w-config-logs/app.log  logs/app.log
    oc rsh frontend-1-zvjhb
