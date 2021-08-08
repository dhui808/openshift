## Work with templates
	Use pre-existing templates written in either JSON or YAML format
	Work with multicontainer templates
	Add a custom parameter to a template

https://docs.openshift.com/container-platform/4.6/openshift_images/using-templates.html

## Upload a template JSON or YAML file
	oc create -f <filename> -n <project>
	
	# ImageStream_ruby.yaml is not a template - a concrete ImageStream object is created
	oc create -f ImageStream_ruby.yaml
	imagestream.image.openshift.io/ruby created
	
	# redis-template.yaml is a template
	oc create -f redis-template.yaml
	template.template.openshift.io/redis-template created
	
	oc create -f cakephp-mysql-example.yaml
	template.template.openshift.io/cakephp-mysql-example created
	
## Create an application from a template using web console

## Outputing objects from templates by using the CLI
	Adding labels
	oc process -f <filename> -l name=otherLabel
	oc process -f redis-template.yaml -l name=otherLabel
	
	Listing parameters
	oc process --parameters -f <filename>
	oc process --parameters -n <project> <template_name>
	oc process --parameters -n openshift rails-postgresql-example
	
	oc process --parameters -f redis-template.yaml
	oc process --parameters redis-template
	oc process --parameters -n openshift rails-postgresql-example
	
	Process a file defining a template to return the list of objects to standard output.
	oc process -f <filename>
	oc process <template_name>
	
	oc process -f redis-template.yaml
	oc process redis-template
	
	Create objects from a template by processing the template and piping the output to oc create:
	oc process -f <filename> | oc create -f -
	oc process <template> | oc create -f -
	
	oc process -f my-template-service.yaml | oc create -f -

## Modifying uploaded templates
	oc edit template <template>
	oc edit template cakephp-mysql-example
	oc describe template cakephp-mysql-example
	
## List the available default Instant App and Quickstart templates 
	oc get templates -n openshift
	
## Writing template parameters
	oc create -f my-template-bc.yaml
	template.template.openshift.io/my-template-bc created
	
## Writing the template object list
	oc create -f my-template-service.yaml
	template.template.openshift.io/my-template-service created
	
## Marking a template as bindable
	Prevent end user from binding against services provisioned from a given template by adding the 
	annotation template.openshift.io/bindable: "false" to the template.
	
## Exposing template object fields
	To expose one or more fields of an object, add annotations prefixed by template.openshift.io/expose- 
	or template.openshift.io/base64-expose- to the object in the template.
	oc create -f my-template-expose-fields.yaml
	template.template.openshift.io/my-template-expose-fields created
	
## Waiting for template readiness
	Indicate that certain objects within a template should be waited for before a template instantiation
	metadata:
		annotation:
			"template.alpha.openshift.io/wait-for-ready": "true"

## Creating a template from existing objects
	oc get -o yaml all > <yaml_filename>
	oc get -o yaml all > coffee-shop.yaml
	
