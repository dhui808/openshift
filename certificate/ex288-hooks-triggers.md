## Work with hooks and triggers
	Create a hook that runs a provided script
	Test and confirm proper operation of the hook
## Deployment Config has a trigger in the image stream. If the image stream changes, then a new deployment is performed.
## Build triggers
	Webhook triggers
	Display webhook URLs: oc describe bc <name>
	Using image change triggers
		1.Define an ImageStream that points to the upstream image you want to trigger on:
			kind: "ImageStream"
			apiVersion: "v1"
			metadata:
			  name: "ruby-20-centos7"
		2.If an image stream is the base image for the build, set the from field in the build strategy to point to the ImageStream:
			strategy:
			  sourceStrategy:
				from:
				  kind: "ImageStreamTag"
				  name: "ruby-20-centos7:latest"
		3.Define a build with one or more triggers that point to ImageStreams:
			type: "ImageChange" 
			imageChange: {}
			type: "ImageChange" 
			imageChange:
			  from:
				kind: "ImageStreamTag"
				name: "custom-image:latest"
	Configuration change triggers
		trigger definition YAML within the BuildConfig:	  type: "ConfigChange"
	Setting triggers manually:
		oc set triggers bc <name> --from-github
		oc set triggers bc <name> --from-image='<image>'
		oc set triggers bc <name> --from-bitbucket --remove
		
## Build hooks allow behavior to be injected into the build process.
	can be used to run unit tests to verify the image before the build is marked complete and the image is made available in a registry
	Configuring post commit build hooks
	Using the CLI to set post commit build hooks
		oc set build-hook bc/mybc --post-commit --command -- bundle exec rake test --verbose
		oc set build-hook bc/mybc --post-commit --script="bundle exec rake test --verbose"
