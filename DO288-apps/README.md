# DO288 Containerized Example Applications

This repository is forked from https://github.com/RedHatTraining/DO288-apps and modified to complete DO288 exercises and labs.

Applications Deployment\
Maintenance\
Troubleshooting\
Customizing Dockerfiles and S2I Builder Images\
Using OpenShift ConfigMaps and Secrets\
Implementing Hooks and Triggers\
Working with Templates\
Application Health Monitoring


Container Images (Builder Images) from Redhat Registry

registry.access.redhat.com/rhscl/httpd-24-rhel7 \
registry.access.redhat.com/rhscl/nodejs-8-rhel7 \
registry.access.redhat.com/rhscl/php-70-rhel7 \
registry.access.redhat.com/rhscl/python-36-rhel7 \
registry.access.redhat.com/rhscl/ror-50-rhel7 \
registry.access.redhat.com/rhscl/mysql-80-rhel7\
registry.access.redhat.com/ubi8/nginx-118

How to use ssh and scp:
ssh lab-user@studentvm.4qxbn.example.opentlc.com\
scp -r pipeline-build-and-deploy-quarkus-application.yaml lab-user@studentvm.wbmqk.example.opentlc.com:/home/lab-user/coffee-shop-final-lab/coffee-shop-pipeline

## Deploying and Managing Applications on an OpenShift Cluster (Chapter 1)
###     Managing Applications with the CLI
	~research\github\openshift\DO288-apps\ubi-echo
	oc new-app --as-deployment-config --name echo https://github.com/dhui808/openshift --context-dir DO288-apps/ubi-echo
	oc expose dc/echo --port=8080
	oc expose svc/echo
	oc get pod
	oc logs -f echo-1-nfmpt

	~research\github\openshift\DO288-apps\quotes
	Remove APPLICATION_DOMAIN from php-mysql-ephemeral.json
	Upload template:
	oc create -f php-mysql-ephemeral.json
	oc new-app --as-deployment-config --template php-mysql-ephemeral -p NAME=quotesapi -p SOURCE_REPOSITORY_URL=https://github.com/dhui808/openshift -p CONTEXT_DIR=DO288-apps/quotes -p DATABASE_SERVICE_NAME=quotesdb -p DATABASE_USER=user1 -p DATABASE_PASSWORD=mypa55 --name quotes
	curl -i quotesapi-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com/get.php
	oc rsh quotesapi-1-d5rss bash -c 'echo > /dev/tcp/$DATABASE_SERVICE_NAME/3306 && echo OK || echo FAIL'
	oc rsh quotesdb-1-7zrzr bash -c "mysql -uuser1 -pmypa55 phpapp -e 'select 1'"
	To fix the table not found error:
	oc cp quote.sql quotesdb-1-7zrzr:/tmp/quote.sql
	oc rsh quotesdb-1-7zrzr bash -c "mysql -u$MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE < /tmp/quote.sql"
	curl -i quotesapi-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com/get.php
	
###     Deploying an Application to an OpenShift Cluster
	oc new-app --name hello https://github.com/dhui808/openshift.git --context-dir=DO288-apps/nodejs-helloworld --as-deployment-config
	py -m json.tool package.json

## Designing Containerized Applications for OpenShift (Chapter 2)
###     Building Container Images with Advanced Dockerfile Instructions
	To allow containers to run as the root user:
		oc create serviceaccount myserviceaccount
		oc explain deploymentconfig.spec.template.spec
		oc patch dc/demo-app --patch '{"spec":{"template":{"spec":{"serviceAccountName": "myserviceaccount"}}}}'
		oc adm policy add-scc-to-user anyuid -z myserviceaccount
		
	oc new-app --as-deployment-config --name hola https://github.com/dhui808/openshift.git --context-dir DO288-apps/container-build
	curl http://hola-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com
	Verify that the application pod fails to start
	Modify Dockerfile:
	
	FROM quay.io/redhattraining/httpd-parent
	EXPOSE 8080
	LABEL io.k8s.description="A basic Apache HTTP Server child image, uses ONBUILD" \
		io.k8s.display-name="My Apache HTTP Server" \
		io.openshift.expose-services="8080:http" \
		io.openshift.tags="apache, httpd"
	RUN sed -i "s/Listen 80/Listen 8080/g" /etc/httpd/conf/httpd.conf
	RUN chgrp -R 0 /var/log/httpd /var/run/httpd && \
	chmod -R g=u /var/log/httpd /var/run/httpd
	USER 1001

