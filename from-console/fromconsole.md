# Deploy Spring Boot Web Application from OpenShift Web Console

Login to OpenShift 4.4

![](media/image1.png)

Create Project

MyProject

![](media/image2.png)

![](media/image3.png)

Obviously, OpenShift requires a project name unique across OpenShift --
this is nonsense. Should be cluster-wide unique only.

MyFirstProject

![](media/image4.png)

![](media/image5.png)

DannyProject

Dashboard

![](media/image6.png)

![](media/image7.png)

Overview

![](media/image8.png)

YAML

![](media/image9.png)

Workloads

![](media/image10.png)

Roles Bindings

![](media/image11.png)

Home -- Projects

![](media/image12.png)

Developer Perspective

![](media/image13.png)

Deploy Spring Boot Web Application

Source:

<https://github.com/dhui808/springbootstarter>

+Add

![](media/image14.png)

From Git

![](media/image15.png)

![](media/image16.png)

Show Advanced Git Options

![](media/image17.png)

Git Repo URL

<https://github.com/dhui808/springbootstarter>

Build Image

Java

![](media/image18.png)

![](media/image19.png)

![](media/image20.png)

Select either Deployment (OpenShift recommendation) or Deployment Config
(allowing adding volume)

Create

![](media/image21.png)

![](media/image22.png)

List View

![](media/image22.png)

![](media/image23.png)

springbootstarter

![](media/image24.png)

View Logs

![](media/image25.png)

Topology -- List View

![](media/image26.png)

![](media/image27.png)

Logs

![](media/image28.png)

From browser:

<http://springbootstarter-dannyproject.apps.us-east-2.starter.openshift-online.com/springbootstarter/hello>

![](media/image29.png)

Voila!
