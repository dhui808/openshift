## Work with configuration maps
	Create configuration maps
	Use configuration maps to inject data into applications
  
## Creating and using config maps
	Understanding ConfigMaps
		A config map can be used to:
			- Populate environment variable values in containers
			- Set command-line arguments in a container
			- Populate configuration files in a volume

	Creating a config map in the OpenShift Container Platform web console
	Creating a config map
		Creating a config map from a directory
			oc create configmap game-config --from-file=example-files/
			oc describe configmap game-config
			oc get configmaps game-config -o yaml
		Creating a ConfigMap from a file
			oc create configmap game-config-2 --from-file=example-files/game.properties --from-file=example-files/ui.properties
			oc create configmap game-config-3 --from-file=game-special-key=example-files/game.properties
		Creating a config map from literal values
			oc create configmap special-config --from-literal=special.how=very --from-literal=special.type=charm
	Use Cases: Consuming ConfigMaps in pods
		Populating environment variables in containers by using config maps
			apiVersion: v1
			kind: Pod
			metadata:
				name: dapi-test-pod
			spec:
				containers:
					- name: test-container
						image: gcr.io/google_containers/busybox
						command: [ "/bin/sh", "-c", "env" ]
						env: 
							- name: SPECIAL_LEVEL_KEY 
								valueFrom:
									configMapKeyRef:
										name: special-config 
										key: special.how 
							- name: SPECIAL_TYPE_KEY
								valueFrom:
									configMapKeyRef:
										name: special-config 
										key: special.type 
										optional: true 
						envFrom: 
							- configMapRef:
									name: env-config 
				restartPolicy: Never
	
		Setting command-line arguments for container commands with ConfigMaps
			apiVersion: v1
			kind: Pod
			metadata:
				name: dapi-test-pod
			spec:
				containers:
					- name: test-container
						image: gcr.io/google_containers/busybox
						command: [ "/bin/sh", "-c", "echo $(SPECIAL_LEVEL_KEY) $(SPECIAL_TYPE_KEY)" ] 
						env:
							- name: SPECIAL_LEVEL_KEY
								valueFrom:
									configMapKeyRef:
										name: special-config
										key: special.how
							- name: SPECIAL_TYPE_KEY
								valueFrom:
									configMapKeyRef:
										name: special-config
										key: special.type
				restartPolicy: Never		
		
		Injecting content into a volume by using config maps
			apiVersion: v1
			kind: Pod
			metadata:
				name: dapi-test-pod
			spec:
				containers:
					- name: test-container
						image: gcr.io/google_containers/busybox
						command: [ "/bin/sh", "cat", "/etc/config/special.how" ]
						volumeMounts:
						- name: config-volume
							mountPath: /etc/config
				volumes:
					- name: config-volume
						configMap:
							name: special-config 
				restartPolicy: Never
	
		apiVersion: v1
		kind: Pod
		metadata:
			name: dapi-test-pod
		spec:
			containers:
				- name: test-container
					image: gcr.io/google_containers/busybox
					command: [ "/bin/sh", "cat", "/etc/config/path/to/special-key" ]
					volumeMounts:
					- name: config-volume
						mountPath: /etc/config
			volumes:
				- name: config-volume
					configMap:
						name: special-config
						items:
						- key: special.how
							path: path/to/special-key 
			restartPolicy: Never