###     Injecting Configuration Data into an Application
	oc new-app --name myapp nodejs:14-ubi8~https://github.com/dhui808/openshift.git --context-dir=DO288-apps/app-config --as-deployment-config
	curl http://myapp-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com/
	Value in the APP_MSG env var is => undefined Error: ENOENT: no such file or directory, open '/opt/app-root/secure/myapp.sec'
	oc create configmap myappconf --from-literal APP_MSG="Test Message"
	oc describe cm/myappconf
	oc create secret generic myappfilesec --from-file C:\opt\openshift\DO28-apps\app-config\myapp.sec
	oc get secret/myappfilesec -o json
	oc set env dc/myapp --from cm/myappconf
	oc set volume dc/myapp --add -t secret -m /opt/app-root/secure --name myappsec-vol --secret-name myappfilesec
	oc rsh myapp-3-xcsmq env | findstr APP_MSG
	curl http://myapp-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com/
	
	Disable configuration change triggers: oc set triggers dc/mydcname --from-config --remove
	Re-enable the change triggers: oc set triggers dc/mydcname --from-config
	
	oc create configmap properties --from-file sample-config.properties
	oc get cm/properties
		NAME         DATA   AGE
		properties   1      22s
	oc get cm/properties -o jsin
	oc describe cm/properties
		
###     Designing Containerized Applications for OpenShift
	Fix the Dockerfile for an application
	oc new-app --as-deployment-config --name elvis https://github.com/dhui808/openshift.git --context-dir DO288-apps/hello-java
	Fix the Dockerfile user issue
	oc start-build elvis
	curl -i elvis-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com/api/hello
	ok
	oc new-app --as-deployment-config --name elvis https://github.com/woyaowoyao/DO288-apps.git --context-dir hello-java
	oc new-app --as-deployment-config --name elvis https://github.com/dhui808/openshift.git --context-dir hello-java
	oc new-app --as-deployment-config --name elvis https://github.com/dhui808/hello-java
	oc new-app --as-deployment-config --name elvis https://github.com/vbondoo7/DO288-apps-1a --context-dir hello-java
	It does not work from my git repo!
	oc new-app --as-deployment-config --name elvis https://github.com/dhui808/DO288-apps-hello-java --context-dir hello-java

## Publishing Enterprise Container Images (Chapter 3)
###     Authenticating OpenShift to Private Registries
	oc create secret docker-registry registrycreds \
	> --docker-server registry.example.com \
	> --docker-username youruser \
	> --docker-password yourpassword

	oc secrets link default registrycreds --for pull
	To use the secret to access an S2I builder image, link the secret to the builder service account
	oc secrets link builder registrycreds
###     Using an Enterprise Registry

	~research\github\openshift\DO288-apps\ubi-sleep
	docker build -t quay.io/yourquayioid/ubi-sleep .
	docker push quay.io/yourquayioid/ubi-sleep
	oc new-app --as-deployment-config --name ubi-sleep --docker-image quay.io/yourquayioid/ubi-sleep

###     Allowing Access to the OpenShift Registry
	Change the spec.defaultRoute attribute to true, and the Image Registry operator creates a route to expose the internal registry
	oc patch config cluster -n openshift-image-registry --type merge -p '{"spec":{"defaultRoute":true}}'
	Error from server (Forbidden): configs.operator.openshift.io "cluster" is forbidden:
	oc get route -n openshift-image-registry

	Granting Access to Images in an Internal Registry
	oc policy add-role-to-user system:image-puller user_name -n project_name
		
