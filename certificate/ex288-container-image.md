## Work with container images
	Use command line utilities to manipulate container images
	Optimize container images

## Containers, images, and image streams
	Image registry: registry.redhat.io
	Image repository: docker.io/openshift/jenkins-2-centos7
	Image tags: registry.access.redhat.com/openshift3/jenkins-2-rhel7:v3.11.59-2
	Image IDs: docker.io/openshift/jenkins-2-centos7@sha256:ab312bda324
	Image streams: Image streams do not contain actual image data, but present a single virtual view of related images
## Create Image Stream
	oc import-image myis --confirm --from registry.access.redhat.com/openjdk/openjdk-11-rhel7 --insecure
	oc new-app -i myis https://github.com/dhui808/userreg  --strategy=source
	oc delete is myis
	oc new-app command and templates also create image stream
## Working with image streams
	Getting information about image streams
		oc describe is/python
		oc describe istag/python:latest
	Adding tags to an image stream
		oc tag <source> <destination>
		oc tag python:3.5 python:latest
	Adding tags for an external image
		oc tag docker.io/python:3.6.0 python:3.6
	Updating image stream tags
		oc tag python:3.6 python:latest
	Removing image stream tags
		oc tag -d python:3.5
## Using image pull secrets
	Allowing pods to reference images across projects
		To allow pods in project-a to reference images in project-b, bind a service account in project-a to the 
		system:image-puller role in project-b:
		oc policy add-role-to-user system:image-puller system:serviceaccount:project-a:default --namespace=project-b
		
		To allow access for any service account in project-a, use the group:
		oc policy add-role-to-group system:image-puller system:serviceaccounts:project-a --namespace=project-b
	Allowing pods to reference images from other secured registries
		oc create secret generic <pull_secret_name> --from-file=.dockercfg=<path/to/.dockercfg> \
		--type=kubernetes.io/dockercfg
		oc create secret docker-registry <pull_secret_name> --docker-server=<registry_server> \
		--docker-username=<user_name> --docker-password=<password> --docker-email=<email>
## Triggering updates on image stream changes
  	Use annotations request triggering.
		Key: image.openshift.io/triggers
		Value:
		[
		 {
		   "from": {
		     "kind": "ImageStreamTag", 
		     "name": "example:latest", 
		     "namespace": "myapp" 
		   },
		   "fieldPath": "spec.template.spec.containers[?(@.name==\"web\")].image", 
		   "paused": "false" 
		 },
		 ...
		]
	Setting the image trigger on Kubernetes resources
		oc set triggers deploy/example --from-image=example:latest -c web
		This cmd adds an image change trigger to the deployment named example so that when the example:latest 
		image stream tag is updated, the web container inside the deployment updates with the new image value.
		
