## Service accounts:  allow a component to directly access the OpenShift API without sharing a regular user’s credentials.
	Replication controllers to make API calls to create or delete pods.
	Applications inside containers to make API calls for discovery purposes.
	External applications to make API calls for monitoring or integration purposes.

	Each service account’s user name is derived from its project and name:
		system:serviceaccount:<project>:<name>
	Each service account automatically contains two secrets:
		An API token
		Credentials for the OpenShift Container Registry
	Creating service accounts
		oc get sa
		oc create sa <service_account_name>
		oc describe sa <service_account_name>
	Granting roles to service accounts
		Add view role to the <service_account_name> service account 
		oc policy add-role-to-user view system:serviceaccount:<project_name>:<service_account_name>
		oc policy add-role-to-user edit -z robot
	Default cluster service accounts: Role documentation (V4.6) does not match the actual OpenShift product. Not visible?
	Default project service accounts and roles: default, builder, deployer
	Using service accounts in applications - Using a service account’s credentials externally (what's the purpose?)
		get all secrets for a service account: 
		oc describe secret robot
		get service account secret: (why there are two? both can be used to log in)
		oc get secret robot_xzxkf
		oc logout
		oc login --token=eyJhbGciOiJSUzI1NiIsImtpZCI6IlZ0UXdOW...
	Using a service account as an OAuth client (why?)

### Nodes: Instances of RHEL or RHCOS with OpenShift installed. An OpenShift server.
## Pods: 
	A pod is one or more containers deployed together on one host.
	The smallest compute unit that can be defined, deployed, and managed.
	The rough equivalent of a machine instance (physical or virtual).
	Allocated with its own internal IP address, owning its entire port space.
	Largely immutable
	Expendable
	Do not maintain state when recreated
	Managed by higher-level controllers, rather than directly by users.
	Can provides ephemeral volumes.

	oc get pods
	To view the usage statistics:
		oc adm top pods
## Configuring a cluster for pods
	Configuring pod restart policy with failed Containers: determines how OpenShift responds when Containers in that pod exit.
		Always, OnFailure, Never
		If an entire pod fails, OpenShift Container Platform starts a new pod - how?
	Limiting the bandwidth available to pods: use annotations in Pod YAML:
		    "kind": "Pod",
			"apiVersion": "v1",
			"metadata": {
				"name": "iperf-slow",
				"annotations": {
					"kubernetes.io/ingress-bandwidth": "10M",
					"kubernetes.io/egress-bandwidth": "10M"
				}
			}
	Specifying the number of pods that must be up with pod disruption budgets
		oc get poddisruptionbudget
		Depending on the pod priority and preemption settings, lower-priority pods might be removed despite their pod 
		disruption budget requirements.		
		Use a PodDisruptionBudget object to specify the minimum number or percentage of replicas that must be up at a time:
			apiVersion: policy/v1beta1 
			kind: PodDisruptionBudget
			metadata:
			  name: my-pdb
			spec:
			  minAvailable: 2  
			  selector:  
				matchLabels:
				  foo: bar
			or
			apiVersion: policy/v1beta1 
			kind: PodDisruptionBudget
			metadata:
			  name: my-pdb
			spec:
			  maxUnavailable: 25% 
			  selector: 
				matchLabels:
				  foo: bar
	Preventing pod removal using critical pods: Pod definition with priorityClassName: system-cluster-critical
## Horizontal Pod Autoscalers for Automatically scaling pods
	Horizontal pod autoscalers (HPAs)
		 OpenShift queries the CPU and/or memory metrics on the pods. The HPA computes the ratio of the
		 current metric utilization with the desired metric utilization, and scales up or down accordingly.
		 OpenShift automatically accounts for resources and prevents unnecessary autoscaling during resource 
		 spikes.
		 To use this feature, you must configure readiness checks to determine if a new pod is ready for use.
	Creating a horizontal pod autoscaler for CPU utilization
		Use the oc describe PodMetrics <pod-name> command to determine if metrics are configured.
		To create a HPA for CPU utilization: 
		  oc autoscale dc/<dc-name> --min <number> --max <number> --cpu-percent=<percent>
		  or oc autoscale rc/<rc-name> --min <number> --max <number> --cpu-percent=<percent>
		  or create HPA object using oc: oc create hpa frontend
		  or create HPA object with YAML:
			apiVersion: autoscaling/v2beta2 
			kind: HorizontalPodAutoscaler
			metadata:
			  name: cpu-autoscale 
			  namespace: default
			spec:
			  scaleTargetRef:
				apiVersion: v1 
				kind: ReplicationController 
				name: example 
			  minReplicas: 1 
			  maxReplicas: 10 
			  metrics: 
			  - type: Resource
				resource:
				  name: cpu 
				  target:
					type: Utilization 
					averageValue: 500m 
		To verify that the horizontal pod autoscaler was created: oc get hpa cpu-autoscale
	Creating a horizontal pod autoscaler object for memory utilization: create HPA object with YAML
	HPA status conditions: AbleToScale, ScalingActive, ScalingLimited
	Viewing horizontal pod autoscaler status conditions: oc describe hpa <pod-name>
## Vertical Pod Autoscaler (VPA) for Automatically adjusting pod resource levels
	About the Vertical Pod Autoscaler Operator
	Installing the Vertical Pod Autoscaler Operator
	About Using the Vertical Pod Autoscaler Operator
	Using the Vertical Pod Autoscaler Operator
## Providing sensitive data to pods
	Understanding secrets
	Types of secrets
	Example secret configurations
	Secret data keys
	Understanding how to create secrets
	Secret creation restrictions
	Creating an opaque secret
	Understanding how to update secrets
	About using signed certificates with secrets (the purpose?)
	Generating signed certificates for use with secrets
## Placing pods on specific nodes using node selectors
	A node selector specifies a map of key-value pairs. The rules are defined using custom labels
	on nodes and selectors specified in pods.

	For the pod to be eligible to run on a node, the pod must have the indicated key-value pairs
	as the label on the node.

	Using node selector labels on pods to control pod placement
		To add node selectors to an existing pod, add a node selector to the controlling object 
		for that pod, such as a ReplicaSet object, DaemonSet object, StatefulSet object, Deployment 
		object, or DeploymentConfig object. Any existing pods under that controlling object are 
		recreated on a node with a matching label. If you are creating a new pod, you can add the 
		node selector directly to the Pod spec.
		
		Adding the label to the machine set ensures that new nodes or machines will have the label.
		If adding labels to a node or machine config, the labels will not persist if the node or machine goes down
## Builds
	Docker build: Using Dockerfile and base image
	Source-to-image build: from git source or binary
	Custom build
	Pipeline build: deprecated. Using Jenkinsfile
	
## BuildConfig: describes a single build definition and a set of triggers for when a new build is created.
	typically generated automatically via the web console or CL

## Creating build inputs
	Build inputs
	Dockerfile source
	Image source
	Git source
		Using a proxy
		Source Clone Secrets
	Binary (local) source
	Input secrets and config maps
		Adding input secrets and config maps
		Source-to-image strategy
		Docker strategy
		Custom strategy
	External artifacts
	Using docker credentials for private registries
	Build environments
		Using build fields as environment variables
		Using secrets as environment variables
	What is a secret?
		Properties of secrets
		Types of Secrets
		Updates to secrets
		Creating secrets
	Service serving certificate secrets
	Secrets restrictions
	
## Using build strategies
	Docker build
		Replacing Dockerfile FROM image
		Using Dockerfile path
		Using docker environment variables
		Adding docker build arguments
	Source-to-image build
		Performing source-to-image incremental builds
		Overriding source-to-image builder image scripts
		Source-to-image environment variables: specifying them in a .s2i/environment file in the source repository.
		Ignoring source-to-image source files: using .s2iignore file
		Creating images from source code with source-to-image: the build process and S2I scripts
			build process consists of the following three fundamental elements:
			Sources
			Source-to-image (S2I) scripts
			Builder image
	Custom build
		Using FROM image for custom builds
		Using secrets in custom builds
		Using environment variables for custom builds
		Using custom builder images
	Pipeline build
		Understanding OpenShift Container Platform pipelines
			Jenkins Client Plugin is installed and enabled by default when using the OpenShift Container Platform 
			Jenkins image.
		Providing the Jenkins file for pipeline builds
		Using environment variables for pipeline builds
		Pipeline build tutorial
	Adding secrets with web console
	Enabling pulling and pushing
	
## Starting a build: oc start-build <buildconfig_name>
	Re-running a build: oc start-build --from-build=<build_name>
	Streaming build logs: oc start-build <buildconfig_name> --follow
	Setting environment variables when starting a build
	Starting a build with source
## Canceling a build: oc cancel-build <build_name>
	Canceling multiple builds: oc cancel-build <build1_name> <build2_name> <build3_name>
	Canceling all builds: oc cancel-build bc/<buildconfig_name>
	Canceling all builds in a given state
## Deleting a BuildConfig
## Viewing build details: oc describe build <build_name>
## Accessing build logs
	Accessing BuildConfig logs: oc logs -f bc/<buildconfig_name>
	Accessing BuildConfig logs for a given version build
	Enabling log verbosity
	
## Build triggers
	Webhook triggers: oc set triggers bc <name> --from-github
	Image change triggers: oc set triggers bc <name> --from-image='<image>'
	Configuration change triggers: oc set triggers bc <name> --from-config
	
	Sample yaml:
	spec:
	  triggers:
	    - type: Generic
	      generic:
		secretReference:
		  name: springbootstarter-w-config-generic-webhook-secret
	    - type: GitHub
	      github:
		secretReference:
		  name: springbootstarter-w-config-github-webhook-secret
	    - type: ImageChange
	      imageChange:
		lastTriggeredImageID: >-
		  image-registry.openshift-image-registry.svc:5000/openshift/java@sha256:d5454e60f7f1f46f10296fa276e1b05e59044fab7761be6a2fdd142b5b27c7e4
	    - type: ConfigChange
    
## Build hooks
	Configuring post commit build hooks
	Using the CLI to set post commit build hooks

## Creating applications using the CLI  
	Creating an application from source code
		Local
			oc new-app java~/mnt/c/github/springbootstarter
			oc expose service/springbootstarter
		Remote
			oc new-app  java:openjdk-11-el7~https://github.com/dhui808/springbootstarter
			oc expose service/springbootstarter
	Build strategy detection
		if Jenkinsfile exists -> pipeline strategy, otherwise -> source strategy
	Language detection
		pom.xml -> java
		app.json, package.json -> nodejs
      
		Note: language detect is not working from oc. Have to specify language explicitly.
		You can override the image the builder uses for a particular source repository by specifying the image, 
		e.g. java~ or java:openjdk-11-el7~.
	Creating an application from an image
		Docker Hub MySQL image
		Image in a private registry
			oc new-app docker.io/dannyhui/servicevirtualizationservlet
		(generating deployment, imagestream, pod. However, if doing the same from web console, service and 
		replica sets are also generated, plus optional route. Inconsistent.)	
		Existing image stream and optional image stream tag
	Creating an application from a template
		Template parameters
	Modifying application creation
		Specifying environment variables
		Specifying build environment variables
		Specifying labels
		Viewing the output without creation
		Creating objects with different names
		Creating objects in a different project
		Creating multiple objects
		Grouping images and source in a single pod
		Searching for images, templates, and other inputs

## Deployment and DeploymentConfig objects
	Deployment - Replica sets - Pods
	DeploymentConfig - Replication controllers - Pods

	Replication controllers:  ensures that a specified number of replicas of a pod are running at all times.
		The number of replicas desired, which can be adjusted at run time.
		A Pod definition to use when creating a replicated pod.
		A selector for identifying managed pods.  is a set of labels assigned to the pods.
		apiVersion: v1
		kind: ReplicationController
		metadata:
		  name: frontend-1
		spec:
		  replicas: 1  
		  selector:    
			name: frontend
		  template:    
			metadata:
			  labels:  
				name: frontend 
			spec:
			  containers:
			  - image: openshift/hello-openshift
				name: helloworld
				ports:
				- containerPort: 8080
				  protocol: TCP
			  restartPolicy: Always
	  
	Replica sets:  is a native Kubernetes API object that ensures a specified number of pod replicas are running at any given time.
		apiVersion: apps/v1
		kind: ReplicaSet
		metadata:
		  name: frontend-1
		  labels:
			tier: frontend
		spec:
		  replicas: 3
		  selector: 
			matchLabels: 
			  tier: frontend
			matchExpressions: 
			  - {key: tier, operator: In, values: [frontend]}
		  template:
			metadata:
			  labels:
				tier: frontend
			spec:
			  containers:
			  - image: openshift/hello-openshift
				name: helloworld
				ports:
				- containerPort: 8080
				  protocol: TCP
			  restartPolicy: Always	
	
	DeploymentConfig objects
		A DeploymentConfig object creates a new replication controller and lets it start up pods.
		If the deployment changes, a new replication controller is created with the latest pod template, and a deployment 
		process runs
		apiVersion: v1
		kind: DeploymentConfig
		metadata:
		  name: frontend
		spec:
		  replicas: 5
		  selector:
			name: frontend
		  template: { ... }
		  triggers:
		  - type: ConfigChange 
		  - imageChangeParams:
			  automatic: true
			  containerNames:
			  - helloworld
			  from:
				kind: ImageStreamTag
				name: hello-openshift:latest
			type: ImageChange  
		  strategy:
			type: Rolling
			
	Deployment objects: Deployments create replica sets, which orchestrate pod lifecycles.
		apiVersion: apps/v1
		kind: Deployment
		metadata:
		  name: hello-openshift
		spec:
		  replicas: 1
		  selector:
			matchLabels:
			  app: hello-openshift
		  template:
			metadata:
			  labels:
				app: hello-openshift
			spec:
			  containers:
			  - name: hello-openshift
				image: openshift/hello-openshift:latest
				ports:
				- containerPort: 80
	
	Comparing Deployment and DeploymentConfig objects
		It is recommended to use Deployment objects.
		DeploymentConfig objects prefer consistency, whereas Deployments objects take availability over consistency.
		DeploymentConfig object-specific features
			Automatic rollbacks
			Triggers
			Lifecycle hooks
		Deployment-specific features
			Rollover
			Proportional scaling
			Pausing mid-rollout

## Managing DeploymentConfig objects
	Starting a deployment: oc rollout latest dc/<name>
	Viewing a deployment: oc rollout history dc/<name>
		oc describe dc <name>
	Retrying a deployment: oc rollout retry dc/<name>
	Rolling back a deployment: oc rollout undo dc/<name>
		Image change triggers on the DeploymentConfig object are disabled as part of the rollback
		To re-enable the image change triggers:  oc set triggers dc/<name> --auto
	Executing commands inside a container: add a command to a container, which modifies the container’s start-up behavior by 
	overruling the image’s ENTRYPOINT
		spec:
		  containers:
			-
			name: example-spring-boot
			image: 'image'
			command:
			  - java
			args:
			  - '-jar'
			  - /opt/app-root/springboots2idemo.jar
	Viewing deployment logs: oc logs -f dc/<name>
	Deployment triggers: 
		Config change deployment trigger
			triggers:
			  - type: "ConfigChange"
		Image change deployment trigger
			triggers:
			  - type: "ImageChange"
				imageChangeParams:
				  automatic: true 
				  from:
					kind: "ImageStreamTag"
					name: "origin-ruby-sample:latest"
					namespace: "myproject"
				  containerNames:
					- "helloworld"
		Setting deployment triggers: oc set triggers dc/<dc_name> --from-image=<project>/<image>:<tag> -c <container_name>
	Setting deployment resources
		type: "Recreate"
		resources:
		  limits:
			cpu: "100m" 
			memory: "256Mi" 
			ephemeral-storage: "1Gi" 
	Scaling manually: oc scale dc frontend --replicas=3
	Accessing private repositories from DeploymentConfig objects: set the Pull Secret
	Assigning pods to specific nodes
		use node selectors in conjunction with labeled nodes to control pod placement.
		spec:
		  nodeSelector:
			disktype: ssd
	Running a pod with a different service account
		Add the serviceAccount and serviceAccountName parameters to the spec field:
		spec:
		  securityContext: {}
		  serviceAccount: <service_account>
		  serviceAccountName: <service_account>

## Deployment strategies for DeploymentConfig: A deployment strategy uses readiness checks to determine if a new pod is ready for use.
	Rolling strategy: A rolling deployment replaces instances of the previous version of an application with instances of the 
	new version one by one.
			maxUnavailable  = 2, maxUnavailable = 10%
		Canary deployments
			All rolling deployments are canary deployments; a new version (the canary) is tested before all of the old 
			instances are replaced. If the 
			readiness check never succeeds, the canary instance is removed and the DeploymentConfig object will be 
			automatically rolled back.
		Creating a rolling deployment using the CLI: oc scale dc/deployment-example --replicas=3
			Trigger a new deployment automatically by tagging a new version as the latest tag: 
				oc tag deployment-example:v2 deployment-example:latest
		Starting a rolling deployment using the Developer perspective
	Recreate strategy: Scales down the previous deployment to zero before scaling up the new deployment
		Starting a recreate deployment using the Developer perspective
	Custom strategy: either use "Custom" strategy or add "customParams" field in "Rolling"/"Recreate" strategy
	Lifecycle hooks: Every hook has a failure policy, which defines the action the strategy should take when a hook failure is 
	encountered
		Setting lifecycle hooks using the CLI
		oc set deployment-hook dc/frontend --pre -c helloworld -e CUSTOM_VAR1=custom_value1 --volumes data \
		--failure-policy=abort -- /usr/bin/command arg1 arg2

## Using route-based deployment strategies for DeploymentConfig
	Proxy shards and traffic splitting
	N-1 compatibility: data written by the new code can be read and handled (or gracefully ignored) by the old version of the code.
	Graceful termination
	Blue-green deployments: The new version (blue) is brought up for testing and evaluation, while the users still use the stable 
	version (green).
		Setting up a blue-green deployment: Blue-green deployments use two DeploymentConfig objects.
	A/B deployments: both old version and new version are active at the same time and some users use one version, and some users 
	use the other version.
		Load balancing for A/B testing
			When you deploy the route, the router balances the traffic according to the weights specified for the services. 
			Adding the other service as an alternateBackends and adjusting the weights brings the A/B setup to life. This 
			can be done by the 
			oc set route-backends command or by editing the route through web console or oc edit route <route_name>
				...
				metadata:
				  name: route-alternate-service
				  annotations:
					haproxy.router.openshift.io/balance: roundrobin
				spec:
				  host: ab-example.my-project.my-domain
				  to:
					kind: Service
					name: ab-example-a
					weight: 10
				  alternateBackends:
				  - kind: Service
					name: ab-example-b
					weight: 15
				...
			or
			oc set route-backends ab-example ab-example-a=198 ab-example-b=2
			oc set route-backends ab-example --adjust ab-example-b=5%
			
			One service, multiple DeploymentConfig objects
			
	In all three deployment, canary, blue-green and A/B, both old version and new version are running at the same time.
	A/B testing is for measuring functionality in the app. Blue-green deployments is about releasing new software safely and 
	rolling back predictably

## Resource quotas per project
	Resources managed by quotas
		Compute resources managed by quota
		Storage resources managed by quota
		Object counts managed by quota
	Quota scopes
	Quota enforcement
	Requests versus limits
	Sample resource quota definitions
	Creating a quota: oc create -f core-object-counts.yaml -n demoproject
		Creating object count quotas: 
			oc create quota test --hard=count/deployments.extensions=2,count/replicasets.extensions=4,count/pods=3,\
			count/secrets=4
		Setting resource quota for extended resources
	Viewing a quota
	Configuring explicit resource quotas using oc
	
## Health Checks
  Health check types:
    Readiness probes
    Liveness probes
    Startup probes
    
  Probe types:
    HTTP Get
    TCP Socket
    Container Command
    
  Monitoring health check failures using the Developer perspective
    Monitoring - Events
	
## Template

	A template is a JSON or YAML file that defines a set of resources.

	The template file is uploaded to OpenShift template library.

	The template ca be used to create any objects:
	  Services
	  Pods
	  Routes
	  Build configurations
	  Deployment configurations

	Install a template / Create a template in a project / upload a template
	  oc create -f $HOME/openshift/template/mlbparks-template-eap.json

	Create an application from the template
	  oc new-app mlbparks-eap --param MONGODB_PASSWORD=passw0rd

	Create an application from the template file
	  oc new-app --file=./example/myapp/template.json --param=MYSQL_USER=admin

	Generate configuration from a template file (just output JSON, no object is created)
	  oc process -f <template_file_name>

	Generate configuration from a template (just output JSON, no object is created)
	   oc process <template_name>

	Generate configuration and create objects
	  oc process -f <filename> | oc create -f -

	Objects generated from a template
	creating application from template generates:
	  deployment configuration, replication controller, service, pod
	  optional: config map, secret

	creating application from image-stream generates:
	  deployment, replica set, build configuration, build, image stream, service, pod
	  optional: config map, secret

	creating application from git - select Deployment
	  deployment, replica set, build configuration, build, image stream, service, pod, secret
	  optional: route

	creating application from git - select Deployment Config
	  deployment configuration, replica controller, build configuration, build, image stream, service, pod, secret
	  optional: route

	creating application from Template
	  deployment configuration, replica controller, build configuration, build, image stream, service, pod, secret, route

	creating application from Helm
	  elements similar to using Template. 

	Modifying uploaded templates
	  oc edit template <template>

	Creating a template from existing objects
	  oc get -o yaml --export all > <yaml_filename>
	(this command does not work as of oc 4.6.9)

	The object types included in oc get --export all are:
		BuildConfig
		Build
	  ImageStream
		DeploymentConfig
		ReplicationController
		Pod
		Route
		Service

## Helm

	Helm is used to deploy applications (buildconfig, deploymentconfig, imagestream, service, route) or just resources 
	(service, route) to OpenShift from yaml files.

	sudo curl -L https://mirror.openshift.com/pub/openshift-v4/clients/helm/latest/helm-linux-amd64 -o /usr/local/bin/helm
	sudo chmod +x /usr/local/bin/helm
	helm version

	Sample Helm template:
	  git clone https://github.com/redhat-gpte-devopsautomation/mlbparks-chart.git

	Install Helm chart = create application from the Helm Chart! This is different from Template!

	To install Helm Chart on OpenShift:
	  helm install mlbparks mlbparks-chart
	where 
	  mlbparks: the intended Helm template name. 
	  mlbparks-chart: Helm folder containg the following structure:

	  mlbparks-chart/
	    Chart.yaml
	    values.yaml
	    templates/
		    buildconfig.yaml
	      deploymentconfig.yaml
	      imagestream.yaml
	      route.yaml
	      service.yaml

## Understanding OpenShift Pipelines - Preview feature, using Tekton 
	Key features
	Red Hat OpenShift Pipelines concepts
	Detailed OpenShift Pipeline Concepts
		Tasks
		TaskRun
		Pipelines
		PipelineRun
		Workspaces
		Triggers
	Additional resources
## Notes
Images: Application "binary"
Containers: Runtime
Imagestreams: different versions of the same basic image. Imagestreams do not contain actual image data,
	but present a single virtual view of related images, similar to an image repository.
Image repository: a collection of related container images and tags identifying them
Imagestreamtags: An imagestreamtag is a named pointer to an image in an imagestream. An image stream tag
	is similar to a container image tag.

/etc/containers/registries.conf