###     Using the OpenShift Registry
		INTERNAL_REGISTRY=$( oc get route default-route -n openshift-image-registry -o jsonpath='{.spec.host}' )
		TOKEN=$(oc whoami -t)
		
###     Creating an Image Stream
	oc describe is php -n openshift
	oc describe is nodejs -n openshift
	oc describe is python -n openshift
	oc describe is mysql -n openshift
	
	Deploy a "hello, world" application based on Nginx, using an image stream
	oc import-image hello-world --confirm --from quay.io/redhattraining/hello-world-nginx
	oc new-app --as-deployment-config --name hello -i hello-world
	curl http://hello-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com
	or 
	oc new-app --as-deployment-config --name hello --docker-image quay.io/redhattraining/hello-world-nginx

	docker build -t dannyhui/hello-world-nginx .
	docker push dannyhui/hello-world-nginx
	docker run -it -d -p 8080:8080 dannyhui/hello-world-nginx
	curl http://localhost:8080
	docker exec -it 0e5518dbd89b /bin/bash 
	
###     Publishing Enterprise Container Images
	Publish an OCI-formatted container image to an external registry and deploy an application from that image using an image stream
	Build image from Dockerfile and context:
	from php-hello-dockerfile/
	docker build -t php-hello-dockerfile .
	Run Docker image:
	docker run -it -d -p 8080:8080 php-hello-dockerfile
	curl http://localhost:8080
	ok
	From quay.io, create repo php-hello-dockerfile
	Tag the image:
	docker tag 4c14a62d7fbb quay.io/yourquayioid/php-hello-dockerfile
	docker login quay.io 
	yourquayioid
	docker push quay.io/yourquayioid/php-hello-dockerfile
	Create secret for quay.io access:
	oc create secret docker-registry quayio   --docker-server=quay.io --docker-username=yourquayioid   --docker-password=yourquayiopwd  --docker-email=you@example.com
	oc secrets link default quayio --for=pull
	oc import-image php-hello-dockerfile --confirm --reference-policy local --from quay.io/yourquayioid/php-hello-dockerfile
	Create the image stream using the --reference-policy local option so that other projects that use that image stream can also use 
	the secret stored in the youruser-common project.
	Allowing pods to reference images across projects
		Switch another project dannyhui-stage
		oc project dannyhui-stage
		Grant service accounts from the dannyhui-stage project access to image streams from the dannyhui-dev project
		(RoleBindings entry is created in dannyhui-dev)
		oc policy add-role-to-group -n dannyhui-dev system:image-puller system:serviceaccounts:dannyhui-stage
		Deploy the application using the image stream from dannyhui-dev:
		oc new-app --as-deployment-config --name phpinfo -i dannyhui-dev/php-hello-dockerfile
		curl http://phpinfo-dannyhui-stage.apps.sandbox.x8i5.p1.openshiftapps.com

## Building Applications  (Chapter 4)
###     Managing Application Builds
	oc new-app --as-deployment-config --name jhost -i redhat-openjdk18-openshift:1.5 https://github.com/RedHatTraining/DO288-apps --context-dir java-serverhost
	curl -i jhost-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com
	
	~research\github\openshift\DO288-apps\java-serverhost
	oc new-app --as-deployment-config --name jhost -i redhat-openjdk18-openshift:1.5 .
	(compiler error)
