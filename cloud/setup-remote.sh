scp -i $VM_KEY ../target/socialnet.war ubuntu@$VM_IP:/home/ubuntu
scp -i $VM_KEY ../Dockerfile ubuntu@$VM_IP:/home/ubuntu
scp -i $VM_KEY start-remote.sh ubuntu@$VM_IP:/home/ubuntu
