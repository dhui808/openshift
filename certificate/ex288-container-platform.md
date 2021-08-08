## Work with Red Hat OpenShift Container Platform
	Create and work with multiple OpenShift projects
	Deploy applications
	Implement application health monitoring
	Understand basic Git usage and work with Git in the context of deploying applications in OpenShift
	Configure the OpenShift internal registry to meet specific requirements

## Working with projects
	Creating a project using the web console
	Creating a project using the Developer perspective in the web console
	Creating a project using the CLI: 
		oc new-project hello-openshift --description="This is an example project" --display-name="Hello OpenShift"
	Viewing a project using the web console
	Viewing a project using the CLI
	Providing access permissions to your project using the Developer perspective
	Adding to a project
	Checking project status using the web console
	Checking project status using the CLI: oc status
	Deleting a project using the web console
	Deleting a project using the CLI: oc delete project <project_name>

## Working with Applications
	There is no such artifact as applications
### Create application from Dockerfile:
	Local
  	oc new-app /<path to source code>
	Remote:
	oc new-app https://github.com/dhui808/userreg  --strategy=docker
	(If Dockerfile is detected, by default, OpenShift uses docker startegy.)
	oc new-app https://github.com/dhui808/userreg
	Artifacts created: deployment instead of deploymentconfig
		imagestream.image.openshift.io "openjdk" created
		imagestream.image.openshift.io "userreg" created
		buildconfig.build.openshift.io "userreg" created
		deployment.apps "userreg" created
		build (failed since Docker is not able find target/userreg-0.0.1-SNAPSHOT.jar!)
	From a private remote Git repository: 
	oc new-app https://github.com/youruser/yourprivaterepo --source-secret=yoursecret
  	From  a subdirectory of a remote Git repository: 
	oc new-app https://github.com/sclorg/s2i-ruby-container.git --context-dir=2.0/test/puma-test-app
  	From a specific Git branch of a remote Git repository:
	oc new-app https://github.com/openshift/ruby-hello-world.git#beta4
### Create application from Git - builder image needs to be specified
	oc new-app openjdk~https://github.com/dhui808/userreg --strategy=source
		imagestream.image.openshift.io "openjdk" created
		imagestream.image.openshift.io "userreg" created
		buildconfig.build.openshift.io "userreg" created
		deployment.apps "userreg" created
		build (failed: /usr/libexec/s2i/assemble: No such file or directory. This is simply because the wrong 
		builder image is used)
	This one works:
	oc new-app registry.access.redhat.com/openjdk/openjdk-11-rhel7~https://github.com/dhui808/userreg  --strategy=source
	    imagestream.image.openshift.io "openjdk-11-rhel7" created
	    imagestream.image.openshift.io "userreg" created
	    buildconfig.build.openshift.io "userreg" created
	    deployment.apps "userreg" created
	    service "userreg" created
	    Build scheduled
	oc new-app -i openjdk-11-rhel7 https://github.com/dhui808/userreg  --strategy=source
	The tilde notation disables the language detection functionality of the oc new-app command. This allows the usage of 
	an image stream that points to a builder for a programming language not known by the oc new-app command.
	The -i option requires the git client to be installed locally since the language detection needs to clone the
	repository so it can inspect the project and the tilde (~) notation does not.
### Create application from Docker image in Docker Hub:
	oc new-app dannyhui/springbootstarter
		imagestream.image.openshift.io "springbootstarter" created
		deployment.apps "springbootstarter" created
		buildconfig and service and NOT created by this approach.
		However, when creating app from OpenShift Web Console, service and replicaset are created!
	Image in a private registry:
	oc new-app myregistry:5000/example/myimage
	Existing image stream and optional image stream tag:
	oc new-app my-stream:v1

	oc new-app --as-deployment-config --docker-image=registry.access.redhat.com/rhscl/mysql-56-rhel7
	or
	oc new-app --as-deployment-config registry.access.redhat.com/rhscl/mysql-56-rhel7
	--docker-image, --code and -i|--image-stream argument help oc new-app command understand if the URL is a docker image URL or source code URL
	or image stream. But they are not mandatory