###     Triggering Builds
	To add an image change trigger: oc set triggers bc/name --from-image=project/image:tag
	To remove an image change trigger: oc set triggers bc/name --from-image=project/image:tag --remove
	The oc new-app command creates a generic and a Git webhook. To add other types of webhook, use the oc set triggers:
	oc set triggers bc/name --from-gitlab
	To remove an existing webhook: oc set triggers bc/name --from-gitlab --remove
	
	Get php builder image:
	docker pull registry.access.redhat.com/rhscl/php-70-rhel7
	docker images:
	registry.access.redhat.com/rhscl/php-70-rhel7     latest    2d5706ee4645   20 months ago   581MB
	docker tag registry.access.redhat.com/rhscl/php-70-rhel7 quay.io/yourquayioid/php-70-rhel7
	docker push quay.io/yourquayioid/php-70-rhel7
	oc import-image php70 --from quay.io/yourquayioid/php-70-rhel7 --confirm
	oc new-app --as-deployment-config --name mytrigger php70~https://github.com/RedHatTraining/DO288-apps --context-dir trigger-builds
	curl -i mytrigger-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com
	oc describe bc/mytrigger | findstr Triggered
	Update the image stream to start a new build
		
###     Implementing Post-commit Build Hooks
	oc set build-hook bc/name --post-commit --command -- bundle exec rake test --verbose
	oc set build-hook bc/name --post-commit --script="curl http://api.com/user/${USER}"
	
	oc new-app --name builds-for-managers --docker-image quay.io/redhattraining/builds-for-managers
	oc expose service/builds-for-managers
	curl builds-for-managers-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com
	oc new-app --as-deployment-config --name myhook php:7.3~https://github.com/RedHatTraining/DO288-apps --context-dir post-commit
	Integrate the PHP application build with the builds-for-managers application
	oc set build-hook bc/myhook --post-commit --command -- bash -c "curl -s -S -i -X POST http://builds-for-managers-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com/api/builds -f -d 'developer=dannyhui&git=https://github.com/RedHatTraining/DO288-apps&project=dannyhui-dev'"
	oc describe bc/myhook | findstr Post
	oc set build-hook bc/myhook --post-commit --remove
	Now use my own git url
	oc new-app --as-deployment-config --name myhook php:7.3~https://github.com/dhui808/openshift --context-dir DO288-apps/post-commit
	oc set build-hook bc/myhook --post-commit --command -- bash -c "curl -s -S -i -X POST http://builds-for-managers-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com/api/builds -f -d 'developer=dannyhui&git=https://github.com/dhui808/openshift&project=dannyhui-dev'"
	oc start-build bc/myhook -F
	(does not work - SQL Table "BUILD" not found)

###     Building Applications		
	oc new-app --as-deployment-config --name simple https://github.com/dhui808/openshift.git --context-dir DO288-apps/build-app
	curl http://simple-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com
	Use the generic webhook for the build configuration to start a new application build.
	oc describe bc simple
	Get the secret for the webhook:
	oc get bc simple -o jsonpath="{.spec.triggers[*].generic.secret}{'\n'}"
	CiiZrjc6-y-FFX4heJZE
	Get the webhook url:
	oc describe bc simple
	https://api.sandbox.x8i5.p1.openshiftapps.com:6443/apis/build.openshift.io/v1/namespaces/dannyhui-dev/buildconfigs/simple/webhooks/<secret>/generic
	Start a new build using the webhook URL, and the secret
	curl -X POST -k https://api.sandbox.x8i5.p1.openshiftapps.com:6443/apis/build.openshift.io/v1/namespaces/dannyhui-dev/buildconfigs/simple/webhooks/CiiZrjc6-y-FFX4heJZE/generic

## Customizing Source-to-Image Builds (Chapter 5)
###     Customize the S2I scripts of an existing S2I builder image
	Explore the S2I scripts packaged in the rhscl/httpd-24-rhel7 builder image.
	docker pull registry.access.redhat.com/rhscl/httpd-24-rhel7
	docker run --name test -it registry.access.redhat.com/rhscl/httpd-24-rhel7 bash
	cat /usr/libexec/s2i/assemble
	cat /usr/libexec/s2i/run
	cat /usr/libexec/s2i/usage
	oc new-app --as-deployment-config --name bonjour httpd:2.4~https://github.com/dhui808/openshift.git --context-dir DO288-apps/s2i-scripts
	curl http://bonjour-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com
	curl http://bonjour-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com/info.html

	oc new-app --as-deployment-config --name hellophp php:7.4-ubi8~https://github.com/dhui808/openshift.git --context-dir DO288-apps/php-s2i-scripts
	curl http://hellophp-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com
		
