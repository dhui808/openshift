## Work with image streams
	Create custom image streams to deploy applications
	Pull applications from existing Git repositories
	Debug minor issues with application deployment

## Using image streams
	Image streams do not contain actual image data, but present a single virtual view of related images, 
	similar to an image repository.

## Creting image streams
	To create an image stream tag resource for a container image hosted on an external registry, use
	the oc import-image command with both the --confirm and --from options.
		oc import-image myimagestream[:tag] --confirm --from registry/myorg/myimage[:tag]
		oc import-image myis --confirm --from registry.access.redhat.com/openjdk/openjdk-11-rhel7 --insecure
		oc new-app -i myis https://github.com/dhui808/userreg --strategy=source --as-deployment-config