### Inspect resource definitions without creating the resources in the current project, use the -o option:
	oc new-app registry.access.redhat.com/openjdk/openjdk-11-rhel7~https://github.com/dhui808/userreg  --strategy=source -o json > userreg.json
	oc new-app registry.access.redhat.com/openjdk/openjdk-11-rhel7~https://github.com/dhui808/userreg  --strategy=source -o yaml > userreg.yaml
### Creating an application from a template
	oc new-app -f examples/sample-app/application-template-stibuild.json
	oc new-app ruby-helloworld-sample -p ADMIN_USERNAME=admin -p ADMIN_PASSWORD=mypassword
	oc new-app ruby-helloworld-sample --param-file=helloworld.params
	oc new-app docker-template.yaml -p BRANCH_NAME=$BRANCH_NAME -p APP_GIT_URL=$APP_GIT_URL -p PROJECT_NAME=$PROJECT_NAME \
	-p APP_NAME=$APP_NAME
	    imagestream.image.openshift.io "hello" created
	    buildconfig.build.openshift.io "hello" created
	    deploymentconfig.apps.openshift.io "hello" created
	    service "hello" created
	    route.route.openshift.io "hello" created
	    Build scheduled, use 'oc logs -f buildconfig/hello' to track its progress.
	Multicontainers:    
	oc create -f example.yaml
	oc new-app --template quotes 
	oc get pod
	oc cp quote.sql quotesdb-1-7smgn:/tmp/quote.sql
	oc rsh -t quotesdb-1-7smgn
	mysql -u$MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE < /tmp/quote.sql
### Modifying application creation
	oc new-app openshift/postgresql-92-centos7 --env-file=postgresql.env
	Environment variables can be given on standard input:
	cat postgresql.env | oc new-app openshift/postgresql-92-centos7 --env-file=-
	Specifying build environment variables
	oc new-app openshift/ruby-23-centos7 \
		--build-env HTTP_PROXY=http://myproxy.net:1337/ \
		--build-env GEM_HOME=~/.gem
	Read the env variables from the file:
	oc new-app openshift/ruby-23-centos7 --build-env-file=ruby.env
	cat ruby.env | oc new-app openshift/ruby-23-centos7 --build-env-file=-
	Creating multiple objects
	oc new-app https://github.com/openshift/ruby-hello-world mysql
	Grouping images and source in a single pod: use the + separator
	oc new-app ruby+mysql
	To deploy an image built from source and an external image together:
	oc new-app ruby~https://github.com/openshift/ruby-hello-world mysql --group=ruby+mysql
	Searching for images, templates, and other inputs:
	oc new-app --search php
### Delete applications
	Have to delete each indidual artifact from oc or console. Consider using selector.
	oc delete deploy userreg
	oc delete bc userreg
	oc get build
	oc delete build userreg-1
	oc delete is userreg
	oc delete is openjdk
## Configuring the registry for AWS user-provisioned infrastructure
	Configuring a secret for the Image Registry Operator
		oc create secret generic image-registry-private-configuration-user \
		--from-literal=REGISTRY_STORAGE_S3_ACCESSKEY=myaccesskey \
		--from-literal=REGISTRY_STORAGE_S3_SECRETKEY=mysecretkey \
		--namespace openshift-image-registry

	Configuring registry storage for AWS with user-provisioned infrastructure
		oc edit configs.imageregistry.operator.openshift.io/cluster
		storage:
		  s3:
		    bucket: <bucket-name>
		    region: <region-name>

## Accessing the registry
	Prerequisites
		oc policy add-role-to-user registry-viewer <user_name>
		oc policy add-role-to-user registry-editor <user_name>
	Accessing registry directly from the cluster
		oc get nodes
		oc debug nodes/<node_address>
		oc login -u kubeadmin -p <password_from_install_log> https://api-int.<cluster_name>.<base_domain>:6443
		podman login -u kubeadmin -p $(oc whoami -t) image-registry.openshift-image-registry.svc:5000
	Checking the status of the registry pods
		oc get pods -n openshift-image-registry
	Viewing registry logs
		oc logs deployments/image-registry -n openshift-image-registry
	Accessing registry metrics
		curl --insecure -s -u <user>:<secret> https://image-registry.openshift-image-registry.svc:5000/extensions/v2/metrics \
		| grep imageregistry | head -n 20
	Additional resources
