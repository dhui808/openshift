## Troubleshoot application deployment issues
	Diagnose and correct minor issues with application deployment

	 -f, --follow=false: Specify if the logs should be streamed.
	oc logs -f bc/quotesapi
	oc logs -f dc/quotesapi
	oc logs pod/quotesapi-1-build
	oc logs pod/quotesapi-1-deploy
	oc logs pod/quotesapi-1-rjh6r
	oc get pod --no-headers -o custom-columns=POD:.metadata.name -l deploymentconfig=quotesapi