###     Creating an S2I Builder Image
	Building an Nginx S2I Builder Image:
	s2i create s2i-do288-nginx s2i-do288-nginx
	Modify assemble
	docker build -t s2i-do288-nginx .
	Build a test application container image:
	s2i build test/test-app s2i-do288-nginx nginx-test --as-dockerfile test/Dockerfile
	Application dockerfile generated in test/Dockerfile
	Build the test container, using the Dockerfile generated by the s2i build command above:
	docker build -t nginx-test test
	Test the container image:
	docker run -d -p 8080:8080 nginx-test
	curl http://localhost:8080

	docker run -d -p 8080:8080 s2i-do288-nginx
	curl http://localhost:8080

	Push the S2I builder image to quay.io
	From quay.io, create repo s2i-do288-nginx
	Tag the image:
	docker tag s2i-do288-nginx quay.io/xyz/s2i-do288-nginx
	docker push quay.io/xyz/s2i-do288-nginx
	
	The s2i build command requires the use of a local Docker service.
	To provide support for environments that do not have Docker available, the s2i build command now includes the --as-dockerfile path/to/Dockerfile 
	option. By using this option, no local Docker daemon is required to run the s2i build command

###     Create an OpenShift image stream for the Nginx S2I builder image:
	oc import-image s2i-do288-nginx --from quay.io/xyz/s2i-do288-nginx --confirm
	Create applications using the Nginx S2I builder image:
	oc new-app --as-deployment-config s2i-do288-nginx~https://github.com/dhui808/openshift.git --context-dir DO288-apps/html-helloworld --name hellonginx --strategy=source
	curl http://hellonginx-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com
		
