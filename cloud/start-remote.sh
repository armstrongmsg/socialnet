curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

mkdir target
mv socialnet.war target

sudo docker build -t socialnet:1.0 .
sudo docker run -d --name socialnet -p 80:8080 socialnet:1.0
