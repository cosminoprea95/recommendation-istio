Steps to use Cloud Build:

	1. Create your own cloudbuild.yml with your build steps. https://cloud.google.com/cloud-build/docs/build-config
	2. If you want to use Kubernetes Engine create your kubernetes.yml file which contains the template you want to deploy.
	3. Connect your repository inside Cloud Build using Connect repository button.
	4. Create a trigger in Cloud Build, specifying the repository, branch.
	5. Commit the code.
