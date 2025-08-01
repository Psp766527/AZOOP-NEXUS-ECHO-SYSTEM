# Backend Kubernetes

## One-time installation

The persistent volume to mount the storage account file share is a one-time installation and is required to be done before any per-build installation

fill in the variables for azurevolume.yaml.ftl and then apply it onto the cluster 

    AZURE_FILE_SECRET_NAME=<secret name> AZURE_FILE_SHARE_NAME=<file share name> envsubst < azurevolume.yaml.ftl | kubectl apply -f -
