## App2Container

### 目录

1. 先决条件
2. 安装App2Container
3. 启动SpringBoot 程序
4. 初始化App2Container
5. Java 程序分析
6. 应用容器化部署
7. 清除环境


### 先决条件

确认已完成以下先决条件：

- 安装`AWS CLI`并在服务器上配置了AWS配置文件。请参阅配置AWS配置文件以了解更多信息。
- 运行容器化和部署步骤的服务器上安装了`Docker`引擎。请参阅安装Docker引擎以获取更多信息。
- 应用程序服务器（和工作计算机，如果使用）上具有`root`用户访问权限。
- 应用程序服务器（和工作计算机，如果使用的话）具有`tar` 和 `20GB` 的空间。
- 应用程序服务器具有`JDK 8`以上版本环境
- 应用程序服务器具有`Apache Maven`环境编译`Java`程序
- 提前准备好`Amazon EKS`测试环境, 并且安装好 `kubectl`工具



### 安装App2Container
- 使用`curl`命令从`Amazon S3`下载`App2Container`安装软件包

	```
	curl -o AWSApp2Container-installer-linux.tar.gz https://app2container-release-us-east-1.s3.us-east-1.amazonaws.com/latest/linux/AWSApp2Container-installer-linux.tar.gz
	```

- 将包解压缩到服务器上的本地文件夹

	```
	sudo tar xvf AWSApp2Container-installer-linux.tar.gz
	```

- 运行从软件包中提取的安装脚本

	```
	sudo ./install.sh
	```
然后按照提示进行操作

	```
	[centos@ip-172-31-31-202 java]$ sudo app2container init
	Workspace directory path for artifacts[default: /root/app2container]: /home/centos/app2container/tool/java
	AWS Profile (configured using 'aws configure --profile')[default: default]: 
	Optional S3 bucket for application artifacts: 
	Report usage metrics to AWS? (Y/N)[default: y]: 
	Require images to be signed using Docker Content Trust (DCT)? (Y/N)[default: n]: 
	Configuration saved
	```
### 启动SpringBoot 程序

- 下载SpringBoot 的示例程序

	```
	git clone https://github.com/nikosheng/App2Container-Springboot-Tutorial.git
	
	cd app2container
	```

- 使用Maven 编译示例程序

	```
	mvn clean package -Dmaven.test.skip=true
	```