###     Building an httpd S2I Builder Image: from DO288-apps/
	s2i create s2i-do288-httpd s2i-do288-httpd
	Modify assemble, run (https://github.com/goern/s2i-apache-httpd/tree/master/2.4/s2i/bin)
	docker build -t s2i-do288-httpd .
	s2i build test/test-app s2i-do288-httpd httpd-test --as-dockerfile test/Dockerfile
	docker build -t httpd-test test
	docker run -d -p 8080:8080 httpd-test
	curl http://localhost:8080
	docker tag s2i-do288-httpd quay.io/xyz/s2i-do288-httpd
	(Without creating repo from Quay console, a direct push creates a private repo! Must link the secret the secret with builder sa)
	docker push quay.io/xyz/s2i-do288-httpd
	oc create secret docker-registry quayio   --docker-server=quay.io ---docker-username=<your quay id>   --docker-password=<quay pwd>  --docker-email=<your email>
	oc secrets link builder quayio
	oc import-image s2i-do288-httpd --from quay.io/xyz/s2i-do288-httpd --confirm
	oc new-app --as-deployment-config s2i-do288-httpd~https://github.com/dhui808/openshift.git --context-dir DO288-apps/nodejs-helloworld --name hellohttpd --strategy=source
	curl http://hellohttpd-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com
		
###     Building a go S2I Builder Image: from DO288-apps/
	https://github.com/sclorg/golang-container/tree/master/1.15
	s2i create s2i-do288-go s2i-do288-go
	docker build -t s2i-do288-go .

	cd ~\research\github\openshift\DO288-apps
	s2i build .\go-hello  s2i-do288-go go-hello-dockerfile --as-dockerfile .\go-dockerfile\Dockerfile
	docker build -t go-test go-dockerfile
	docker run -d -p 8080:8080 go-test
	curl http://localhost:8080/Universe

	docker tag s2i-do288-go quay.io/xyz/s2i-do288-go
	docker push quay.io/xyz/s2i-do288-go
	oc create secret docker-registry quayio   --docker-server=quay.io --docker-username=<your quay id>   --docker-password=<quay pwd>  --docker-email=<your email>
	oc secrets link builder quayio
	oc import-image s2i-do288-go --from quay.io/xyz/s2i-do288-go --confirm
	oc new-app --as-deployment-config s2i-do288-go~https://github.com/dhui808/openshift.git --context-dir DO288-apps/golang-ex --name hellogo --strategy=source
	curl http://hellogo-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com

	oc new-app golang~https://github.com/dhui808/openshift.git --context-dir DO288-apps/golang-ex --as-deployment-config --name hellogo
	oc expose dc/hellogo --port=8080
	oc expose svc/hellogo
	curl http://hellogo-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com
    
## Creating Applications from OpenShift Templates (Chapter 6)
###     Creating a Multicontainer Template
Manually Creating Resource Files
	use the oc explain and oc api-resources commands to view the attributes for each resource type
Concatenating Resource Files				oc new-app -o json/yaml can create a resource definition file, instead of creating resources
Exporting Existing Resources
	oc get -o json/yaml
	oc get -o yaml is,bc,dc,svc,route > mytemplate.yaml
Describing OpenShift Resource Types
	oc explain command lists the top-level attributes for that resource type:
	oc explain routes
	oc explain routes.spec
Creating an Application from a Template
	oc new-app command creates resources from the template, 
	oc process command creates a resource list from the template
	oc new-app --file mytemplate.yaml -p PARAM1=value1 -p PARAM2=value2
	oc process -f mytemplate.yaml -p PARAM1=value1 -p PARAM2=value2 > myresourcelist.yaml
	oc process -f mytemplate.yaml -p PARAM1=value1 -p PARAM2=value2 | oc create -f -
Listing parameters
	oc process --parameters -f mytemplate.yaml

Lab
	~research\github\do288-apps-quotes
	oc create -f quotes.yaml
	template.template.openshift.io/quotes created
	oc get templates quotes
	oc new-app --as-deployment-config --template quotes -p APP_GIT_URL=https://github.com/dhui808/do288-apps-quotes -p SECRET=my5ecret -p PASSWORD=mypa55word --name quotes
	curl http://quotesapi-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com
	oc get route/quotesapi -o jsonpath='{.spec.host}{\"\n\"}'

	oc get -o yaml is > /tmp/is.yaml
	oc get -o yaml bc > /tmp/bc.yaml
	oc get -o yaml dc > /tmp/dc.yaml
	oc get -o yaml svc > /tmp/svc.yaml
	oc get -o yaml route > /tmp/route.yaml
	oc get -o yaml pvc > /tmp/pvc.yaml

	~research\github\openshift\DO288-apps\chapter6-create-template
	oc delete all -l app=quotesapi
	oc delete all -l app=quotesdb
	oc delete pvc quotesdb-claim
	oc new-app --as-deployment-config --file quotes-template.yaml -p APP_GIT_URL=https://github.com/dhui808/do288-apps-quotes -p SECRET=my5ecret -p PASSWORD=mypa55word --name quotes
	curl http://quotesapi-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com
	oc get pod -l deploymentconfig=quotesdb -o name
	oc rsh quotesdb-1-fk2vx
	vi /tmp/quotes.sql
	(copy & paste from local quotes.sql)
	mysql -uquoteapp -pmypa55word quotesdb < /tmp/quotes.sql
	exit
	curl http://quotesapi-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com/get.php

###     Creating Applications from OpenShift Templates
	~research\github\openshift\DO288-apps\chapter6-template
	oc new-app --file todo-template.yaml -p NPM_PROXY=https://registry.access.redhat.com/rhscl/nodejs-4-rhel7 -p PASSWORD=mypass -p CLEAN_DATABASE=true -p APP_GIT_URL=https://github.com/dhui808/do288-apps-todo-single -p HOSTNAME=todoapp-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com
	curl http://todoapp-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com/todo
			
## Managing Application Deployments (Chapter 7)
###     Activating Probes
	oc new-app --as-deployment-config --name probes nodejs~https://github.com/dhui808/openshift --context-dir DO288-apps/probes
	curl -i probes-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com
	curl -i probes-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com/ready
	curl -i probes-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com/healthy

	oc set probe dc/probes --liveness --get-url=http://:8080/healthz --initial-delay-seconds=2 --timeout-seconds=2
	oc set probe dc/probes --readiness --get-url=http://:8080/ready --initial-delay-seconds=2 --timeout-seconds=2

###     Implementing a Deployment Strategy
	oc new-app --as-deployment-config --name mysql -e MYSQL_USER=test -e MYSQL_PASSWORD=redhat -e MYSQL_DATABASE=testdb --docker-image registry.access.redhat.com/rhscl/mysql-57-rhel7
	oc set triggers dc/mysql --from-config --remove
	Change the default deployment strategy:
	oc patch dc/mysql --type=json --patch '{"spec":{"strategy":{"type":"Recreate"}}}'
	(does not work)

### Managing Application Deployments with CLI
	oc rollout latest dc/name
	To view the history of deployments: oc rollout history dc/name
	To view the history of a specific deployment: oc rollout history dc/name --revision=1
	oc describe dc name
	To cancel a deployment: oc rollout cancel dc/name
	To retry a failed deployment: oc rollout retry dc/name
	oc rollback dc/name
	To prevent accidentally starting a new deployment process after a rollback is complete: oc set triggers dc/mydcname --from-config --remove
	After a rollback, you can re-enable image change triggers: oc set triggers dc/mydcname --auto
	oc scale dc/name --replicas=3
	To set the ImageChange trigger:
	oc set triggers dc/name --from-image=myproject/origin-ruby-sample:latest -c helloworld
		
###     Managing Application Deployments
	Scale pods
	Redeploy app
	Roll back a change
	oc new-app --as-deployment-config --name quip -i redhat-openjdk18-openshift:1.5 https://github.com/dhui808/openshift --context-dir DO288-apps/quip
	curl http://quip-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com
	oc set probe dc/quip --liveness --get-url=http://:8080/ready --initial-delay-seconds=30 --timeout-seconds=2
	oc set probe dc/quip --readiness --get-url=http://:8080/ready --initial-delay-seconds=30 --timeout-seconds=2
	oc describe dc/quip
	oc describe pod quip-3-hppqxi
	curl -X POST -k -H "Content-Type: application/json" https://api.sandbox.x8i5.p1.openshiftapps.com:6443/apis/build.openshift.io/v1/namespaces/dannyhui-dev/buildconfigs/quip/webhooks/DAkiLM-JdSqjTUBPgq6k/generic
	oc rollback dc/quip

	oc new-app --as-deployment-config --name scale php:7.3~https://github.com/dhui808/openshift --context-dir DO288-apps/php-scale
	curl http://scale-dannyhui-dev.apps.sandbox.x8i5.p1.openshiftapps.com
	Manual Scaling: oc scale dc/scale --replicas=2
	Make code change
	Trigger build by generic webhook:
	curl -X POST -k -H "Content-Type: application/json"  https://api.sandbox.x8i5.p1.openshiftapps.com:6443/apis/build.openshift.io/v1/namespaces/dannyhui-dev/buildconfigs/scale/webhooks/kUYRofurYFZZ1x_S8KEs/generic
	Roll back:
	oc rollback dc/scale
	Re-deploy:
	oc rollout latest dc/scale
		
## Deploying a Containerized Nexus Server (Chapter 9)
###     Deploying a Containerized Nexus Server
	https://github.com/OpenShiftDemos/nexus/blob/master/nexus3-persistent-template.yaml
	~research\github\openshift\DO288-apps\nexus3
	docker build -t nexus3 .
	docker tag nexus3 quay.io/yourquayioid/nexus3
	docker push quay.io/yourquayioid/nexus3
	~research\github\openshift\DO288-apps\nexus3-template
	oc new-app --as-deployment-config --name nexus3 -f nexus3-persistent-template.yaml -p IMAGE_NAME=quay.io/yourquayioid/nexus3
	(does not work)
	