- 编译完成后，运行程序

	```
	mvn spring-boot:run 
	```
	这时候可以看到如下运行成功界面
	
	![alt text](https://github.com/nikosheng/App2Container-Springboot-Tutorial/blob/master/img/SpringBoot_Running_Image.png "SpringBoot Running Image")

- 执行`sudo app2container inventory` 运行清单命令以列出服务器上运行的Java应用程序

	```
	[centos@ip-172-31-31-202 java]$ sudo app2container inventory
	{
	                "java-generic-466a359b": {
	                                "processId": 8177,
	                                "cmdline": "/usr/bin/java ... -Dlibrary.jansi.path=/home/centos/bin/apache-maven-3.6.3/lib/jansi-native -Dmaven.multiModuleProjectDirectory=/home/centos/app2container/App2Container-Springboot-Tutorial/app2container org.codehaus.plexus.classworlds.launcher.Launcher spring-boot:run ",
	                                "applicationType": "java-generic"
	                }
	}
```

### Java 程序分析
- 在清单命令的JSON输出中找到要转换的应用程序的应用程序ID，然后按如下所示运行`app2container analyze`命令，将Java-app-id替换为您找到的应用程序ID, 本示例为`java-generic-466a359b`

	```
	sudo app2container analyze --application-id java-generic-466a359b
	```
	
	输出的`analysis.json` 会存储在刚才初始化`app2container`的工作目录中
	
	```
	[centos@ip-172-31-31-202 java]$ sudo app2container analyze --application-id java-generic-466a359b
	✔ Created artifacts folder /home/centos/app2container/tool/java/java-generic-466a359b
	✔ Generated analysis data in /home/centos/app2container/tool/java/java-generic-466a359b/analysis.json
	👍 Analysis successful for application java-generic-466a359b
	
	💡 Next Steps:
	1. View the application analysis file at /home/centos/app2container/tool/java/java-generic-466a359b/analysis.json.
	2. Edit the application analysis file as needed.
	3. Start the containerization process using this command: app2container containerize --application-id java-generic-466a359b
	```
	在`analysis.json` 内，analysisInfo部分标识系统依赖性。您可以定制通过修改`containerParameters`部分生成的容器映像。
	
	```
	{
	"a2CTemplateVersion": "",
       "createdTime": "",
       "containerParameters": {
              "_comment1": "*** EDITABLE: The below section can be edited according to the application requirements. Please see the analysisInfo section below for details discovered regarding the application. ***",
              "imageRepository": "java-generic-466a359b",
              "imageTag": "latest",
              "containerBaseImage": "amazonlinux:2",
              "appExcludedFiles": [],
              "appSpecificFiles": [],
              "applicationMode": true,
              "logLocations": [],
              "enableDynamicLogging": false,
              "dependencies": []
       },
       "analysisInfo": {
              "_comment2": "*** NON-EDITABLE: Analysis Results ***",
              "processId": 2065,
              "appId": "java-tomcat-6e6f3a87",
              "userId": "1000",
              "groupId": "1000",
              "cmdline": [
                     "/usr/bin/java",
                     "... list of commands",
                     "start"
              ],
              "osData": {
                     "BUG_REPORT_URL": "",
                     "HOME_URL": "",
                     "ID": "ubuntu",
                     "ID_LIKE": "debian",
                     "NAME": "Ubuntu",
                     "PRETTY_NAME": "Ubuntu 18.04.2 LTS",
                     "PRIVACY_POLICY_URL": "",
                     "SUPPORT_URL": "",
                     "UBUNTU_CODENAME": "",
                     "VERSION": "",
                     "VERSION_CODENAME": "",
                     "VERSION_ID": "18.04"
              },
              "osName": "ubuntu",
              "ports": [
                     {
                            "localPort": 8080,
                            "protocol": "tcp6"
                     },
                     {
                            "localPort": 8009,
                            "protocol": "tcp6"
                     },
                     {
                            "localPort": 8005,
                            "protocol": "tcp6"
                     }
              ],
              "Properties": {
                     "catalina.base": "... directory location",
                     "catalina.home": "... directory location",
                     "classpath": "... directory location/bin/bootstrap.jar:... etc.",
                     "ignore.endorsed.dirs": "",
                     "java.io.tmpdir": "... directory location/temp",
                     "java.protocol.handler.pkgs": "org.apache.catalina.webresources",
                     "java.util.logging.config.file": "... directory location/conf/logging.properties",
                     "java.util.logging.manager": "org.apache.juli.ClassLoaderLogManager",
                     "jdk.tls.ephemeralDHKeySize": "2048",
                     "jdkVersion": "11.0.7",
                     "org.apache.catalina.security.SecurityListener.UMASK": ""
              },
              "AdvancedAppInfo": {
                     "Directories": {
                            "base": "... directory location",
                            "bin": "... directory location/bin",
                            "conf": "... directory location/conf",
                            "home": "... directory location",
                            "lib": "... directory location/lib",
                            "logConfig": "... directory location/conf/logging.properties",
                            "logs": "... directory location/logs",
                            "tempDir": "... directory location/temp",
                            "webapps": "... directory location/webapps",
                            "work": "... directory location/work"
                     },
                     "distro": "java-tomee",
                     "flavor": "plume",
                     "jdkVersion": "11.0.7",
                     "version": "8.0.0"
              },
              "env": {
                     "HOME": "... Java Home directory",
                     "JDK_JAVA_OPTIONS": "",
                     "LANG": "C.UTF-8",
                     "LC_TERMINAL": "iTerm2",
                     "LC_TERMINAL_VERSION": "3.3.11",
                     "LESSCLOSE": "/usr/bin/lesspipe %s %s",
                     "LESSOPEN": "| /usr/bin/lesspipe %s",
                     "LOGNAME": "ubuntu",
                     "LS_COLORS": "",
                     "MAIL": "",
                     "OLDPWD": "",
                     "PATH": "... server PATH",
                     "PWD": "",
                     "SHELL": "/bin/bash",
                     "SHLVL": "1",
                     "SSH_CLIENT": "",
                     "SSH_CONNECTION": "",
                     "SSH_TTY": "",
                     "TERM": "",
                     "USER": "ubuntu",
                     "XDG_DATA_DIRS": "",
                     "XDG_RUNTIME_DIR": "",
                     "XDG_SESSION_ID": "1",
                     "_": "bin/startup.sh"
              },
              "cwd": "",
              "procUID": {
                     "euid": "1000",
                     "suid": "1000",
                     "fsuid": "1000",
                     "ruid": "1000"
              },
              "procGID": {
                     "egid": "1000",
                     "sgid": "1000",
                     "fsgid": "1000",
                     "rgid": "1000"
              },
              "userNames": {
                     "1000": "ubuntu"
              },
              "groupNames": {
                     "1000": "ubuntu"
              },
              "fileDescriptors": [
                     "... directory location/logs/... log files",
                     "... directory location/lib/... jar files",
                     "... etc.",
                     "/usr/lib/jvm/.../lib/modules"
              ],
              "dependencies": {}
          }
	}
	```
	
### 应用容器化部署

- 在修改完 `analysis.json` 后，可以通过 `app2container containerize` 来进行容器化的转换。
	
	
	```
	[centos@ip-172-31-31-202 .aws]$ sudo app2container containerize --application-id java-generic-466a359b
	✔ AWS prerequisite check succeeded
	✔ Docker prerequisite check succeeded
	✔ Extracted container artifacts for application
	✔ Entry file generated
	✔ Dockerfile generated under /home/centos/app2container/tool/java/java-generic-466a359b/Artifacts
	✔ Generated dockerfile.update under /home/centos/app2container/tool/java/java-generic-466a359b/Artifacts
	✔ Generated deployment file at /home/centos/app2container/tool/java/java-generic-466a359b/deployment.json
	👍 Containerization successful. Generated docker image java-generic-466a359b
	
	💡 You're all set to test and deploy your container image.
	
	Next Steps:
	1. View the container image with "docker images" and test the application.
	2. When you're ready to deploy to AWS, please edit the deployment file as needed at /home/centos/app2container/tool/java/java-generic-466a359b/deployment.json.
	3. Generate deployment artifacts using "app2container generate app-deployment --application-id java-generic-466a359b"
	```
	
	执行结束后，会在本地的`Docker`环境下生成`Springboot`应用的容器镜像。您可以使用`docker run`命令启动容器并测试应用程序功能。
	
	```
	[centos@ip-172-31-31-202 .aws]$ docker images | grep java-generic-466a359b
java-generic-466a359b                                            latest              3c526048c504        2 minutes ago       11.5 GB
	```

	除了生成容器映像之外，`app2container containerize`命令还会生成一个`deploy.json`模板文件. 我们可以在deploy.json模板文件中编辑参数，以更改要在Amazon ECR中注册的映像存储库名称，ECS任务定义参数或Kubernetes应用程序名称。由于本次的测试程序需要暴露`8080`端口，因此在`exposedPorts`项内需要添加暴露的端口和协议。
	
	默认的`deployment.json` 文件会默认选择`Amazon ECS`作为生成项目，由于本次实验我们是针对`Amazon EKS`进行实验，所以我们需要在文件中修改`ecsParameters`下的`createEcsArtifacts`为`false`，同时修改`eksParameters`下的`createEksArtifacts为true`，代表执行`app2container generate app-deployment`的时候会以`EKS`为目标。
	
	```
		{
	       "a2CTemplateVersion": "1.0",
	       "applicationId": "java-generic-0a2e2329",
	       "imageName": "java-generic-0a2e2329",
	       "exposedPorts": [{
	                     "localPort": 8080,
	                     "protocol": "tcp6"
	              }],
	       "environment": [],
	       "ecrParameters": {
	              "ecrRepoTag": "latest"
	       },
	       "ecsParameters": {
	              "createEcsArtifacts": false,
	              "ecsFamily": "java-generic-0a2e2329",
	              "cpu": 2,
	              "memory": 4096,
	              "dockerSecurityOption": "",
	              "enableCloudwatchLogging": false,
	              "publicApp": true,
	              "stackName": "a2c-java-generic-0a2e2329-ECS",
	              "reuseResources": {
	                     "vpcId": "",
	                     "cfnStackName": "",
	                     "sshKeyPairName": ""
	              },
	              "gMSAParameters": {
	                     "domainSecretsArn": "",
	                     "domainDNSName": "",
	                     "domainNetBIOSName": "",
	                     "createGMSA": false,
	                     "gMSAName": ""
	              }
	       },
	       "eksParameters": {
	              "createEksArtifacts": true,
	              "stackName": "a2c-java-generic-0a2e2329-EKS",
	              "reuseResources": {
	                     "vpcId": "",
	                     "cfnStackName": "",
	                     "sshKeyPairName": ""
	              }
	       }
	}
	```
	- 执行`app2container generate app-deployment` 生成`Kubernetes` 的 `deployment` 和 `service` YML文件，可提供后续在`EKS`集群部署

	```
	[root@ip-192-168-0-17 java-generic-0a2e2329]# app2container generate app-deployment --application-id java-generic-0a2e2329
	✔ AWS prerequisite check succeeded
	✔ Docker prerequisite check succeeded
	✔ Created ECR Repository
	✔ Uploaded CloudFormation resources to S3 Bucket: aws-nikofeng-ap-southeast-1-app2container
	✔ Generated CloudFormation Master template at: /root/app2container/java-generic-0a2e2329/EksDeployment/amazon-eks-master.template.yaml
	👍 EKS CloudFormation templates and additional deployment artifacts generated successfully for application java-generic-0a2e2329
	
	💡 You're all set to use AWS CloudFormation to manage your application stack.You're all set to use AWS CloudFormation to manage your application stack.
	
	Next Steps:
	1. Edit the CloudFormation template as necessary.
	2. Create an application stack using the AWS CLI or the AWS Console. AWS CLI command:
	
		aws cloudformation deploy --template-file /root/app2container/java-generic-0a2e2329/EksDeployment/amazon-eks-master.template.yaml --capabilities CAPABILITY_NAMED_IAM --stack-name a2c-java-generic-0a2e2329-EKS
	
	3. Setup a pipeline for your application stack using app2container:
	
		app2container generate pipeline --application-id java-generic-0a2e2329
	```
	
	执行成功后，我们可以在工作目录下能找到具体的`deployment`和`service`执行YML文件，然后我们可以通过`kubectl apply`来执行部署操作。
	
	```
	kubectl apply -f eks_deplyoment.yaml
	kubectl apply -f eks_service.yaml
	```
	
	通过kubectl 观察deployment 和 service的部署状态
	
	```
	[centos@ip-172-31-31-202 eks]$ kubectl get po,svc
	NAME                                                    READY   STATUS    RESTARTS   AGE
	pod/java-generic-0a2e2329-deployment-68dd67f9f4-zsfn9   1/1     Running   0          99m
	
	NAME                                    TYPE           CLUSTER-IP      EXTERNAL-IP                                                                    PORT(S)          AGE
	service/java-generic-0a2e2329-service   LoadBalancer   10.100.215.53   a86b2855bb4364daf8d2d065af68694b-1520529547.ap-southeast-1.elb.amazonaws.com   8080:30209/TCP   95m
	service/kubernetes                      ClusterIP      10.100.0.1      <none>                                                                         443/TCP          29d
	```
	通过LoadBalancer的访问查看结果
	
	```
	[centos@ip-172-31-31-202 eks]$ curl a86b2855bb4364daf8d2d065af68694b-1520529547.ap-southeast-1.elb.amazonaws.com:8080
	
	Hello Spring v1
	```
	
### 清除环境

- 清除应用环境

	```
	kubectl delete -f eks_deplyoment.yaml
	kubectl delete -f eks_service.yaml
	docker image rm <image id>
	```

- 清除应用环境

	清除本次实验中生成的所有目录文件
